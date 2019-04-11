package com.aqr.etf.book.generator;

import com.aqr.etf.book.dao.OrderRepository;
import com.aqr.etf.book.model.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.IdGenerator;
import rx.subjects.ReplaySubject;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;


/**
 * This is a sample class that mimics tickers coming from different exchanges.
 * Observer Pattern using RXJava ReplaySubject: Once the tickers are generated,
 *          they are put to a subject, which is observed by application processor.
 * This is also responsible for persisting Model - OrderModel into in-memory DB.
 * DB used: Apache Derby
 * Persistence used: Spring JPA
 */
@Component
@EnableScheduling
@Log4j2
public class GenearteQuotes {

    private final IdGenerator idGenerator;
    private final List<UUID> orderIdList;
    private final Random randomNumber;
    private final ReplaySubject<IModel> newOrderReplaySubject;
    private final ReplaySubject<IModel> modifyOrderReplaySubject;
    private final ReplaySubject<IModel> cancelOrderReplaySubject;
    private final OrderRepository orderRepository;

    @Autowired
    public GenearteQuotes(final IdGenerator idGenerator,
                          final List<UUID> orderIdList,
                          final Random randomNumber,
                          @Qualifier("newOrderReplaySubject") final ReplaySubject<IModel> newOrderReplaySubject,
                          @Qualifier("modifyOrderReplaySubject") final ReplaySubject<IModel> modifyOrderReplaySubject,
                          @Qualifier("cancelOrderReplaySubject") final ReplaySubject<IModel> cancelOrderReplaySubject,
                          final OrderRepository orderRepository) {

        this.idGenerator = idGenerator;
        this.orderIdList = orderIdList;
        this.randomNumber = randomNumber;
        this.newOrderReplaySubject = newOrderReplaySubject;
        this.modifyOrderReplaySubject = modifyOrderReplaySubject;
        this.cancelOrderReplaySubject = cancelOrderReplaySubject;
        this.orderRepository = orderRepository;
    }

    private static Double generateRandomBetween(int min, int max) {
        return Math.round((min + (Math.random() * ((max - min) + 1))) * 10.0) / 10.0;
    }

    @Scheduled(cron = "${generator.top-of-book.schedule}")
    public void generateTopBookQuotes() {
        Stream.of(Exchange.values()).forEachOrdered(exchange1 -> {
            Stream.of(Symbol.values()).forEachOrdered(sym -> {
                Stream.of(Side.values()).forEachOrdered(side -> {

                    TopBook topBook = new TopBook(sym,
                            generateRandomBetween(sym.getIndex(), sym.getIndex() + 2),
                            Math.round(generateRandomBetween(100, 1000)),
                            generateRandomBetween(sym.getIndex(), sym.getIndex() + 2),
                            Math.round(generateRandomBetween(100, 1000)));

                    log.info(topBook.toString());
                });
            });
        });
    }

    @PostConstruct
    @Scheduled(cron = "${generator.new-order.schedule}")
    public void generateNewOrders() {
        Stream.of(Exchange.values()).forEachOrdered(exchange1 -> {
            Stream.of(Symbol.values()).forEachOrdered(sym -> {
                Stream.of(Side.values()).forEachOrdered(side -> {

                    UUID id = idGenerator.generateId();
                    orderIdList.add(id);

                    OrderBook newOrder = new OrderBook(
                            id,
                            sym,
                            generateRandomBetween(sym.getIndex(), sym.getIndex() + 2),
                            side,
                            Math.round(generateRandomBetween(100, 10000)),
                            null);

                    this.newOrderReplaySubject.onNext(newOrder);
                    this.orderRepository.save(newOrder);
//                    log.info(newOrder.toString());
                });
            });
        });
    }


    @Scheduled(cron = "${generator.modify-order.schedule}")
    public void generateModifyOrder() {

        for (int i = 0; i < 20; i++) {

            UUID id = orderIdList.get(randomNumber.nextInt(orderIdList.size()));
            Optional<OrderBook> order = this.orderRepository.findById(id);
            Long newQuantity = Math.round(generateRandomBetween(100, 10000));
            Long changeInQuantity = order.isPresent()? newQuantity - order.get().getQuantity() : null;

            Optional<OrderBook> checkOrder = this.orderRepository.findById(id);
            if (checkOrder.isPresent()) {
                OrderBook modifyOrder = new OrderBook(
                        id,
                        checkOrder.get().getSymbol(),
                        checkOrder.get().getLimitPrice(),
                        checkOrder.get().getSide(),
                        newQuantity,
                        changeInQuantity);
                this.modifyOrderReplaySubject.onNext(modifyOrder);
                this.orderRepository.deleteById(id);
                this.orderRepository.save(modifyOrder);
//                log.info(modifyOrder.toString() + " - ID: " + modifyOrder.getOrderId());
            }

        }
    }

    @Scheduled(cron = "${generator.cancel-order.schedule}")
    public void generateCancelOrder() {

        for (int i = 0; i < 5; i++) {
            UUID id = orderIdList.get(randomNumber.nextInt(orderIdList.size()));
            Optional<OrderBook> order = this.orderRepository.findById(id);
            Long changeInQuantity = order.isPresent()? - order.get().getQuantity() : null;

            Optional<OrderBook> checkOrder = this.orderRepository.findById(id);
            if (checkOrder.isPresent()) {
                OrderBook cancelOrder = new OrderBook(
                        id,
                        checkOrder.get().getSymbol(),
                        checkOrder.get().getLimitPrice(),
                        checkOrder.get().getSide(),
                        checkOrder.get().getQuantity(),
                        changeInQuantity);

                this.cancelOrderReplaySubject.onNext(cancelOrder);
                this.orderRepository.deleteById(id);
//                log.info(cancelOrder.toString());
            }

        }
    }


}
