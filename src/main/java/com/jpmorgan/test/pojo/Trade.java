package com.jpmorgan.test.pojo;

/**
 * Represents single {@link Stock stock} trade. Can be buy or sell.
 *
 * @version 1.0 / 11.08.2015
 * @author Radoslav Lozanov
 */
public class Trade {
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
    /** Dividend for the trade */
    private int dividend;

    /**
     * Default constructor
     */
    public Trade() {
        super();
    }

    /**
     * Construct a single Trade with all information needed
     *
     * @param tradeType Type of trade
     * @param dateTime When trade happened
     * @param quantity How many Stocks are sale
     * @param totalPrice Total price of the deal
     */
    public Trade(TradeType tradeType, long dateTime, int quantity, int totalPrice, int dividend) {
        super();

        this.tradeType = tradeType;
        this.dateTime = dateTime;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.dividend = dividend;

        this.singlePrice = Math.round((float)totalPrice / (float)quantity);
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

    public int getDividend() {
        return dividend;
    }

    public void setDividend(int dividend) {
        this.dividend = dividend;
    }

    /**
     * Validates all values of the trade
     * @return <code>true</code> if Trade is valid, <code>false</code> otherwise
     */
    public boolean validate() {
        if (this.getTradeType() == null) {
            return false;
        }

        if (this.getDateTime() == 0) {
            return false;
        }

        if (this.getDividend() <= 0) {
            return false;
        }

        if (this.getQuantity() <= 0) {
            return false;
        }

        if (this.getTotalPrice() <= 0) {
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Trade)) {
            return false;
        }

        Trade trade = (Trade)o;
        return this.getDateTime() == trade.getDateTime() &&
               this.getQuantity() == trade.getQuantity() &&
               this.getTotalPrice() == trade.getTotalPrice() &&
               this.getTradeType().equals(trade.getTradeType());
    }
}
