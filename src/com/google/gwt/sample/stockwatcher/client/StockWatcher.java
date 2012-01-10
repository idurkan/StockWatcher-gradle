package com.google.gwt.sample.stockwatcher.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.sample.stockwatcher.shared.DelistedException;
import com.google.gwt.sample.stockwatcher.shared.StockPrice;
import com.google.gwt.sample.stockwatcher.shared.StockPriceService;
import com.google.gwt.sample.stockwatcher.shared.StockPriceServiceAsync;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StockWatcher implements EntryPoint {

    // constants
    private static final int REFRESH_INTERVAL = 5000; // milliseconds;

    // GWT RPC
    private StockPriceServiceAsync priceService = GWT.create(StockPriceService.class);

    // ui
    private VerticalPanel mainPanel = new VerticalPanel();
    private FlexTable stocksFlexTable = new FlexTable();
    private HorizontalPanel addPanel = new HorizontalPanel();
    private TextBox newSymbolTextBox = new TextBox();
    private Button addStockButton = new Button("Add");
    private Label lastUpdatedLabel = new Label();
    private Label errorMessageLabel = new Label();

    // data
    private List<String> stocks = new ArrayList<String>();

    /**
     * Entry point method.
     */
    public void onModuleLoad() {
        addWidgets();
        styleWidgets();
        addHandlers();
    }

    public void addWidgets() {
        stocksFlexTable.setText(0, 0, "Symbol");
        stocksFlexTable.setText(0, 1, "Price");
        stocksFlexTable.setText(0, 2, "Change");
        stocksFlexTable.setText(0, 3, "Remove");

        addPanel.add(newSymbolTextBox);
        addPanel.add(addStockButton);

        errorMessageLabel.setStyleName("errorMessage");
        errorMessageLabel.setVisible(false);

        mainPanel.add(errorMessageLabel);
        mainPanel.add(stocksFlexTable);
        mainPanel.add(addPanel);
        mainPanel.add(lastUpdatedLabel);

        // get a RootPanel wrapping the <div> w/ ID 'stockList' in the StockWatcher.html host page.
        RootPanel.get("stockList").add(mainPanel);

        newSymbolTextBox.setFocus(true);
    }

    private void styleWidgets() {
        stocksFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");

        // styles the table itself.
        stocksFlexTable.addStyleName("watchList");

        // styles columns
        stocksFlexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(0, 3, "watchListRemoveColumn");

        stocksFlexTable.setCellPadding(6);

        addPanel.addStyleName("addPanel");
    }

    public void addHandlers() {
        addStockButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                addStock();
            }
        });

        addStockButton.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if (event.getCharCode() == KeyCodes.KEY_ENTER)
                    addStock();
            }
        });

        Timer refreshTimer = new Timer() {
            @Override
            public void run() {
                refreshWatchList();
            }
        };
        refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
    }

    private void addStock() {
        final String symbol = newSymbolTextBox.getText().toUpperCase().trim();

        // symbol must be 1-10 characters long, containing numerals, upper-case Roman chars,
        // or periods.
        if (!symbol.matches("^[0-9A-Z\\.]{1,10}$")) {
            Window.alert("Symbol '" + symbol + "' is not a valid symbol.");
            newSymbolTextBox.selectAll();
            return;
        }

        newSymbolTextBox.setText("");

        if (stocks.contains(symbol)) {
            return;
        }

        int row = stocksFlexTable.getRowCount();
        stocks.add(symbol);
        // will automatically add enough rows to accommodate given row.
        stocksFlexTable.setText(row, 0, symbol);

        // add styling
        stocksFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(row, 3, "watchListRemoveColumn");


        // add a remove-row button.
        Button removeStockButton = new Button("x");
        removeStockButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                // handler will remember the symbol for which it was created.
                int removedIndex = stocks.indexOf(symbol);
                stocks.remove(removedIndex);

                // row 0 is the headings row!
                stocksFlexTable.removeRow(removedIndex+1);
            }
        });
        stocksFlexTable.setWidget(row, 3, removeStockButton);

        // add a label for later re-styling in change row
        Label changeLabel = new Label();
        changeLabel.addStyleName("noChange");
        stocksFlexTable.setWidget(row, 2, new Label());

        refreshWatchList();
    }

    /**
     * Generate random stock prices.
     */
    private void refreshWatchList() {
        AsyncCallback<StockPrice[]> callback = new AsyncCallback<StockPrice[]>() {
            public void onFailure(Throwable caught) {
                String details = caught.getMessage();

                if (caught instanceof DelistedException) {
                    details = "Company '" + ((DelistedException)caught).getSymbol()
                            + "' was delisted";
                }

                if (caught instanceof StatusCodeException) {
                    StatusCodeException underlying = (StatusCodeException)caught;
                    details = "HTTP error " + underlying.getStatusCode() + "; encoded error "
                            + underlying.getEncodedResponse();
                }

                errorMessageLabel.setText("Error: " + details);
                errorMessageLabel.setVisible(true);
            }

            public void onSuccess(StockPrice[] result) {
                // work must be done after a callback - the async call does not block.
                updateTable(result);
            }
        };

        priceService.getPrices(stocks.toArray(new String[stocks.size()]), callback);
    }

    private void updateTable(StockPrice[] prices) {
        for (StockPrice price : prices) {
            updateTableRow(price);
        }

        // DateTimeFormat is a GWT class but Date is Java's Date.
        lastUpdatedLabel.setText("Last update: "
            + DateTimeFormat.getMediumDateFormat().format(new Date()));

        errorMessageLabel.setVisible(false);
    }

    private void updateTableRow(StockPrice price) {
        // only update if stock is still in-table.
        if (!stocks.contains(price.getSymbol())) {
            return;
        }

        int row = stocks.indexOf(price.getSymbol()) + 1;

        // Format data in the price and change fields (columns 1 and 2)
        // NumberFormat is another GWT-specific class here.
        String priceText = NumberFormat.getFormat("#,##0.00").format(
            price.getPrice());
        NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
        String changeText = changeFormat.format(price.getChange());
        String changePercentText = changeFormat.format(price.getChangePercent());

        stocksFlexTable.setText(row, 1, priceText);
//        stocksFlexTable.setText(row, 2, changeText + " (" + changePercentText
//            + "%)");

        // add a label to the change cell, styled based on change sign
        Label changeWidget = (Label)stocksFlexTable.getWidget(row, 2);
        changeWidget.setText(changeText + " ( " + changePercentText + "% )");

        // set style of change cell based on quantity of change.
        String changeStyleName = "noChange";
        if (price.getChangePercent() < -0.1f) {
            changeStyleName = "negativeChange";
        } else if (price.getChangePercent() > 0.1f) {
            changeStyleName = "positiveChange";
        }
        changeWidget.setStyleName(changeStyleName);
    }

}
