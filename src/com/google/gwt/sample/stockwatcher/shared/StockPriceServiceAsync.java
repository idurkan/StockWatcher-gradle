package com.google.gwt.sample.stockwatcher.shared;

import com.google.gwt.sample.stockwatcher.shared.StockPrice;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Class Description Here!
 * User: idurkan
 * Date: 1/9/12
 * Time: 3:49 PM
 */
public interface StockPriceServiceAsync {
    void getPrices(String[] symbols, AsyncCallback<StockPrice[]> callback);
}
