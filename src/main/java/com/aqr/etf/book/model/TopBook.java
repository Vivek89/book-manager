package com.aqr.etf.book.model;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TopBook implements IModel {

    private final Symbol symbol;
    private final Double bestBidPrice;
    private final Long bestBidSize;
    private final Double bestOfferPrice;
    private final Long bestOfferSize;

}
