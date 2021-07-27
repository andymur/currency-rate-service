package com.andymur.sme.challenge;

import com.andymur.sme.challenge.cache.InstantRateCache;
import com.andymur.sme.challenge.model.CurrencyCouple;
import com.andymur.sme.challenge.provider.RateProviderFacade;
import com.andymur.sme.challenge.provider.external.ExternalHistoricalRateProvider;
import com.andymur.sme.challenge.provider.external.ExternalInstantRateProvider;
import com.andymur.sme.challenge.reader.InstantRateReader;
import com.andymur.sme.challenge.reader.InstantRateReaderImpl;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests (with mocking some of the parts) of updater rate task
 */
@ExtendWith(MockitoExtension.class)
class RateUpdaterTaskTest {

    private static final InstantRateCache INSTANT_RATE_CACHE = new InstantRateCache(new HashMap<>());

    private final RateProviderFacade providerFacade = new RateProviderFacade();

    private RateUpdaterTask rateUpdaterTask;

    @Mock
    private InstantRateUpdater mockedInstantRateUpdater;

    private InstantRateUpdater realInstantRateUpdater = new InstantRateUpdaterImpl(INSTANT_RATE_CACHE);

    private InstantRateReader realInstantRateReader = new InstantRateReaderImpl(INSTANT_RATE_CACHE);

    @Mock
    private ExternalInstantRateProvider externalInstantRateProvider;

    @Mock
    private ExternalHistoricalRateProvider externalHistoricalRateProvider;

    @BeforeEach
    public void setUp() {
        providerFacade.addInstantRateProvider(CurrencyCouple.XBTUSD, externalInstantRateProvider);
        providerFacade.addHistoricalRateProvider(CurrencyCouple.XBTUSD, externalHistoricalRateProvider);
    }

    /* cannot read from external (task fails) */
    @Test
    public void testRunTaskExternalComponentBroken() {
        Mockito.when(externalInstantRateProvider.getRate()).thenThrow(RuntimeException.class);
        rateUpdaterTask = new RateUpdaterTask(providerFacade, mockedInstantRateUpdater);

        rateUpdaterTask.updateRate(CurrencyCouple.XBTUSD);
        Mockito.verify(mockedInstantRateUpdater, Mockito.never()).updateRate(Mockito.any(CurrencyCouple.class), Mockito.any(BigDecimal.class));
    }

     /* read from external cannot save into cache (task fails) */
     @Test
     public void testRunTaskCachUpdateBroken() {
         rateUpdaterTask = new RateUpdaterTask(providerFacade, mockedInstantRateUpdater);

         Mockito.when(externalInstantRateProvider.getRate()).thenReturn(Optional.of(new BigDecimal("55000")));
         Mockito.doThrow(RuntimeException.class).when(mockedInstantRateUpdater).updateRate(CurrencyCouple.XBTUSD, new BigDecimal("55000"));

         rateUpdaterTask.updateRate(CurrencyCouple.XBTUSD);
         Mockito.verify(mockedInstantRateUpdater, Mockito.times(1)).updateRate(CurrencyCouple.XBTUSD, new BigDecimal("55000"));
     }

     /* successfully read from external and saves into cache */
     @Test
     public void testRunTaskUpdatesTheCache() {
         rateUpdaterTask = new RateUpdaterTask(providerFacade, realInstantRateUpdater);

         Mockito.when(externalInstantRateProvider.getRate()).thenReturn(Optional.of(new BigDecimal("55000")));

         rateUpdaterTask.updateRate(CurrencyCouple.XBTUSD);
         final BigDecimal rateValue = realInstantRateReader.getRate(CurrencyCouple.XBTUSD).orElse(null);
         Assertions.assertEquals(new BigDecimal("55000"), rateValue);
     }
}