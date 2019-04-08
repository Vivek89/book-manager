package com.aqr.etf.book.web;

import com.aqr.etf.book.dao.OrderRepository;
import com.aqr.etf.book.model.IModel;
import com.aqr.etf.book.model.OrderBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookController {

    private OrderRepository orderRepository;

    @Autowired
    public BookController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/allNewOrder")
    public List<OrderBook> allNewOrder() {
        return (List<OrderBook>) orderRepository.findAll();
    }


}
