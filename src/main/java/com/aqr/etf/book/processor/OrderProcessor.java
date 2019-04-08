package com.aqr.etf.book.processor;

import com.aqr.etf.book.exception.OrderException;
import com.aqr.etf.book.model.IModel;
import com.aqr.etf.book.model.OrderBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import rx.subjects.PublishSubject;

import javax.annotation.PostConstruct;
import java.util.logging.Logger;


@Component
public class OrderProcessor implements ILoader {

    private final static Logger LOG =
            Logger.getLogger(OrderProcessor.class.getName());
    private final PublishSubject<IModel> newOrderPublishSubject;
    private final PublishSubject<IModel> modifyOrderPublishSubject;
    private final PublishSubject<IModel> cancelOrderPublishSubject;
    private final OrderProcessorFacade orderProcessorFacade;

    @Autowired
    public OrderProcessor(
            @Qualifier("newOrderPublishSubject")
            final PublishSubject<IModel> newOrderPublishSubject,
            @Qualifier("modifyOrderPublishSubject")
            final PublishSubject<IModel> modifyOrderPublishSubject,
            @Qualifier("cancelOrderPublishSubject")
            final PublishSubject<IModel> cancelOrderPublishSubject,
            final OrderProcessorFacade orderProcessorFacade) {

        this.newOrderPublishSubject     = newOrderPublishSubject;
        this.modifyOrderPublishSubject  = modifyOrderPublishSubject;
        this.cancelOrderPublishSubject  = cancelOrderPublishSubject;
        this.orderProcessorFacade       = orderProcessorFacade;
    }

    @PostConstruct
    public void processNewOrder() {
        newOrderPublishSubject.subscribe(
                newOrder -> {
                    orderProcessorFacade.processNewOrder(newOrder);},
                (Throwable ex) -> new OrderException("Error Processing New Order"),
                () -> {}    // do nothing on completion
        );
    }

    @PostConstruct
    public void processModifiedOrder() {
        modifyOrderPublishSubject.subscribe(
                modifyOrder -> orderProcessorFacade.processModifiedOrder(modifyOrder),
                (Throwable ex) -> new OrderException("Error Processing Modify Order"),
                () -> {}
        );
    }

    @PostConstruct
    public void processCancelOrder() {
        cancelOrderPublishSubject.subscribe(
                cancelOrder -> orderProcessorFacade.processCancelOrder(cancelOrder),
                (Throwable ex) -> new OrderException("Error Processing Cancel Order"),
                () -> {}
        );
    }
}
