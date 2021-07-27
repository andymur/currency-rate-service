package com.andymur.sme.challenge;

import com.andymur.sme.challenge.cache.HistoricalRatesCache;
import com.andymur.sme.challenge.cache.InstantRateCache;
import com.andymur.sme.challenge.cache.model.RateHistoryKey;
import com.andymur.sme.challenge.model.CurrencyCouple;
import com.andymur.sme.challenge.provider.RateProvider;
import com.andymur.sme.challenge.provider.RateProviderFacade;
import com.andymur.sme.challenge.provider.external.BtcHistoricalRatesProvider;
import com.andymur.sme.challenge.provider.external.BtcRateProvider;
import com.andymur.sme.challenge.reader.HistoricalRatesReader;
import com.andymur.sme.challenge.reader.HistoricalRatesReaderImpl;
import com.andymur.sme.challenge.reader.InstantRateReader;
import com.andymur.sme.challenge.reader.InstantRateReaderImpl;
import com.andymur.sme.challenge.resource.HistoricalRatesResource;
import com.andymur.sme.challenge.resource.InstantRateResource;
import com.andymur.sme.challenge.resource.converters.LocalDateParamConverterProvider;
import com.andymur.sme.challenge.resource.exceptions.RateServiceExceptionMapper;
import com.andymur.sme.challenge.updater.HistoricalRatesUpdater;
import com.andymur.sme.challenge.updater.HistoricalRatesUpdaterImpl;
import com.andymur.sme.challenge.updater.InstantRateUpdater;
import com.andymur.sme.challenge.updater.InstantRateUpdaterImpl;
import io.dropwizard.Application;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.jobs.JobsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.andymur.sme.challenge.cache.Cache.HISTORICAL_RATE_INSTANCE;
import static com.andymur.sme.challenge.cache.Cache.INSTANT_RATE_INSTANCE;

public class CurrencyRateApplication extends Application<CurrencyRateServiceConfiguration> {

    public static void main(String[] args) throws Exception {
        new CurrencyRateApplication().run(args);
    }

    @Override
    public String getName() {
        return "CurrencyRate-App";
    }

    @Override
    public void initialize(final Bootstrap<CurrencyRateServiceConfiguration> bootstrap) {
        super.initialize(bootstrap);
        // load configuration from resources
        bootstrap.setConfigurationSourceProvider(
                new ResourceConfigurationSourceProvider());
        // adding tasks
        bootstrap.addBundle(new JobsBundle(createRateUpdaterTask()));
    }

    @Override
    public void run(CurrencyRateServiceConfiguration currencyRateServiceConfiguration,
                    Environment environment) throws Exception {

        final RateProvider rateProvider = createRateProvider();
        InstantRateResource rateResource = new InstantRateResource(rateProvider);
        HistoricalRatesResource historicalRatesResource = new HistoricalRatesResource(rateProvider);

        environment.jersey().register(historicalRatesResource);
        environment.jersey().register(rateResource);
        environment.jersey().register(new LocalDateParamConverterProvider());
        environment.jersey().register(new RateServiceExceptionMapper());
    }

    private RateProvider createRateProvider() {
        return new RateProvider(createRateProviderFacade(),
                createInstantRateReader(), createInstantRateUpdater(),
                createHistoricalRatesReader(), createHistoricalRatesUpdater());
    }

    private RateUpdaterTask createRateUpdaterTask() {
        return new RateUpdaterTask(createRateProviderFacade(), createInstantRateUpdater());
    }

    /**
     * @return configured faced with different external rate providers (instant & historical) for different ccy couples
     */
    protected RateProviderFacade createRateProviderFacade() {
        final RateProviderFacade providerFacade = new RateProviderFacade();
        providerFacade.addInstantRateProvider(CurrencyCouple.XBTUSD, new BtcRateProvider());
        providerFacade.addHistoricalRateProvider(CurrencyCouple.XBTUSD, new BtcHistoricalRatesProvider());
        return providerFacade;
    }

    private InstantRateReader createInstantRateReader() {
        return new InstantRateReaderImpl(createInstantRateCache());
    }

    private InstantRateUpdater createInstantRateUpdater() {
        return new InstantRateUpdaterImpl(createInstantRateCache());
    }

    private InstantRateCache createInstantRateCache() {
        return new InstantRateCache(INSTANT_RATE_INSTANCE);
    }

    private HistoricalRatesReader createHistoricalRatesReader() {
        return new HistoricalRatesReaderImpl(createHistoricalRatesCache());
    }

    private HistoricalRatesUpdater createHistoricalRatesUpdater() {
        return new HistoricalRatesUpdaterImpl(createHistoricalRatesCache());
    }

    private HistoricalRatesCache createHistoricalRatesCache() {
        return new HistoricalRatesCache(HISTORICAL_RATE_INSTANCE);
    }
}
