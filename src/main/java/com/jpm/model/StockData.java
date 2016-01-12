package com.jpm.model;

import java.util.ArrayList;
import java.util.List;


public class StockData {
    public enum StockType{COMMON, PREFERRED}

    private final String code;
    private final StockType type;
    private final Double fixedDividend;
    private final Double lastDividend;
    private final Double parValue;
    public static final Double ZERO = new Double(0.0);

    private List<StockTrade> tradingDetails = new ArrayList<>();

    public StockData(String code, Double parValue, Double lastDividend, StockType type) {
        this(code, parValue, lastDividend, type, null);
    }

    public StockData(String code, Double parValue, Double lastDividend, StockType type, Double fixedDividend) {
        this.code = code;
        this.type = type;
        this.lastDividend = lastDividend;
        this.fixedDividend = fixedDividend;
        this.parValue = parValue;
    }


    public String getCode() {
        return code;
    }

    public StockType getType() {
        return type;
    }

    public Double getFixedDividend() {
        return fixedDividend;
    }

    public Double getParValue() {
        return parValue;
    }

    public Double getLastDividend() {
        return lastDividend;
    }

    public List<StockTrade> getTradingDetails() {
        return tradingDetails;
    }

    public void setTradingDetails(List<StockTrade> tradingDetails) {
        this.tradingDetails = tradingDetails;
    }

    public Double getTickerPrice(){
        int lastItemIndex =  this.tradingDetails.size()-1;
        if(lastItemIndex >= 0){
            return this.tradingDetails.get(this.tradingDetails.size()-1).getTradingPrice();
        }else{
            return ZERO;
        }
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof StockData)) return false;

        StockData that = (StockData) other;

        return this.code.equals(that.code);
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (fixedDividend != null ? fixedDividend.hashCode() : 0);
        result = 31 * result + (lastDividend != null ? lastDividend.hashCode() : 0);
        return result;
    }
}
