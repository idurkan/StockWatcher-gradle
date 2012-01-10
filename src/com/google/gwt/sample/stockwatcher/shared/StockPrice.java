package com.google.gwt.sample.stockwatcher.shared;

import java.io.Serializable;

/**
 * Class Description Here!
 * User: idurkan
 * Date: 1/9/12
 * Time: 1:31 AM
 */
public class StockPrice implements Serializable {
    private String symbol;
    private double price;
    private double change;

    public StockPrice() {
        price = 0.0;
        change = 0.0;
    }

    public StockPrice(String symbol, double price, double change) {
        this.symbol = symbol;
        this.price = price;
        this.change = change;
    }


    public double getChangePercent() {
        return 100.0 * (this.change / this.price);
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }
}
