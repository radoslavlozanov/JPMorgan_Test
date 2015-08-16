package com.jpmorgan.test.bl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for TradesManager.
 */
public class TradesManagerTest {
    private static final String[] STOCK_SYMBOLS = {"ABC", "BCD", "CDE", "DEF", "CBA", "DCB", "ETC"};
    private TradesManager tradesManager;

    @Before
    public void setUp() throws Exception {
        this.tradesManager = new TradesManager();

        // Add few Stocks
    }

    @Test
    public void testAddTrade() throws Exception {

    }
}
