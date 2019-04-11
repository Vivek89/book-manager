package com.aqr.etf.book.processor;

public interface ILoader {

    void processNewOrder();
    void processModifiedOrder();
    void processCancelOrder();
}
