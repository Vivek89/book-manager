package com.aqr.etf.book.generator;

import com.aqr.etf.book.dao.OrderRepository;
import com.aqr.etf.book.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.IdGenerator;
import rx.subjects.PublishSubject;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Component
@EnableScheduling
public class GenearteQuotes {

    private final static Logger LOG =
            Logger.getLogger(GenearteQuotes.class.getName());

    private final IdGenerator idGenerator;
    private final List<UUID> orderIdList;
    private final Random randomNumber;
    private final PublishSubject<IModel> newOrderPublishSubject;
    private final PublishSubject<IModel> modifyOrderPublishSubject;
    private final PublishSubject<IModel> cancelOrderPublishSubject;
    private final OrderRepository orderRepository;

    @Autowired
    public GenearteQuotes(final IdGenerator idGenerator,
                          final List<UUID> orderIdList,
                          final Random randomNumber,
                          @Qualifier("newOrderPublishSubject") final PublishSubject<IModel> newOrderPublishSubject,
                          @Qualifier("modifyOrderPublishSubject") final PublishSubject<IModel> modifyOrderPublishSubject,
                          @Qualifier("cancelOrderPublishSubject") final PublishSubject<IModel> cancelOrderPublishSubject,
                          final OrderRepository orderRepository) {

        this.idGenerator = idGenerator;
        this.orderIdList = orderIdList;
        this.randomNumber = randomNumber;
        this.newOrderPublishSubject = newOrderPublishSubject;
        this.modifyOrderPublishSubject = modifyOrderPublishSubject;
        this.cancelOrderPublishSubject = cancelOrderPublishSubject;
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

                    LOG.info(topBook.toString());
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

                    this.newOrderPublishSubject.onNext(newOrder);
                    this.orderRepository.save(newOrder);
//                    LOG.info(newOrder.toString());
                });
            });
        });
    }


    @Scheduled(cron = "${generator.modify-order.schedule}")
    public void generateModifyOrder() {

        for (int i = 0; i < 10; i++) {

            UUID id = orderIdList.get(randomNumber.nextInt(orderIdList.size()));
            Optional<OrderBook> order = this.orderRepository.findById(id);
            Long newQuantity = Math.round(generateRandomBetween(100, 10000));
            Long changeInQuantity = order.isPresent()? newQuantity - order.get().getQuantity() : null;

            OrderBook modifyOrder = new OrderBook(
                    id,
                    null,
                    null,
                    null,
                    newQuantity,
                    changeInQuantity);

            this.modifyOrderPublishSubject.onNext(modifyOrder);
            this.orderRepository.deleteById(id);
            this.orderRepository.save(modifyOrder);

            LOG.info(modifyOrder.toString() + " - ID: " + modifyOrder.getOrderId());
        }
    }

    @Scheduled(cron = "${generator.cancel-order.schedule}")
    public void generateCancelOrder() {

        for (int i = 0; i < 5; i++) {
            UUID id = orderIdList.get(randomNumber.nextInt(orderIdList.size()));
            Optional<OrderBook> order = this.orderRepository.findById(id);
            Long changeInQuantity = order.isPresent()? - order.get().getQuantity() : null;
            OrderBook cancelOrder = new OrderBook(
                    id,
                    null,
                    null,
                    null,
                    null,
                    changeInQuantity);

            this.cancelOrderPublishSubject.onNext(cancelOrder);
            this.orderRepository.deleteById(id);
            LOG.info(cancelOrder.toString());
        }
    }


}
