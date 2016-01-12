package com.jpm.model;

import java.util.Calendar;
import java.util.Date;


public class StockTrade {
    public enum TradeType {BUY, SELL};
    private Double tradingPrice;
    private int quantity;
    private Date tradeDate;
    private TradeType tradeType;

    public StockTrade(){
    }

    public StockTrade(Double tradingPrice, int quantity, TradeType tradeType){
        this(tradingPrice, quantity, tradeType, Calendar.getInstance().getTime());
    }

    public StockTrade(Double tradingPrice, int quantity, TradeType tradeType, Date tradeDate){
        this.quantity = quantity;
        this.tradingPrice = tradingPrice;
        this.tradeType = tradeType;
        this.tradeDate = tradeDate;
    }

    public void setTradingPrice(Double tradingPrice) {
        this.tradingPrice = tradingPrice;
    }

    public Double getTradingPrice() {
        return tradingPrice;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public Double getTotalPrice(){
        return quantity * tradingPrice;
    }
}
