package com.rtb.blocks.api;

public class QuoteValue {
    private final double bid;
    private final double ask;

    public QuoteValue(double bid, double ask) {
        this.bid = bid;
        this.ask = ask;
    }

    public double getBid() {
        return bid;
    }

    public double getAsk() {
        return ask;
    }

    public double getMid() {
        return (bid + ask) / 2;
    }
}
