package com.andymur.sme.challenge.provider;

import com.andymur.sme.challenge.cache.HistoricalRatesCache;
import com.andymur.sme.challenge.cache.InstantRateCache;
import com.andymur.sme.challenge.cache.model.RateHistoryKey;
import com.andymur.sme.challenge.model.CurrencyCouple;
import com.andymur.sme.challenge.provider.external.ExternalHistoricalRateProvider;
import com.andymur.sme.challenge.provider.external.ExternalInstantRateProvider;
import com.andymur.sme.challenge.reader.HistoricalRatesReader;
import com.andymur.sme.challenge.reader.HistoricalRatesReaderImpl;
import com.andymur.sme.challenge.reader.InstantRateReader;
import com.andymur.sme.challenge.reader.InstantRateReaderImpl;
import com.andymur.sme.challenge.updater.HistoricalRatesUpdater;
import com.andymur.sme.challenge.updater.HistoricalRatesUpdaterImpl;
import com.andymur.sme.challenge.updater.InstantRateUpdater;
import com.andymur.sme.challenge.updater.InstantRateUpdaterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Unit tests (with mocking some of the parts) of rate provider
 */
@ExtendWith(MockitoExtension.class)
class RateProviderTest {

    private static final Map<CurrencyCouple, BigDecimal> INSTANT_RATE_INSTANCE = new HashMap<>();
    private static final Map<RateHistoryKey, List<BigDecimal>> HISTORICAL_RATE_INSTANCE = new HashMap<>();

    private static final InstantRateCache INSTANT_RATE_CACHE = new InstantRateCache(INSTANT_RATE_INSTANCE);
    private static final HistoricalRatesCache HISTORICAL_RATES_CACHE = new HistoricalRatesCache(HISTORICAL_RATE_INSTANCE);

    private RateProvider rateProvider;

    private final RateProviderFacade providerFacade = new RateProviderFacade();

    /** mocked readers/updaters **/
    @Mock
    private InstantRateReader mockedInstantRateReader;

    @Mock
    private InstantRateUpdater mockedInstantRateUpdater;

    @Mock
    private HistoricalRatesReader mockedHistoricalRatesReader;

    @Mock
    private HistoricalRatesUpdater mockedHistoricalRatesUpdater;

    /** real readers/updaters **/

    private InstantRateReaderImpl realInstantRateReader = new InstantRateReaderImpl(INSTANT_RATE_CACHE);

    private InstantRateUpdaterImpl realInstantRateUpdater = new InstantRateUpdaterImpl(INSTANT_RATE_CACHE);

    private HistoricalRatesReaderImpl realHistoricalRatesReader = new HistoricalRatesReaderImpl(HISTORICAL_RATES_CACHE);

    private HistoricalRatesUpdaterImpl realHistoricalRatesUpdater = new HistoricalRatesUpdaterImpl(HISTORICAL_RATES_CACHE);


    /** external components are always mocked **/
    @Mock
    private ExternalInstantRateProvider externalInstantRateProvider;

    @Mock
    private ExternalHistoricalRateProvider externalHistoricalRateProvider;

    @BeforeEach
    public void setUp() {
        providerFacade.addInstantRateProvider(CurrencyCouple.XBTUSD, externalInstantRateProvider);
        providerFacade.addHistoricalRateProvider(CurrencyCouple.XBTUSD, externalHistoricalRateProvider);
    }

    /* Instant Rate Tests */

    /* we have no rate in cache, read it successfully from external, save it into cache */
    @Test
    public void testEmptyCacheOnFirstInstantRateRequest() {
        rateProvider = new RateProvider(providerFacade, mockedInstantRateReader, mockedInstantRateUpdater,
                mockedHistoricalRatesReader, mockedHistoricalRatesUpdater);

        Mockito.when(mockedInstantRateReader.getRate(CurrencyCouple.XBTUSD)).thenReturn(Optional.empty());
        Mockito.when(externalInstantRateProvider.getRate()).thenReturn(Optional.of(new BigDecimal("55000")));

        final BigDecimal rateValue = rateProvider.getRate(CurrencyCouple.XBTUSD).orElse(null);

        // cached!
        Mockito.verify(mockedInstantRateUpdater, Mockito.times(1)).updateRate(CurrencyCouple.XBTUSD, new BigDecimal("55000"));
        Assertions.assertEquals(new BigDecimal("55000"), rateValue);
    }

    /* we have rate in cache after successful previous rate */
    @Test
    public void testNonEmptyCacheOnFollowingInstantRateRequest() {
        rateProvider = new RateProvider(providerFacade, realInstantRateReader, realInstantRateUpdater,
                mockedHistoricalRatesReader, mockedHistoricalRatesUpdater);

        Mockito.when(externalInstantRateProvider.getRate()).thenReturn(Optional.of(new BigDecimal("55000")));

        final BigDecimal cacheValueAfterBeforeFirstRequest = realInstantRateReader.getRate(CurrencyCouple.XBTUSD).orElse(null);
        // making request
        rateProvider.getRate(CurrencyCouple.XBTUSD);
        final BigDecimal cacheValueAfterAfterFirstRequest = realInstantRateReader.getRate(CurrencyCouple.XBTUSD).orElse(null);

        Assertions.assertNull(cacheValueAfterBeforeFirstRequest);
        Assertions.assertEquals(new BigDecimal("55000"), cacheValueAfterAfterFirstRequest);
    }

    /* we have no rate in cache, cannot read from external */
    @Test
    public void testEmptyCacheOnFirstInstantRateRequestWithExternalError() {
        rateProvider = new RateProvider(providerFacade, mockedInstantRateReader, mockedInstantRateUpdater,
                mockedHistoricalRatesReader, mockedHistoricalRatesUpdater);

        Mockito.when(mockedInstantRateReader.getRate(CurrencyCouple.XBTUSD)).thenReturn(Optional.empty());
        Mockito.when(externalInstantRateProvider.getRate()).thenThrow(RuntimeException.class);

        final BigDecimal rateValue = rateProvider.getRate(CurrencyCouple.XBTUSD).orElse(null);
        Assertions.assertNull(rateValue);
    }

    /* we have no rate in cache, read it from external, cannot save it into the cache */
    @Test
    public void testEmptyCacheOnFirstInstantRateRequestWithCacheSaveError() {
        rateProvider = new RateProvider(providerFacade, mockedInstantRateReader, mockedInstantRateUpdater,
                mockedHistoricalRatesReader, mockedHistoricalRatesUpdater);

        Mockito.when(mockedInstantRateReader.getRate(CurrencyCouple.XBTUSD)).thenReturn(Optional.empty());
        Mockito.when(externalInstantRateProvider.getRate()).thenReturn(Optional.of(new BigDecimal("55000")));
        Mockito.doThrow(RuntimeException.class).when(mockedInstantRateUpdater).updateRate(CurrencyCouple.XBTUSD, new BigDecimal("55000"));

        final BigDecimal rateValue = rateProvider.getRate(CurrencyCouple.XBTUSD).orElse(null);
        Assertions.assertEquals(new BigDecimal("55000"), rateValue);
    }

    /* Historical Rate Tests */
    // we can imagine we should have very similar test methods for historical part of the story...
    // TODO: complete me!
}