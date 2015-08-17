package com.jpmorgan.test.bl;

import com.jpmorgan.test.pojo.Stock;
import com.jpmorgan.test.pojo.StockType;
import com.jpmorgan.test.pojo.Trade;
import com.jpmorgan.test.pojo.TradeType;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Main trades manager. Holds information about all trades of all stocks.<br/>
 * Calculates all needed data.
 *
 * @version 1.0 / 15.08.2015
 * @author Radoslav Lozanov
 */
public class TradesManager {
    /** Trades manager by stock */
    private HashMap<Stock, StockTradesManager> tradesMap;
    /** All stocks by symbol */
    private HashMap<String, Stock> stocksMap;
    /** Read write lock for reading from stocksMap */
    private ReadWriteLock readWriteLock;

    /**
     * Default constructor
     */
    public TradesManager() {
        super();

        this.tradesMap = new HashMap<Stock, StockTradesManager>();
        this.stocksMap = new HashMap<String, Stock>();
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    /**
     * Add new trade with all needed information
     *
     * @param stockSymbol Stock symbol
     * @param tradeType Type of the trade
     * @param quantity quantity
     * @param totalPrice total price
     * @param dividend dividend for the trade
     * @throws StockNotInitializedException
     */
    public void addTrade(String stockSymbol, TradeType tradeType, int quantity, int totalPrice, int dividend)
            throws StockNotInitializedException {
        Stock stock = this.getStock(stockSymbol);
        Trade trade = new Trade(tradeType, System.currentTimeMillis(), quantity, totalPrice, dividend);
        StockTradesManager tradesManager = this.getStockTradesManager(stock);

        tradesManager.addTrade(trade);
    }

    /**
     * Adds or updates stock into mem database.
     *
     * @param stockSymbol Stock symbol
     * @param stockType Type of the stock
     * @param parValue Par value
     * @param fixedDividend Fixed dividend
     */
    public void addOrUpdateStock(String stockSymbol, StockType stockType, int parValue, int fixedDividend) {
        Lock lock = this.readWriteLock.writeLock();
        try {
            lock.lock();
            Stock stock = this.stocksMap.remove(stockSymbol);
            StockTradesManager stockTradesManager = null;
            if (stock == null) {
                stock = new Stock();
                stock.setSymbol(stockSymbol);
                stockTradesManager = new StockTradesManager();
            } else {
                stockTradesManager = this.tradesMap.remove(stock);
            }

            stock.setType(stockType);
            stock.setParValue(parValue);
            stock.setFixedDividend(fixedDividend);

            this.stocksMap.put(stockSymbol, stock);
            this.tradesMap.put(stock, stockTradesManager);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Calculates dividend yield for given stock.
     * @param stockSymbol Stock symbol
     * @return Calculated dividend yield divide by 100 for float value
     * @throws StockNotInitializedException In case that stock doesn't exists
     */
    public int calculateDividendYield(String stockSymbol) throws StockNotInitializedException {
        int dividendYield = 0;
        Stock stock = this.getStock(stockSymbol);
        StockTradesManager stockTradesManager = this.getStockTradesManager(stock);
        Trade lastTrade = stockTradesManager.getLastTrade();

        switch (stock.getType()) {
            case Preferred:
                dividendYield = (int)((stock.getFixedDividend() * stock.getParValue()) /
                                      (float)lastTrade.getSinglePrice());
                break;
            default:
                dividendYield = (int)(lastTrade.getDividend() / (float)lastTrade.getSinglePrice());
                break;
        }

        return dividendYield;
    }

    /**
     * Calculate P/E Ratio for given stock
     * @param stockSymbol Symbol representing the stock
     * @return Integer P/E Ratio value - divide by 100 for float value
     * @throws StockNotInitializedException In case that stock doesn't exists
     */
    public int calculatePERatio(String stockSymbol) throws StockNotInitializedException {
        Stock stock = this.getStock(stockSymbol);
        StockTradesManager stockTradesManager = this.getStockTradesManager(stock);
        Trade lastTrade = stockTradesManager.getLastTrade();

        return (int)(lastTrade.getSinglePrice() / (float)lastTrade.getDividend());
    }

    /**
     * Get current Stock by given symbol
     *
     * @param stockSymbol symbol of the stock
     * @return Stock
     * @throws StockNotInitializedException Throw exception if stock is not found
     */
    private Stock getStock(String stockSymbol) throws StockNotInitializedException {
        Lock lock = this.readWriteLock.readLock();
        Stock stock = null;
        try {
            lock.lock();
            stock = this.stocksMap.get(stockSymbol);
        } finally {
            lock.unlock();
        }

        if (stock == null) {
            throw new StockNotInitializedException("Stock not initialized!", stockSymbol);
        }

        return stock;
    }

    /**
     * Get Trades manager by given stock
     *
     * @param stock the stock
     * @return StockTradesManager
     */
    private StockTradesManager getStockTradesManager(Stock stock) {
        Lock lock = this.readWriteLock.readLock();
        try {
            lock.lock();
            return this.tradesMap.get(stock);
        } finally {
            lock.unlock();
        }
    }
}
