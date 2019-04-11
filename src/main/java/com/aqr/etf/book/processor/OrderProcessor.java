package com.aqr.etf.book.processor;

import com.aqr.etf.book.exception.OrderException;
import com.aqr.etf.book.model.IModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import rx.subjects.ReplaySubject;

import javax.annotation.PostConstruct;

@Log4j2
@Component
public class OrderProcessor implements ILoader {

    private final ReplaySubject<IModel> newOrderReplaySubject;
    private final ReplaySubject<IModel> modifyOrderReplaySubject;
    private final ReplaySubject<IModel> cancelOrderReplaySubject;
    private final OrderProcessorFacade orderProcessorFacade;

    @Autowired
    public OrderProcessor(
            @Qualifier("newOrderReplaySubject")
            final ReplaySubject<IModel> newOrderReplaySubject,
            @Qualifier("modifyOrderReplaySubject")
            final ReplaySubject<IModel> modifyOrderReplaySubject,
            @Qualifier("cancelOrderReplaySubject")
            final ReplaySubject<IModel> cancelOrderReplaySubject,
            final OrderProcessorFacade orderProcessorFacade) {

        this.newOrderReplaySubject     = newOrderReplaySubject;
        this.modifyOrderReplaySubject  = modifyOrderReplaySubject;
        this.cancelOrderReplaySubject  = cancelOrderReplaySubject;
        this.orderProcessorFacade       = orderProcessorFacade;
    }

    @PostConstruct
    public void processNewOrder() {
        newOrderReplaySubject.subscribe(
                newOrder -> {
                    orderProcessorFacade.processNewOrder(newOrder);} ,
                (Throwable ex) -> {
                    new OrderException("Error Processing New Order");
                    log.warn(ex.getMessage());
                    ex.printStackTrace();
                }
        );
    }

    @PostConstruct
    public void processModifiedOrder() {
        modifyOrderReplaySubject.subscribe(
                modifyOrder -> orderProcessorFacade.processModifiedOrder(modifyOrder),
                (Throwable ex) -> {
                    new OrderException("Error Processing Modify Order");
                    log.warn(ex.getMessage());
                    ex.printStackTrace();
                }
        );
    }

    @PostConstruct
    public void processCancelOrder() {
        cancelOrderReplaySubject.subscribe(
                cancelOrder -> orderProcessorFacade.processCancelOrder(cancelOrder),
                (Throwable ex) -> {
                    new OrderException("Error Processing Cancel Order");
                    log.warn(ex.getMessage());
                    ex.printStackTrace();
                }
        );
    }
}
