package com.aqr.etf.book.web;

import com.aqr.etf.book.dao.OrderRepository;
import com.aqr.etf.book.model.OrderBook;
import com.aqr.etf.book.model.Side;
import com.aqr.etf.book.model.Symbol;
import com.aqr.etf.book.model.dto.LevelDTO;
import com.aqr.etf.book.service.AbstractBookService;
import com.aqr.etf.book.service.BookLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(value = "Book Manager", description = "Book Manager Server REST End Point")
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


    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Response", response = LevelDTO.class)})
    @ApiOperation(value = "Get Levels for a Symbol", response = LevelDTO.class)
    @GetMapping("/levels/symbol/{symbol}")
    public LevelDTO getLevel(@PathVariable("symbol") String symbol) {
        return service.applyStrategy(Symbol.valueOf(symbol));
    }


    /**
     * ----------------------------------------------------------------------------------
     * All end points below are - utility End Points to validate Levels calculated above!
     * ----------------------------------------------------------------------------------
     */

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Response", response = OrderBook.class)})
    @ApiOperation(value = "Get all Incoming Orders", response = OrderBook.class)
    @GetMapping("/allNewOrder")
    public List<OrderBook> allNewOrder() {
        return (List<OrderBook>) orderRepository.findAll();
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Response", response = OrderBook.class)})
    @ApiOperation(value = "Get Incoming Orders for Symbol, Side & Price", response = OrderBook.class)
    @GetMapping("/symbol/{symbol}/side/{side}/price/{price}")
    public List<OrderBook> getOrderForSymbolPrice(@PathVariable("symbol") String symbol,
                                                  @PathVariable("side") String side,
                                                  @PathVariable("price") Double price) {
        return orderRepository.findBySymbolAndLimitPriceAndSide(Symbol.valueOf(symbol),
                price,
                Side.valueOf(side));
    }


    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Response", response = OrderBook.class)})
    @ApiOperation(value = "Get all Incoming Orders for a Symbol", response = OrderBook.class)
    @GetMapping("/symbol/{symbol}")
    public List<OrderBook> getOrderForSymbol(@PathVariable("symbol") String symbol) {
        return orderRepository.findBySymbol(Symbol.valueOf(symbol));
    }


    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Response", response = OrderBook.class)})
    @ApiOperation(value = "Get all Orders for next large limit price", response = OrderBook.class)
    @GetMapping("/nextLargerPrice/symbol/{symbol}/side/{side}/threshold/{price}")
    public List<OrderBook> nextLargerPrice(@PathVariable("symbol") String symbol,
                                           @PathVariable("side") String side,
                                           @PathVariable("price") Double price) {
        return orderRepository.findNextLargerPrice(price, Symbol.valueOf(symbol), Side.valueOf(side));
    }


    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Response", response = OrderBook.class)})
    @ApiOperation(value = "Get all Orders for next small limit price", response = OrderBook.class)
    @GetMapping("/nextSmallerPrice/symbol/{symbol}/side/{side}/threshold/{price}")
    public List<OrderBook> nextSmallerPrice(@PathVariable("symbol") String symbol,
                                      @PathVariable("side") String side,
                                      @PathVariable("price") Double price) {
        return orderRepository.findNextSmallerPrice(price, Symbol.valueOf(symbol), Side.valueOf(side));
    }

}
