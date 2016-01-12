import com.jpm.data.StockDao;
import com.jpm.model.StockData;
import com.jpm.model.StockTrade;
import com.jpm.service.StockExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.IsEqual.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StockExchangeUnitTest {

    private StockExchange stockExchange;
    @Mock
    private StockDao stockDao;

    @Before
    public void setUp() {
        stockExchange = new StockExchange();
        stockExchange.setStockDao(stockDao);
    }

    @Test
    public void getDividendYieldForCommonTest() {
        StockData stock = mock(StockData.class);
        when(stockDao.findStock(anyString())).thenReturn(stock);
        when(stock.getTickerPrice()).thenReturn(200.00);
        when(stock.getLastDividend()).thenReturn(10.0);
        when(stock.getType()).thenReturn(StockData.StockType.COMMON);
        double dividendYield = stockExchange.getDividendYield(anyString());
        assertThat(dividendYield, equalTo(10.0 / 200.00));
    }

    @Test
    public void getDividendYieldForPreferredTest() {
        StockData stockData = mock(StockData.class);
        when(stockDao.findStock(anyString())).thenReturn(stockData);
        when(stockData.getTickerPrice()).thenReturn(200.00);
        when(stockData.getType()).thenReturn(StockData.StockType.PREFERRED);
        when(stockData.getFixedDividend()).thenReturn(10.0);
        when(stockData.getParValue()).thenReturn(100.0);
        double dividendYield = stockExchange.getDividendYield(anyString());
        assertThat(dividendYield, equalTo((10.0 * 100.0) / 200.00));
    }

    @Test
    public void getDividendYieldWithZeroPriceTest() {
        StockData stockData = mock(StockData.class);
        when(stockDao.findStock(anyString())).thenReturn(stockData);
        when(stockData.getTickerPrice()).thenReturn(0.0);
        double dividendYield = stockExchange.getDividendYield(anyString());
        assertThat(dividendYield, equalTo(0.0));
    }

    @Test
    public void getPERatioForCommonTest() {
        StockData stockData = mock(StockData.class);
        when(stockDao.findStock(anyString())).thenReturn(stockData);
        when(stockData.getTickerPrice()).thenReturn(100.0);
        when(stockData.getType()).thenReturn(StockData.StockType.COMMON);
        when(stockData.getLastDividend()).thenReturn(10.0);
        double peRatio = stockExchange.getPERatio(anyString());
        assertThat(peRatio, equalTo(1000.0));
    }

    @Test
    public void getPERatioForPreferredTest() {
        StockData stockData = mock(StockData.class);
        when(stockDao.findStock(anyString())).thenReturn(stockData);
        when(stockData.getTickerPrice()).thenReturn(200.00);
        when(stockData.getType()).thenReturn(StockData.StockType.PREFERRED);
        when(stockData.getFixedDividend()).thenReturn(10.00);
        when(stockData.getParValue()).thenReturn((100.00));
        double peRatio = stockExchange.getPERatio(anyString());
        assertThat(peRatio, equalTo(40.00));
    }

    @Test
    public void getPERatioWithZeroPriceTest() {
        StockData stockData = mock(StockData.class);
        when(stockDao.findStock(anyString())).thenReturn(stockData);
        when(stockData.getTickerPrice()).thenReturn(0.0);
        double peRatio = stockExchange.getPERatio(anyString());
        assertThat(peRatio, equalTo(0.0));
    }

    @Test
    public void getStockPriceTest() {
        List<StockTrade> stockTradeList = getStockTrades();
        when(stockDao.findTradesWithinTimeRange(anyString(), anyLong())).thenReturn(stockTradeList);
        double stockPrice = stockExchange.getStockPrice("ALE");
        assertThat(stockPrice, equalTo(110.00));

    }

    public List<StockTrade> getStockTrades() {
        return  Arrays.asList(
                new StockTrade(100.00, 2, StockTrade.TradeType.BUY, null),
                new StockTrade(120.00, 2, StockTrade.TradeType.SELL, null)
        );
    }

    @Test
    public void getGbceAllShareIndexTest() {
        Set<StockData> stocksData = new HashSet<StockData>() {
            {
                add(new StockData("TEA", 100.0, 0.0, StockData.StockType.COMMON));
                add(new StockData("POP", 100.0, 8.0, StockData.StockType.COMMON));
            }};
        when(stockDao.getAllStocks()).thenReturn(stocksData);
        when(stockDao.findTradesWithinTimeRange(anyString(), anyLong())).thenReturn(getStockTrades());
        Double mean = stockExchange.getGbceAllShareIndex();
        assertThat(mean, equalTo(110.00));

    }

}
