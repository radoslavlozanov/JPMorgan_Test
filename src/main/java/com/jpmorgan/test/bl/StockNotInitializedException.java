package com.jpmorgan.test.bl;

/**
 * Exception is thrown in case that stock is not initialized.<br/>
 * It contains information about the stock symbol.
 *
 * @version 1.0 / 16.08.2015
 * @author Radoslav Lozanov
 */
public class StockNotInitializedException extends Exception {
    /** Stock symbol for the exception */
    private String stockSymbol;

    /**
     * Initialize exception with given stock symbol
     * @param stockSymbol
     */
    public StockNotInitializedException(String stockSymbol) {
        super();
        this.stockSymbol = stockSymbol;
    }

    /**
     * Initialize with message and stock symbol
     * @param message
     * @param stockSymbol
     */
    public StockNotInitializedException(String message, String stockSymbol) {
        super(message);
        this.stockSymbol = stockSymbol;
    }
}
