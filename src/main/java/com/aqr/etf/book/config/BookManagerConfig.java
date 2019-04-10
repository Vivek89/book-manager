package com.aqr.etf.book.config;

import com.aqr.etf.book.model.IModel;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.IdGenerator;
import org.springframework.util.SimpleIdGenerator;
import rx.subjects.ReplaySubject;

import java.io.File;
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
    public Map<String, List> getBuyOffHeapMap() throws IOException {

        Map<String, List> symbolMap =
                ChronicleMapBuilder.of(String.class, List.class)
                        .entries(20) //the maximum number of entries for the map
                        .averageKeySize(32) //the average number of bytes for the key
                        .createPersistedTo(new File("/tmp",
                                "symbolBuyMap"));
        return symbolMap;
    }

    @Bean(name="sellMap")
    public Map<String, List> getSellOffHeapMap() throws IOException {

        Map<String, List> symbolMap =
                ChronicleMapBuilder.of(String.class, List.class)
                        .entries(20)
                        .averageKeySize(32)
                        .createPersistedTo(new File("/tmp",
                                "symbolSellMap"));
        return symbolMap;
    }

    @Bean(name = "buyThresholdMap")
    public Map<String, Double> getBuyThresholdMap() throws IOException {
        Map<String, Double> symbolMap =
                ChronicleMapBuilder.of(String.class, Double.class)
                        .entries(20)
                        .averageKeySize(32)
                        .createPersistedTo(new File("/tmp",
                                "symbolBuyThresholdMap"));
        return symbolMap;
    }


    @Bean(name = "sellThresholdMap")
    public Map<String, Double> getSellThresholdMap() throws IOException {
        Map<String, Double> symbolMap =
                ChronicleMapBuilder.of(String.class, Double.class)
                        .entries(20)
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

    @Bean(name = "newOrderReplaySubject")
    public ReplaySubject<IModel> getNewOrderReplaySubject() {
        return ReplaySubject.create();
    }

    @Bean(name = "modifyOrderReplaySubject")
    public ReplaySubject<IModel> getModifyOrderReplaySubject() {
        return ReplaySubject.create();
    }

    @Bean(name = "cancelOrderReplaySubject")
    public ReplaySubject<IModel> getCancelOrderReplaySubject() {
        return ReplaySubject.create();
    }

//    @Bean
//    RouterFunction<ServerResponse> routes(ProfileHandler handler) {
//        return route(GET("/top"), handler::get)
//                .andRoute(GET("/symbol/{id}"), handler::getBySymbol);
//    }

}
