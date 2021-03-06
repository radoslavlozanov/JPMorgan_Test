package com.jpmorgan.test.bl;

import com.jpmorgan.test.pojo.Stock;
import com.jpmorgan.test.pojo.StockType;
import com.jpmorgan.test.pojo.Trade;
import com.jpmorgan.test.pojo.TradeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
     * @param stockSymbol Stock symbol. Cannot be <code>null</code>
     * @param tradeType Type of the trade. Cannot be <code>null</code>
     * @param quantity quantity. Must be positive value
     * @param totalPrice total price. Must be positive value
     * @param dividend dividend for the trade. Must be positive value
     * @throws StockNotInitializedException if Stock is not found
     * @throws RuntimeException if some of the arguments is not valid
     */
    public void addTrade(String stockSymbol, TradeType tradeType, int quantity, int totalPrice, int dividend)
            throws StockNotInitializedException {
        Trade trade = new Trade(tradeType, System.currentTimeMillis(), quantity, totalPrice, dividend);
        if (!trade.validate()) {
            throw new RuntimeException("Invalid argument(s)");
        }

        Stock stock = this.getStock(stockSymbol);
        StockTradesManager tradesManager = this.getStockTradesManager(stock);

        tradesManager.addTrade(trade);
    }

    /**
     * Adds or updates stock into mem database.
     *
     * @param stockSymbol Stock symbol. Cannot be null
     * @param stockType Type of the stock. Cannot be null
     * @param parValue Par value. Must be positive value
     * @param fixedDividend Fixed dividend. In case of {@link StockType#Preferred preferred} type must be positive
     */
    public void addOrUpdateStock(String stockSymbol, StockType stockType, int parValue, int fixedDividend) {
        // Check input parameters
        if (stockSymbol == null) {
            throw new RuntimeException("Stock symbol cannot be null");
        }

        if (stockType == null) {
            throw new RuntimeException("Stock type cannot be null");
        }

        if (StockType.Preferred.equals(stockType) && fixedDividend <= 0) {
            throw new RuntimeException("Invalid fixed dividend value");
        }

        if (parValue <= 0) {
            throw new RuntimeException("Invalid par value");
        }

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
     * @return Calculated dividend yield
     * @throws StockNotInitializedException In case that stock doesn't exists
     */
    public float calculateDividendYield(String stockSymbol) throws StockNotInitializedException {
        float dividendYield = 0;
        Stock stock = this.getStock(stockSymbol);
        StockTradesManager stockTradesManager = this.getStockTradesManager(stock);
        Trade lastTrade = stockTradesManager.getLastTrade();

        switch (stock.getType()) {
            case Preferred:
                dividendYield = ( ((float)stock.getFixedDividend() / 100) * stock.getParValue()) /
                                ((float)(this.calculateStockPrice(stockSymbol)));
                break;
            default:
                dividendYield = lastTrade.getDividend() / (float)this.calculateStockPrice(stockSymbol);
                break;
        }

        return dividendYield;
    }

    /**
     * Calculate P/E Ratio for given stock
     * @param stockSymbol Symbol representing the stock
     * @return P/E Ratio value
     * @throws StockNotInitializedException In case that stock doesn't exists
     */
    public float calculatePERatio(String stockSymbol) throws StockNotInitializedException {
        Stock stock = this.getStock(stockSymbol);
        StockTradesManager stockTradesManager = this.getStockTradesManager(stock);
        Trade lastTrade = stockTradesManager.getLastTrade();

        return this.calculateStockPrice(stockSymbol) / (float)lastTrade.getDividend();
    }

    /**
     * Calculate stock price from all trades in past interval
     * @param stockSymbol Symbol of the stock
     * @return Calculated price in pennies
     * @throws StockNotInitializedException If stock is not found
     */
    public int calculateStockPrice(String stockSymbol) throws StockNotInitializedException {
        Stock stock = this.getStock(stockSymbol);
        StockTradesManager stockTradesManager = this.getStockTradesManager(stock);
        Iterator<Trade> tradesIterator = stockTradesManager.getAllTradesInCalcInterval();

        int tradePriceAndQuantity = 0;
        int quantity = 0;
        while(tradesIterator.hasNext()) {
            Trade trade = tradesIterator.next();
            tradePriceAndQuantity += trade.getTotalPrice() * trade.getQuantity();
            quantity += trade.getQuantity();
        }

        return (int)(tradePriceAndQuantity / (float)quantity);
    }

    /**
     * Calculates GBCE All Shares Index based on Geometric Mean of prices for all stocks
     * @return Calculated value
     */
    public double calculateGBCEAllSharesIndex() {
        // Get prices from all stocks
        Lock lock = this.readWriteLock.readLock();
        List<Integer> stockPricesList = new ArrayList<Integer>();
        try {
            lock.lock();

            for (String stockSymbol: this.stocksMap.keySet()) {
                try {
                    stockPricesList.add(this.calculateStockPrice(stockSymbol));
                } catch (StockNotInitializedException e) { // can't happen
                }
            }
        } finally {
            lock.unlock();
        }

        // calculate geometric mean
        double gmLog = 0.0d;
        for (Integer price: stockPricesList) {
            if (price.intValue() == 0) {
                return 0;
            }
            gmLog += Math.log(price);
        }

        return Math.exp(gmLog / stockPricesList.size());
    }

    /**
     * Get current Stock by given symbol
     *
     * @param stockSymbol symbol of the stock
     * @return Stock
     * @throws StockNotInitializedException Throw exception if stock is not found
     */
    public Stock getStock(String stockSymbol) throws StockNotInitializedException {
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
