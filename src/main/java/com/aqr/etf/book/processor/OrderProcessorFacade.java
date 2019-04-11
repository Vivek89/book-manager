package com.aqr.etf.book.processor;

import com.aqr.etf.book.model.IModel;
import com.aqr.etf.book.model.OrderBook;
import com.aqr.etf.book.model.Side;
import com.aqr.etf.book.model.Symbol;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.IntStream;

@Log4j2
@Component
public class OrderProcessorFacade {

    private static final int LEVEL_SIZE = 5;
    private final Map<Symbol, List> buyMap;
    private final Map<Symbol, List> sellMap;
    private final Map<Symbol, Double> buyThresholdPrice;
    private final Map<Symbol, Double> sellThresholdPrice;

    @Autowired
    public OrderProcessorFacade(@Qualifier("buyMap")
                                    final Map<Symbol, List> buyMap,
                                @Qualifier("sellMap")
                                final Map<Symbol, List> sellMap,
                                @Qualifier("buyThresholdMap")
                                    final Map<Symbol, Double> buyThresholdPrice,
                                @Qualifier("sellThresholdMap")
                                    final Map<Symbol, Double> sellThresholdPrice) {
        this.buyMap = buyMap;
        this.sellMap = sellMap;
        this.buyThresholdPrice = buyThresholdPrice;
        this.sellThresholdPrice = sellThresholdPrice;
    }


    public void processNewOrder(IModel newOrder) {
        OrderBook order = (OrderBook) newOrder;

        // BUY SIDE
        if (order.getSide() == Side.BUY) {

            List<OrderBook> buyList = buyMap.get(order.getSymbol());

            if (buyList == null) {
                createNewOrderList(order);
                return;
            } else if (buyList.size() < LEVEL_SIZE
                    || order.getLimitPrice() >= buyThresholdPrice.get(order.getSymbol())) {
                updateNewOrderList(order);
                return;
            } else return; // Do Nothing: These price are not in the Top 5 Levels
        }

        // SELL SIDE
        else {
            List<OrderBook> sellList = sellMap.get(order.getSymbol());

            if (sellList == null) {
                createNewOrderList(order);
                return;
            } else if (sellList.size() < LEVEL_SIZE
                    || order.getLimitPrice() <= sellThresholdPrice.get(order.getSymbol())) {
                updateNewOrderList(order);
                return;
            } else return; // Do Nothing: These price are not in the Top 5 Levels
        }

    }


    public void processModifiedOrder(IModel modifyOrder) {
        OrderBook order = (OrderBook) modifyOrder;
        updateModifyOrderList(order);
    }


    public void processCancelOrder(IModel cancelOrder) {

    }


    private void createNewOrderList(OrderBook order) {
        List<OrderBook> orderList;
        orderList = new ArrayList<>(LEVEL_SIZE);

        orderList.add(order);

        if(order.getSide() == Side.BUY) {
            buyThresholdPrice.put(order.getSymbol(), order.getLimitPrice());
            buyMap.put(order.getSymbol(), orderList);
        }
        else {
            sellThresholdPrice.put(order.getSymbol(), order.getLimitPrice());
            sellMap.put(order.getSymbol(), orderList);
        }
    }


    private void updateNewOrderList(OrderBook order) {
        List<OrderBook> orderList;

        if(order.getSide() == Side.BUY) {
            orderList = buyMap.get(order.getSymbol());
        } else {
            orderList = sellMap.get(order.getSymbol());
        }

        // Find index of matching price
        OptionalInt index = IntStream.range(0, orderList.size())
                .filter(i -> orderList.get(i).getLimitPrice().equals(order.getLimitPrice()))
                .findFirst();

        // If Matching Price Exist
        if (index.isPresent()) {

            OrderBook matchingPriceOrder = orderList.get(index.getAsInt());
            OrderBook aggPriceOrders = new OrderBook(order.getOrderId(),
                    order.getSymbol(),
                    order.getLimitPrice(),
                    order.getSide(),
                    order.getQuantity() + matchingPriceOrder.getQuantity(),
                    null);

            // replace order model with updated qty
            log.info(String.format("Updating a Level %d %s order for %s",
                    index.getAsInt(),
                    order.getSide().name(),
                    order.getSymbol().name()));
            orderList.set(index.getAsInt(), aggPriceOrders);

        } else {
            // otherwise just add new order to list
            if (orderList.size() < LEVEL_SIZE) {
                orderList.add(order);
            } else {
                // Add new order at last index, to remove the old Level-4
                orderList.set(LEVEL_SIZE - 1, order);
            }
        }

        // sort of direct retrieval and update Threshold and Map of Symbol:List<OrderBook>
        if(order.getSide() == Side.BUY) {
            Collections.sort(orderList);
            buyThresholdPrice.put(order.getSymbol(),
                    orderList.get(orderList.size() -1).getLimitPrice());
            buyMap.put(order.getSymbol(), orderList);
        } else {
            Collections.sort(orderList, ascendingComparator);
            sellThresholdPrice.put(order.getSymbol(),
                    orderList.get(orderList.size() -1).getLimitPrice());
            sellMap.put(order.getSymbol(), orderList);
        }
    }


    private void updateModifyOrderList(OrderBook order) {
        List<OrderBook> orderList;
        Double thresholdPrice;
        if(order.getSide() == Side.BUY) {
            orderList = buyMap.get(order.getSymbol());
            thresholdPrice = buyThresholdPrice.get(order.getSymbol());

            if(order.getLimitPrice() > thresholdPrice) {
                orderList = checkModifyOrderMatch(orderList, order);
                Collections.sort(orderList);
                buyThresholdPrice.put(order.getSymbol(),
                        orderList.get(orderList.size() -1).getLimitPrice());
                buyMap.put(order.getSymbol(), orderList);
            }
        } else {
            orderList = sellMap.get(order.getSymbol());
            thresholdPrice = sellThresholdPrice.get(order.getSymbol());

            if(order.getLimitPrice() < thresholdPrice) {
                orderList = checkModifyOrderMatch(orderList, order);
                Collections.sort(orderList, ascendingComparator);
                sellThresholdPrice.put(order.getSymbol(),
                        orderList.get(orderList.size() -1).getLimitPrice());
                sellMap.put(order.getSymbol(), orderList);
            }
        }
    }

    public List<OrderBook>  checkModifyOrderMatch(List<OrderBook> orderList, OrderBook order) {
        // Find index of matching price
        OptionalInt index = IntStream.range(0, orderList.size())
                .filter(i -> orderList.get(i).getLimitPrice().equals(order.getLimitPrice()))
                .findFirst();

        // If Matching Price Exist
        if (index.isPresent()) {

            OrderBook matchingPriceOrder = orderList.get(index.getAsInt());
            OrderBook aggPriceOrders = new OrderBook(order.getOrderId(),
                    order.getSymbol(),
                    order.getLimitPrice(),
                    order.getSide(),
                    order.getChangeInQuantity() + matchingPriceOrder.getQuantity(),
                    null);

            // replace order model with updated qty
            log.info(String.format("Modifying a Level %d %s order for %s",
                    index.getAsInt(),
                    order.getSide().name(),
                    order.getSymbol().name()));
            orderList.set(index.getAsInt(), aggPriceOrders);
            return orderList;

        } else {
            // Update the order at last element in the list
            orderList.set(LEVEL_SIZE - 1, order);
            return orderList;
        }
    }



    // Ascending Compare
    private Comparator<OrderBook> ascendingComparator = (o1, o2) -> {
        if (o1.getLimitPrice() > o2.getLimitPrice())
            return 1;
        else return -1;
    };


}
