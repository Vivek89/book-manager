package com.aqr.etf.book.model;

public enum Symbol {
    APLE(199),
    MSFT(119),
    GOOG(1203),
    JPM(105),
    WMT(99),
    TGT(81),
    BP(45),
    IBM(143),
    GM(38),
    FB(174);

    private final int index;

    private Symbol(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
