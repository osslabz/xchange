package info.bitrich.xchangestream.bitmex;

import info.bitrich.xchangestream.bitmex.dto.BitmexTicker;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;

import java.util.concurrent.TimeUnit;

/**
 * @author Foat Akhmadeev
 * 31/05/2018
 */
public class BitmexTest {
    private static final CurrencyPair xbtUsd = CurrencyPair.XBT_USD;
    private static final int MIN_DATA_COUNT = 2;

    private StreamingExchange exchange;
    private BitmexStreamingMarketDataService streamingMarketDataService;

    @Before
    public void setup() {
        exchange = StreamingExchangeFactory.INSTANCE.createExchange(BitmexStreamingExchange.class.getName());
        awaitCompletable(exchange.connect());
        streamingMarketDataService = (BitmexStreamingMarketDataService)
                exchange.getStreamingMarketDataService();
    }

    @After
    public void tearDown() {
        awaitCompletable(exchange.disconnect());
    }

    private void awaitCompletable(Completable completable) {
        completable.test()
                .awaitDone(1, TimeUnit.MINUTES)
                .assertComplete()
                .assertNoErrors();
    }

    private <T> void awaitDataCount(Observable<T> observable) {
        observable.test()
                .assertSubscribed()
                .assertNoErrors()
                .awaitCount(BitmexTest.MIN_DATA_COUNT)
                .assertNoTimeout()
                .dispose();
    }

    @Test
    public void shouldReceiveBooks() {
        Observable<OrderBook> orderBookObservable = streamingMarketDataService.getOrderBook(xbtUsd);
        awaitDataCount(orderBookObservable);
    }

    @Test
    public void shouldReceiveRawTickers() {
        Observable<BitmexTicker> rawTickerObservable = streamingMarketDataService.getRawTicker(xbtUsd);
        awaitDataCount(rawTickerObservable);
    }

    @Test
    public void shouldReceiveTickers() {
        Observable<Ticker> tickerObservable = streamingMarketDataService.getTicker(xbtUsd);
        awaitDataCount(tickerObservable);
    }

    @Test
    public void shouldReceiveTrades() {
        Observable<Ticker> orderBookObservable = streamingMarketDataService.getTicker(xbtUsd);
        awaitDataCount(orderBookObservable);
    }
}
