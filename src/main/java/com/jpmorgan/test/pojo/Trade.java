package com.jpmorgan.test.pojo;

/**
 * Represents single {@link Stock stock} trade. Can be buy or sell.
 *
 * @version 1.0 / 11.08.2015
 * @author Radoslav Lozanov
 */
public class Trade {
    /** Stock that was traded */
    private Stock stock;
    /** Type of the Trade */
    private TradeType tradeType;
    /** When trade occured */
    private long dateTime;
    /** Quantity of shares subject of the Trade */
    private int quantity;
    /** Calculated single share price */
    private int singlePrice;
    /** Total price of the trade */
    private int totalPrice;
}
