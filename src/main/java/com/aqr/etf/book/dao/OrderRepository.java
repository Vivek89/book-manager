package com.aqr.etf.book.dao;

import com.aqr.etf.book.model.OrderBook;
import com.aqr.etf.book.model.Side;
import com.aqr.etf.book.model.Symbol;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends CrudRepository<OrderBook, UUID> {

    List<OrderBook> findBySymbolAndLimitPriceAndSide(Symbol symbol, Double limitPrice, Side side);

}
