package com.jpmorgan.test.bl;

import com.jpmorgan.test.pojo.Stock;
import com.jpmorgan.test.pojo.StockType;
import com.jpmorgan.test.pojo.TradeType;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

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

        // Add all stocks using random values
        Random random = new Random();
        for (String stockSymbol: STOCK_SYMBOLS) {
            StockType stockType = StockType.values()[random.nextInt(StockType.values().length)];
            int parValue = random.nextInt(200) + 1;
            int fixedDividend = random.nextInt(50) + 1;
            this.tradesManager.addOrUpdateStock(stockSymbol, stockType, parValue, fixedDividend);
        }

        // Negative tests
        try {
            this.tradesManager.addOrUpdateStock(null, StockType.Common, 1, 1);
            assertTrue(false);
        } catch (RuntimeException e) {}
        try {
            this.tradesManager.addOrUpdateStock("TST", null, 1, 1);
            assertTrue(false);
        } catch (RuntimeException e) {}
        try {
            this.tradesManager.addOrUpdateStock("TST", StockType.Common, 0, 1);
            assertTrue(false);
        } catch (RuntimeException e) {}
        try {
            this.tradesManager.addOrUpdateStock("TST", StockType.Preferred, 1, 0);
            assertTrue(false);
        } catch (RuntimeException e) {}
    }

    @Test
    public void testAddTrade() throws Exception {
        // Negative test
        try {
            this.tradesManager.addTrade("NOT", TradeType.buy, 1, 1, 1);
            assertTrue(false); // Fail
        } catch (StockNotInitializedException e) {}
        try {
            this.tradesManager.addTrade(null, TradeType.buy, 1, 1, 1);
            assertTrue(false); // Fail
        } catch (StockNotInitializedException e) {}
        try {
            this.tradesManager.addTrade(STOCK_SYMBOLS[0], TradeType.buy, 0, 1, 1);
            assertTrue(false); // Fail
        } catch (RuntimeException e) {}
        try {
            this.tradesManager.addTrade(STOCK_SYMBOLS[0], TradeType.buy, 1, 0, 1);
            assertTrue(false); // Fail
        } catch (RuntimeException e) {}
        try {
            this.tradesManager.addTrade(STOCK_SYMBOLS[0], TradeType.buy, 1, 1, 0);
            assertTrue(false); // Fail
        } catch (RuntimeException e) {}

        // Add 10000 random trades
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            String stockSymbol = STOCK_SYMBOLS[random.nextInt(STOCK_SYMBOLS.length)];
            TradeType tradeType = TradeType.values()[random.nextInt(TradeType.values().length)];
            int quantity = random.nextInt(100) + 1;
            int totalPrice = random.nextInt(300) + 1;
            int dividend = (totalPrice * (random.nextInt(20) + 1) / 100) + 1;

            this.tradesManager.addTrade(stockSymbol, tradeType, quantity, totalPrice, dividend);
        }
    }

    @Test
    public void testCalculateDividendYield() throws Exception {
        // find stocks with Preferred and common type
        Stock preferredStock = null;
        Stock commonStock = null;

        for (String stockSymbol: STOCK_SYMBOLS) {
            Stock currentStock = this.tradesManager.getStock(stockSymbol);

            if (StockType.Preferred.equals(currentStock.getType())) {
                preferredStock = currentStock;
                if (commonStock != null) {
                    break;
                } else {
                    continue;
                }
            }

            commonStock = currentStock;
            if (preferredStock != null) {
                break;
            } else {
                continue;
            }
        }

        // Test preferred
        this.tradesManager.addTrade(preferredStock.getSymbol(), TradeType.buy, 1, 10000, 1000);
        float dividendYieldCalc = this.tradesManager.calculateDividendYield(preferredStock.getSymbol());
        float dividendYieldExp = (preferredStock.getFixedDividend() * preferredStock.getParValue()) / (float)(100 * 10000);
        assertTrue(dividendYieldCalc == dividendYieldExp);

        // Test common
        this.tradesManager.addTrade(commonStock.getSymbol(), TradeType.buy, 1, 10000, 1000);
        dividendYieldCalc = this.tradesManager.calculateDividendYield(commonStock.getSymbol());
        dividendYieldExp = 1000/(float)10000;
        assertTrue(dividendYieldCalc == dividendYieldExp);
    }
}
