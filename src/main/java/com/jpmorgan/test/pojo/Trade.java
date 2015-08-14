package com.jpmorgan.test.pojo;

/**
 * Represents single {@link Stock stock} trade. Can be buy or sell.
 *
 * @version 1.0 / 11.08.2015
 * @author Radoslav Lozanov
 */
public class Trade implements Comparable {
    /** Stock that was traded */
    private Stock stock;
    /** Type of the Trade */
    private TradeType tradeType;
    /** When trade occurred */
    private long dateTime;
    /** Quantity of shares subject of the Trade */
    private int quantity;
    /** Calculated single share price */
    private int singlePrice;
    /** Total price of the trade */
    private int totalPrice;

    /**
     * Construct a single Trade with all information needed
     *
     * @param stock Stock that is traded
     * @param tradeType Type of trade
     * @param dateTime When trade happened
     * @param quantity How many Stocks are sale
     * @param totalPrice Total price of the deal
     */
    public Trade(Stock stock, TradeType tradeType, long dateTime, int quantity, int totalPrice) {
        super();

        this.stock = stock;
        this.tradeType = tradeType;
        this.dateTime = dateTime;
        this.quantity = quantity;
        this.totalPrice = totalPrice;

        this.singlePrice = Math.round((float)totalPrice / (float)quantity);
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSinglePrice() {
        return singlePrice;
    }

    public void setSinglePrice(int singlePrice) {
        this.singlePrice = singlePrice;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Trade)) {
            throw new ClassCastException("Can not compare with different object");
        }

        Trade trade = (Trade)o;
        return Long.valueOf(this.dateTime).compareTo(trade.getDateTime());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Trade)) {
            return false;
        }

        Trade trade = (Trade)o;
        return this.getStock().equals(trade.getStock()) &&
               this.getDateTime() == trade.getDateTime() &&
               this.getQuantity() == trade.getQuantity() &&
               this.getTotalPrice() == trade.getTotalPrice() &&
               this.getTradeType().equals(trade.getTradeType());
    }
}
