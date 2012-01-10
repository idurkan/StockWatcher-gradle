package com.google.gwt.sample.stockwatcher.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Class Description Here!
 * User: idurkan
 * Date: 1/9/12
 * Time: 3:32 PM
 */
@RemoteServiceRelativePath("stockPrices")
public interface StockPriceService extends RemoteService {
    StockPrice[] getPrices(String[] symbols) throws DelistedException;
}
