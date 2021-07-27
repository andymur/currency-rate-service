package com.andymur.sme.challenge.provider;

import com.andymur.sme.challenge.model.CurrencyCouple;
import com.andymur.sme.challenge.provider.external.ExternalHistoricalRateProvider;
import com.andymur.sme.challenge.provider.external.ExternalInstantRateProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Container & Facade of different external components for different currency couples.
 */
public class RateProviderFacade implements InstantRateProvider, HistoricalRateProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateProviderFacade.class);

    private final Map<CurrencyCouple, ExternalInstantRateProvider> instantRateProviders;
    private final Map<CurrencyCouple, ExternalHistoricalRateProvider> historicalProviders;

    public RateProviderFacade() {
        this.instantRateProviders = new HashMap<>();
        this.historicalProviders = new HashMap<>();
    }

    public void addInstantRateProvider(final CurrencyCouple currencyCouple,
                                       final ExternalInstantRateProvider instantRateProvider) {
        instantRateProviders.put(currencyCouple, instantRateProvider);
    }

    public void addHistoricalRateProvider(final CurrencyCouple currencyCouple,
                                          final ExternalHistoricalRateProvider historicalRateProvider) {
        historicalProviders.put(currencyCouple, historicalRateProvider);
    }

    @Override
    public List<BigDecimal> getHistoricalRates(final LocalDate fromDate,
                                               final LocalDate toDate,
                                               final CurrencyCouple currencyCouple) {
        LOGGER.info("getHistoricalRates; facade requested for historical rates, fromDate = {}, toDate = {}, currencyCouple = {}",
                fromDate, toDate, currencyCouple);
        final ExternalHistoricalRateProvider historicalRateProvider = historicalProviders.get(currencyCouple);

        if (historicalRateProvider != null) {
            return historicalRateProvider.getRates(fromDate, toDate);
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<BigDecimal> getRate(final CurrencyCouple currencyCouple) {
        LOGGER.info("getRate; facade requested for rate of currencyCouple = {}", currencyCouple);
        final ExternalInstantRateProvider instantRateProvider = instantRateProviders.get(currencyCouple);
        if (instantRateProvider != null) {
            return instantRateProvider.getRate();
        }
        return Optional.empty();
    }
}
