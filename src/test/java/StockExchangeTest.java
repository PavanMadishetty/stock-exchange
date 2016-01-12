import com.jpm.data.StockDao;
import com.jpm.model.StockData;
import com.jpm.model.StockTrade;
import com.jpm.service.StockExchange;
import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;


public class StockExchangeTest {

    public StockExchange stockExchange;
    public StockDao stockDao;

    private Calendar currentTime;

    @Before
    public void setUp() {
        stockExchange = new StockExchange();
        stockDao = new StockDao();
        stockExchange.setStockDao(stockDao);
        currentTime = Calendar.getInstance();
    }

    @Test
    public void findSockForValidStockCodeTest() {
        assertNotNull(stockDao);

        StockData stock = new StockData("JOE", 250.0, 13.0, StockData.StockType.COMMON);
        StockData stockData = stockDao.findStock(stock.getCode());
        assertNotNull(stockData);
        assertThat(stockData.getType(), IsEqual.equalTo(stock.getType()));
        assertThat(stockData, IsEqual.equalTo(stock));

    }

    @Test(expected = IllegalArgumentException.class)
    public void findSockForInvalidStockCodeTest() {
        assertNotNull(stockDao);

        StockData stock = new StockData("TE", 250.0, 13.0, StockData.StockType.COMMON);
        StockData stockData = stockDao.findStock(stock.getCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSockForEmptyStringStockCodeTest() {
        assertNotNull(stockDao);

        StockData stock = new StockData("", 250.0, 13.0, StockData.StockType.COMMON);
        StockData stockData = stockDao.findStock(stock.getCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findSockForNullStockCodeTest() {
        assertNotNull(stockDao);

        StockData stock = new StockData(null, 250.0, 13.0, StockData.StockType.COMMON);
        StockData stockData = stockDao.findStock(stock.getCode());
    }

    @Test
    public void calculateDividendYieldForCommon() {
        assertNotNull(stockExchange);
        assertNotNull(stockDao);

        StockTrade trade = new StockTrade(110.00, 2, StockTrade.TradeType.BUY);
        stockExchange.tradeShares("ALE", trade);
        double dividendYield = stockExchange.getDividendYield("ALE");
        assertTrue(dividendYield >= 0.0);
        assertThat(dividendYield, IsEqual.equalTo(23.00/110.00));

    }

    @Test
    public void calculateDividendYieldForPreferred() {
        assertNotNull(stockExchange);
        assertNotNull(stockDao);

        StockTrade trade = new StockTrade(120.00, 2, StockTrade.TradeType.SELL);
        stockExchange.tradeShares("GIN", trade);
        double dividendYield = stockExchange.getDividendYield("GIN");
        assertTrue(dividendYield >= 0.0);
        assertThat(dividendYield, IsEqual.equalTo(0.02*100/120.00));

    }


    @Test
    public void calculatePERatioTest() {
        assertNotNull(stockExchange);
        assertNotNull(stockDao);

        StockTrade trade = new StockTrade(120.00, 2, StockTrade.TradeType.BUY);
        stockExchange.tradeShares("POP", trade);
        double peRatio = stockExchange.getPERatio("POP");
        assertThat(peRatio, IsEqual.equalTo(120.00/(8.0/120.00)));
    }

    @Test
    public void calculateTickerPriceBelow15MinRangeTest() {
        assertNotNull(stockExchange);
        assertNotNull(stockDao);

        List<StockTrade> tradeList = Arrays.asList(
                new StockTrade(110.00, 2, StockTrade.TradeType.BUY, getTimeLessThan(14)),
                new StockTrade(120.00, 2, StockTrade.TradeType.SELL, getTimeLessThan(10)),
                new StockTrade(130.00, 2, StockTrade.TradeType.BUY, getTimeLessThan(4)),
                new StockTrade(125.00, 2, StockTrade.TradeType.SELL, getTimeLessThan(2))
        );
        for(StockTrade trade : tradeList){
            stockExchange.tradeShares("ALE", trade);
        }
        //end of data prep
        double stockPrice = stockExchange.getStockPrice("ALE");
        assertThat(stockPrice, IsEqual.equalTo((110.00*2+120.00*2+130.00*2+125.00*2)/8.0));

    }

    @Test
    public void calculateTickerPriceAbove15MinRangeTest() {
        assertNotNull(stockExchange);
        assertNotNull(stockDao);

        List<StockTrade> tradeList = Arrays.asList(
                new StockTrade(100.00, 2, StockTrade.TradeType.BUY, getTimeLessThan(16)),
                new StockTrade(110.00, 2, StockTrade.TradeType.BUY, getTimeLessThan(15)),
                new StockTrade(120.00, 2, StockTrade.TradeType.SELL, getTimeLessThan(10)),
                new StockTrade(130.00, 2, StockTrade.TradeType.BUY, getTimeLessThan(4)),
                new StockTrade(125.00, 2, StockTrade.TradeType.SELL, getTimeLessThan(2))
        );
        for(StockTrade trade : tradeList){
            stockExchange.tradeShares("ALE", trade);
        }
        //end of data prep
        double stockPrice = stockExchange.getStockPrice("ALE");
        assertThat(stockPrice, IsEqual.equalTo((120.00*2+130.00*2+125.00*2)/6.0));

    }

    private Date getTimeLessThan(int minutes){
        Calendar cal = (Calendar)currentTime.clone();
        cal.add(Calendar.MINUTE, -minutes);
        return cal.getTime();
    }

    @Test
    public void calculateGeometricMeanTest() {
        assertNotNull(stockExchange);
        assertNotNull(stockDao);

        List<StockTrade> tradeList = Arrays.asList(
                new StockTrade(110.00, 2, StockTrade.TradeType.BUY),
                new StockTrade(120.00, 2, StockTrade.TradeType.SELL),
                new StockTrade(130.00, 2, StockTrade.TradeType.BUY),
                new StockTrade(125.00, 2, StockTrade.TradeType.SELL)
        );
        for(StockTrade trade : tradeList){
            stockExchange.tradeShares("ALE", trade);
            stockExchange.tradeShares("GIN", trade);
        }
        //end of data prep
        double shareIndex = stockExchange.getGbceAllShareIndex();
        assertThat(shareIndex, IsEqual.equalTo(Math.pow(121.25*121.25, 0.5)));

    }

}
