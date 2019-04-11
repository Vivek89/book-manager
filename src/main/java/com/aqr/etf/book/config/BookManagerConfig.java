package com.aqr.etf.book.config;

import com.aqr.etf.book.model.IModel;
import com.aqr.etf.book.model.Symbol;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.IdGenerator;
import org.springframework.util.SimpleIdGenerator;
import rx.subjects.ReplaySubject;

import java.io.IOException;
import java.util.*;

import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * This class contains all the spring managed Beans.
 * These beans will be used as global variables.
 */
@Configuration
public class BookManagerConfig {

    @Bean
    public IdGenerator getIdGenerator() {
        return new SimpleIdGenerator();
        // use alternative ID generator in non POC environment
//        return new AlternativeJdkIdGenerator();
    }

    @Bean(name="buyMap")
    public Map<Symbol, List> getBuyMap() throws IOException {
        return new HashMap<>();
    }

    @Bean(name="sellMap")
    public Map<Symbol, List> getSellMap() throws IOException {
        return new HashMap<>();
    }

    @Bean(name = "buyThresholdMap")
    public Map<Symbol, Double> getBuyThresholdMap() throws IOException {
        return new HashMap<>();
    }


    @Bean(name = "sellThresholdMap")
    public Map<Symbol, Double> getSellThresholdMap() throws IOException {
        return new HashMap<>();
    }

    @Bean(name = "orderIdList")
    public List<UUID> orderIdList() {
        return new ArrayList<>();
    }

    @Bean(name = "randomNumber")
    public Random getRandom() {
        return new Random();
    }

    @Bean(name = "newOrderReplaySubject")
    public ReplaySubject<IModel> getNewOrderReplaySubject() {
        return ReplaySubject.create(300);
    }

    @Bean(name = "modifyOrderReplaySubject")
    public ReplaySubject<IModel> getModifyOrderReplaySubject() {
        return ReplaySubject.create(300);
    }

    @Bean(name = "cancelOrderReplaySubject")
    public ReplaySubject<IModel> getCancelOrderReplaySubject() {
        return ReplaySubject.create(300);
    }

//    @Bean
//    RouterFunction<ServerResponse> routes(ProfileHandler handler) {
//        return route(GET("/top"), handler::get)
//                .andRoute(GET("/symbol/{id}"), handler::getBySymbol);
//    }

}
