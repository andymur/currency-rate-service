package com.andymur.sme.challenge.provider;

import com.andymur.sme.challenge.model.CurrencyCouple;
import com.andymur.sme.challenge.reader.HistoricalRatesReader;
import com.andymur.sme.challenge.reader.InstantRateReader;
import com.andymur.sme.challenge.updater.HistoricalRatesUpdater;
import com.andymur.sme.challenge.updater.InstantRateUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Crucial component, part of rest endpoints.
 * Controller might be a better name, has very same algorithm of handling both historical & instant requests.
 * We might generalize it.
 */
public class RateProvider implements InstantRateProvider, HistoricalRateProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateProvider.class);

    // for historical requests
    private final RateProviderFacade providerFacade;

    // readers from cache
    private final InstantRateReader instantRateReader;
    private final HistoricalRatesReader historicalRatesReader;

    private final InstantRateUpdater instantRateUpdater;
    private final HistoricalRatesUpdater historicalRatesUpdater;

    public RateProvider(final RateProviderFacade rateProviderFacade,
                        final InstantRateReader instantRateReader,
                        final InstantRateUpdater instantRateUpdater,
                        final HistoricalRatesReader historicalRatesReader,
                        final HistoricalRatesUpdater historicalRatesUpdater) {
        this.providerFacade = rateProviderFacade;
        this.instantRateReader = instantRateReader;
        this.instantRateUpdater = instantRateUpdater;
        this.historicalRatesReader = historicalRatesReader;
        this.historicalRatesUpdater = historicalRatesUpdater;
    }

    @Override
    public Optional<BigDecimal> getRate(final CurrencyCouple currencyCouple) {
        return instantRateReader.getRate(currencyCouple).or(() -> {
            Optional<BigDecimal> newRate = Optional.empty();
            try {
                newRate = providerFacade.getRate(currencyCouple);
                newRate.ifPresent(rateValue -> instantRateUpdater.updateRate(currencyCouple, rateValue));
            } catch (Exception e) {
                // don't want to fail because of underlying layer
                //TODO: better differentiate between provider facade is not able to fetch data and we cannot cache it
                LOGGER.warn("We failed to fetch the rate or cache it", e);
            }
            return newRate;
        });
    }

    @Override
    public List<BigDecimal> getHistoricalRates(final LocalDate fromDate,
                                               final LocalDate toDate,
                                               final CurrencyCouple currencyCouple) {

        // read from cache
        List<BigDecimal> historicalRates = historicalRatesReader
                .getRates(fromDate, toDate, currencyCouple);

        if (historicalRates.isEmpty()) {
            try {
                // read from external component
                historicalRates = providerFacade.getHistoricalRates(fromDate, toDate, currencyCouple);
                historicalRatesUpdater.updateHistoricalRates(fromDate, toDate, currencyCouple, historicalRates);
            } catch (Exception e) {
                // don't want to fail because of underlying layer
                //TODO: better differentiate between provider facade is not able to fetch data and we cannot cache it
                LOGGER.warn("We failed to fetch the historical rates or cache it", e);
            }
        }

        return historicalRates;
    }
}
