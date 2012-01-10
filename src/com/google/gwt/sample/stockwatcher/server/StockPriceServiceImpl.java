package com.google.gwt.sample.stockwatcher.server;

import com.google.gwt.sample.stockwatcher.shared.DelistedException;
import com.google.gwt.sample.stockwatcher.shared.StockPriceService;
import com.google.gwt.sample.stockwatcher.shared.StockPrice;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.Random;

/**
 * Class Description Here!
 * User: idurkan
 * Date: 1/9/12
 * Time: 3:33 PM
 */
public class StockPriceServiceImpl extends RemoteServiceServlet implements StockPriceService {
    private static final double MAX_PRICE = 100.0; // $100.00
    private static final double MAX_PRICE_CHANGE = 0.03; // +/- 2%

    private static final String USERNAME = "idurkan";
    private static final String PASSWORD = "31337";

    public StockPrice[] getPrices(String[] symbols) throws DelistedException {
        StockPrice[] prices = new StockPrice[symbols.length];

        Random generator = new Random();

        for (int i = 0; i < symbols.length; ++i) {
            if (symbols[i].equals("ERR")) {
                throw new DelistedException("ERR");
            }

            double price = generator.nextDouble() * MAX_PRICE;
            double change = price * MAX_PRICE_CHANGE
                    * (generator.nextDouble() * 2.0 - 1.0);

            prices[i] = new StockPrice(symbols[i], price, change);
        }

        return prices;
    }

    public boolean validateUserLogin(String username, String password) {
        return (username.equals(USERNAME) && password.equals(PASSWORD));
    }
}
