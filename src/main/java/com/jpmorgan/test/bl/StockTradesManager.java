package com.jpmorgan.test.bl;

import com.jpmorgan.test.pojo.Trade;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class is holding information about all trades that are done for single stock
 */
public class StockTradesManager {
    /** Time frame that is used to calculate Stock price */
    private static final long CALC_TIME = 15 * 60 * 1000;
    /** Main data holder for trades */
    private TreeSet<Trade> tradesSet;
    /** Lock object for multiple readers and single writer */
    private ReadWriteLock readWriteLock;

    /**                                                                t
     * Default constructor.
     */
    public StockTradesManager() {
        super();

        this.tradesSet = new TreeSet<Trade>(new Comparator<Trade>() {
            @Override
            public int compare(Trade o1, Trade o2) {
                if (o1.getDateTime() > o2.getDateTime()) {
                    return 1;
                }
                if (o1.getDateTime() < o2.getDateTime()) {
                    return -1;
                }
                return 0;
            }
        });

        this.readWriteLock = new ReentrantReadWriteLock();
    }

    /**
     * Add new trade
     * @param trade value to add
     */
    public void addTrade(Trade trade) {
        Lock lock = this.readWriteLock.writeLock();
        try {
            lock.lock();
            this.tradesSet.add(trade);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Prepare iterator with all Trades that occurred within calculation interval.
     *
     * @return All Trades for calculation
     */
    public Iterator<Trade> getAllTradesInCalcInterval() {
        Trade trade = new Trade();
        trade.setDateTime(System.currentTimeMillis() - CALC_TIME);

        Lock lock = this.readWriteLock.readLock();
        try {
            lock.lock();
            return this.tradesSet.tailSet(trade).iterator();
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return Last trade that is recorded
     */
    public Trade getLastTrade() {
        Lock lock = this.readWriteLock.readLock();
        try {
            lock.lock();
            return this.tradesSet.last();
        } finally {
            lock.unlock();
        }
    }
}
