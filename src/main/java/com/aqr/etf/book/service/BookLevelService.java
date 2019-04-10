package com.aqr.etf.book.service;

import com.aqr.etf.book.model.OrderBook;
import com.aqr.etf.book.model.Valid;
import com.aqr.etf.book.model.dto.Level;
import com.aqr.etf.book.model.dto.LevelDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service("bookLevelService")
public class BookLevelService extends AbstractBookService<String, LevelDTO> {

    private final Map<String, List> buyMap;
    private final Map<String, List> sellMap;

    @Autowired
    public BookLevelService(@Qualifier("buyMap")
                                final Map<String, List> buyMap,
                            @Qualifier("sellMap")
                                final Map<String, List> sellMap) {
        this.buyMap = buyMap;
        this.sellMap = sellMap;
    }


    @Override
    String preProcess(String symbol) {
        // Nothing to do here!
        return symbol;
    }

    @Override
    Valid validate(String symbol) {
        return Valid.VALID;
    }

    @Override
    LevelDTO compute(String symbol) {
        List<OrderBook> buyLevels = buyMap.get(symbol);
        List<OrderBook> sellLevels = sellMap.get(symbol);

        LevelDTO levelDTO = new LevelDTO();
        List<Level> responseBuyLevel = new ArrayList<>();
        List<Level> responseSellLevel = new ArrayList<>();

        IntStream.range(0, buyLevels.size())
                .forEach(index -> {
                    responseBuyLevel.add(new Level(
                            "Level-" + index,
                            buyLevels.get(index).getLimitPrice(),
                            buyLevels.get(index).getQuantity()
                    ));
                });

        IntStream.range(0, sellLevels.size())
                .forEach(index -> {
                    responseSellLevel.add(new Level(
                            "Level-" + index,
                            sellLevels.get(index).getLimitPrice(),
                            sellLevels.get(index).getQuantity()
                    ));
                });

        levelDTO.setBuyLevel(responseBuyLevel);
        levelDTO.setSellLevel(responseSellLevel);
        return levelDTO;
    }

    @Override
    LevelDTO postProcess(LevelDTO levelDTO) {
        return levelDTO;
    }

    @Override
    LevelDTO processError(String r) {
        return null;
    }
}
