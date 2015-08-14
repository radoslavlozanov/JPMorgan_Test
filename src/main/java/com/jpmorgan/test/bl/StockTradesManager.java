package com.jpmorgan.test.bl;

/**
 * Class is holding information about all trades that are done for single stock
 */

import com.jpmorgan.test.pojo.Trade;

import java.util.TreeSet;

public class StockTradesManager {
    /** Time frame that is used to calculate Stock price */
    private static final long CALC_TIME = 15 * 60 * 1000;
    /** Main data holder for trades */
    private TreeSet<Trade> tradesSet;

    /**
     * Default constructor.
     */
    public StockTradesManager() {
        super();

        this.tradesSet = new TreeSet<Trade>();
    }
}