package com.jpm.service;

import com.jpm.data.StockDao;
import com.jpm.model.StockData;
import com.jpm.model.StockTrade;

import java.util.ArrayList;
import java.util.List;

import static com.jpm.model.StockData.ZERO;

public class StockExchange {

    private StockDao stockDao;

    public Double getDividendYield(String stockCode) {
        StockData stock = stockDao.findStock(stockCode);
        return getDividendYield(stock);
    }

    private Double getDividendYield(StockData stock) {

        double tickerPrice = stock.getTickerPrice();
        if (tickerPrice > ZERO) {
            if (stock.getType() == StockData.StockType.COMMON) {
                return stock.getLastDividend() / tickerPrice;
            } else {
                return (stock.getFixedDividend() * stock.getParValue()) / tickerPrice;
            }
        }
        return ZERO;
    }

    public Double getPERatio(String stockCode) {
        StockData stock = stockDao.findStock(stockCode);
        double tickerPrice = stock.getTickerPrice();
        if (tickerPrice > ZERO) {
            return tickerPrice / getDividendYield(stock);
        }
        return ZERO;
    }

    public Double getStockPrice(String stockCode) {
        return getStockPriceInRange(stockCode, 15);
    }

    private Double getStockPriceInRange(String stockCode, int minutesRange) {
        final long tradedMinutesRange = minutesRange > 0 ? System.currentTimeMillis() - minutesRange * 60 * 1000L : minutesRange;
        List<StockTrade> stockTrades = stockDao.findTradesWithinTimeRange(stockCode, tradedMinutesRange);

        double totalShares = ZERO;
        double totalSharePrice = ZERO;
        for (StockTrade trade : stockTrades) {
            totalSharePrice += (trade.getTradingPrice() * trade.getQuantity());
            totalShares += trade.getQuantity();
        }

        if (totalShares > ZERO) {
            return totalSharePrice / totalShares;
        }

        return ZERO;
    }

    public Double getGbceAllShareIndex() {
        List<Double> stockPrices = new ArrayList<>();
        stockDao.getAllStocks().forEach(stock -> {
            double stockPrice = getStockPriceInRange(stock.getCode(), 0);
            if (stockPrice > 0) {
                stockPrices.add(stockPrice);
            }
        });
        return geometricMean(stockPrices);
    }

    private Double geometricMean(List<Double> priceList) {
        double product = 1.0;
        for (Double price : priceList) {
            product = product * price;
        }
        return Math.pow(product, 1.0 / priceList.size());
    }

    public void tradeShares(String stockCode, StockTrade stockTrade) {
        stockDao.tradeShares(stockCode, stockTrade);
    }

    public void setStockDao(StockDao dao) {
        this.stockDao = dao;
    }

}
