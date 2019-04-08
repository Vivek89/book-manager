package com.aqr.etf.book.dao;

import com.aqr.etf.book.model.OrderBook;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends CrudRepository<OrderBook, UUID> {

    List<OrderBook> findBySymbolAndLimitPrice(UUID id, Double limitPrice);

}
