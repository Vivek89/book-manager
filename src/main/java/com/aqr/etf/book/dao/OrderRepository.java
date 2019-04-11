package com.aqr.etf.book.dao;

import com.aqr.etf.book.model.OrderBook;
import com.aqr.etf.book.model.Side;
import com.aqr.etf.book.model.Symbol;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends CrudRepository<OrderBook, UUID> {

    List<OrderBook> findBySymbolAndLimitPriceAndSide(Symbol symbol, Double limitPrice, Side side);
    List<OrderBook> findBySymbol(Symbol symbol);

    @Query("select o from order_book o where o.limitPrice < ?1" +
            "and o.symbol = ?2 and o.side = ?3")
    List<OrderBook> findNextSmallerPrice(Double limitPrice, Symbol symbol, Side side);


}
