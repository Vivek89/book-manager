package com.aqr.etf.book.web;

import com.aqr.etf.book.dao.OrderRepository;
import com.aqr.etf.book.model.OrderBook;
import com.aqr.etf.book.model.Side;
import com.aqr.etf.book.model.Symbol;
import com.aqr.etf.book.model.dto.LevelDTO;
import com.aqr.etf.book.service.AbstractBookService;
import com.aqr.etf.book.service.BookLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookController {

    private final AbstractBookService<Symbol, LevelDTO> service;
    private final OrderRepository orderRepository;

    @Autowired
    public BookController(final OrderRepository orderRepository,
                          @Qualifier("bookLevelService")
                            final BookLevelService service) {
        this.orderRepository = orderRepository;
        this.service = service;
    }

    @GetMapping("/allNewOrder")
    public List<OrderBook> allNewOrder() {
        return (List<OrderBook>) orderRepository.findAll();
    }

    @GetMapping("/symbol/{symbol}/side/{side}/price/{price}")
    public List<OrderBook> getOrderForSymbolPrice(@PathVariable("symbol") String symbol,
                                                  @PathVariable("side") String side,
                                                  @PathVariable("price") Double price) {
        return orderRepository.findBySymbolAndLimitPriceAndSide(Symbol.valueOf(symbol), price, Side.valueOf(side));
    }

    @GetMapping("/symbol/{symbol}")
    public List<OrderBook> getOrderForSymbol(@PathVariable("symbol") String symbol) {
        return orderRepository.findBySymbol(Symbol.valueOf(symbol));
    }

    @GetMapping("/levels/symbol/{symbol}")
    public LevelDTO getLevel(@PathVariable("symbol") String symbol) {
        return service.applyStrategy(Symbol.valueOf(symbol));
    }

    @GetMapping("/otherPrices/symbol/{symbol}/side/{side}/threshold/{price}")
    public List<OrderBook> otherPrice(@PathVariable("symbol") String symbol,
                                      @PathVariable("side") String side,
                                      @PathVariable("price") Double price) {
        return orderRepository.findNextSmallerPrice(price, Symbol.valueOf(symbol), Side.valueOf(side));

    }

}
