package com.jpmorgan.test.pojo;

/**
 * Pojo that representing single Stock.
 *
 * @version 1.0 / 11.08.2015
 * @author Radoslav Lozanov
 */
public class Stock {
    /** Type */
    private StockType type;

    /** Representing symbol */
    private String symbol;
    /** Symbol hash code - for internal usage (equal and hashCode) */
    private int symbolHashCode;
    /** Par value of the stock */
    private int parValue;

    /**
     * @return Type of the Stock
     */
    public StockType getType() {
        return type;
    }

    /**
     * Set Stock type
     * @param type value to set
     */
    public void setType(StockType type) {
        this.type = type;
    }

    /**
     * @return Symbol that representing the Stock
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Setting symbol that is representing the Stock
     * @param symbol Value to set
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
        // Calculate hashCode
        this.symbolHashCode = this.symbol.hashCode();
    }

    /**
     * @return Stock par value
     */
    public int getParValue() {
        return parValue;
    }

    /**
     * Set stock par value
     * @param parValue Value to set
     */
    public void setParValue(int parValue) {
        this.parValue = parValue;
    }

    /**
     * @return Hash code of the symbol that is representing the stock
     */
    public int getSymbolHashCode() {
        return symbolHashCode;
    }

    /**
     * @return Stock symbol hash code - used to retrieve {@link com.jpmorgan.test.bl.StockTradesManager trade manager}
     */
    @Override
    public int hashCode() {
        return this.getSymbolHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Stock)) {
            return false;
        }

        Stock stockToCompare = (Stock)obj;
        return stockToCompare.getSymbolHashCode() == this.getSymbolHashCode() &&
               stockToCompare.getSymbol() != null && stockToCompare.getSymbol().equals(this.getSymbol());
    }
}
