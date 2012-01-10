package com.google.gwt.sample.stockwatcher.shared;

import java.io.Serializable;

/**
 * Class Description Here!
 * User: idurkan
 * Date: 1/9/12
 * Time: 6:17 PM
 */
public class DelistedException extends Exception implements Serializable {
    private String symbol;

    public DelistedException() {

    }

    public DelistedException(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }
}
