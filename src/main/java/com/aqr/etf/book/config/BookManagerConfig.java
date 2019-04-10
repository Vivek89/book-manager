package com.aqr.etf.book.config;

import com.aqr.etf.book.model.IModel;
import com.aqr.etf.book.model.Symbol;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.IdGenerator;
import org.springframework.util.SimpleIdGenerator;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import rx.subjects.PublishSubject;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

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
    public Map<Symbol, List> getBuyOffHeapMap() throws IOException {

        Map<Symbol, List> symbolMap =
                ChronicleMapBuilder.of(Symbol.class, List.class)
                        .entries(Symbol.values().length) //the maximum number of entries for the map
                        .averageKeySize(32) //the average number of bytes for the key
                        .createPersistedTo(new File("/tmp",
                                "symbolBuyMap"));
        return symbolMap;
    }

    @Bean(name="sellMap")
    public Map<Symbol, List> getSellOffHeapMap() throws IOException {

        Map<Symbol, List> symbolMap =
                ChronicleMapBuilder.of(Symbol.class, List.class)
                        .entries(Symbol.values().length)
                        .averageKeySize(32)
                        .createPersistedTo(new File("/tmp",
                                "symbolSellMap"));
        return symbolMap;
    }

    @Bean(name = "buyThresholdMap")
    public Map<Symbol, Double> getBuyThresholdMap() throws IOException {
        Map<Symbol, Double> symbolMap =
                ChronicleMapBuilder.of(Symbol.class, Double.class)
                        .entries(Symbol.values().length)
                        .averageKeySize(32)
                        .createPersistedTo(new File("/tmp",
                                "symbolBuyThresholdMap"));
        return symbolMap;
    }


    @Bean(name = "sellThresholdMap")
    public Map<Symbol, Double> getSellThresholdMap() throws IOException {
        Map<Symbol, Double> symbolMap =
                ChronicleMapBuilder.of(Symbol.class, Double.class)
                        .entries(Symbol.values().length)
                        .averageKeySize(32)
                        .createPersistedTo(new File("/tmp",
                                "symbolSellThresholdMap"));
        return symbolMap;
    }

    @Bean(name = "orderIdList")
    public List<UUID> orderIdList() {
        return new ArrayList<>();
    }

    @Bean(name = "randomNumber")
    public Random getRandom() {
        return new Random();
    }

    @Bean(name = "newOrderPublishSubject")
    public PublishSubject<IModel> getNewOrderPublishSubject() {
        return PublishSubject.create();
    }

    @Bean(name = "modifyOrderPublishSubject")
    public PublishSubject<IModel> getModifyOrderPublishSubject() {
        return PublishSubject.create();
    }

    @Bean(name = "cancelOrderPublishSubject")
    public PublishSubject<IModel> getCancelOrderPublishSubject() {
        return PublishSubject.create();
    }

//    @Bean
//    RouterFunction<ServerResponse> routes(ProfileHandler handler) {
//        return route(GET("/top"), handler::get)
//                .andRoute(GET("/symbol/{id}"), handler::getBySymbol);
//    }

}
