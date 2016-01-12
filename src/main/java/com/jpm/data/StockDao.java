package com.jpm.data;

import com.jpm.model.StockData;
import com.jpm.model.StockData.StockType;
import com.jpm.model.StockTrade;

import java.util.*;
import java.util.stream.Collectors;


public class StockDao {
    private final Set<StockData> stocksData = new HashSet<StockData>() {
        {
            add(new StockData("TEA", 100.0, 0.0, StockType.COMMON));
            add(new StockData("POP", 100.0, 8.0, StockType.COMMON));
            add(new StockData("ALE", 60.0, 23.0, StockType.COMMON));
            add(new StockData("GIN", 100.0, 8.0, StockType.PREFERRED, 0.02));
            add(new StockData("JOE", 250.0, 13.0, StockType.COMMON));
        }
    };

    public StockData findStock(final String stockCode) {
        return stocksData.stream()
                .filter(stockData -> stockData.getCode().equals(stockCode))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public List<StockTrade> findTradesFor(String stockCode) {
        return findStock(stockCode).getTradingDetails();
    }

    public List<StockTrade> findTradesWithinTimeRange(String stockCode, long minutesInRange) {
        return findTradesFor(stockCode)
                .stream().filter(stockTrade -> stockTrade.getTradeDate().getTime() >= minutesInRange)
                .collect(Collectors.toList());
    }

    public Set<StockData> getAllStocks() {
        return stocksData;
    }

    public void tradeShares(String stockCode, StockTrade trade) {
        findStock(stockCode).getTradingDetails().add(trade);
    }

}