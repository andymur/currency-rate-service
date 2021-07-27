package com.andymur.sme.challenge;

import com.andymur.sme.challenge.model.CurrencyCouple;
import com.andymur.sme.challenge.provider.InstantRateProvider;
import com.andymur.sme.challenge.updater.InstantRateUpdater;
import io.dropwizard.jobs.Job;
import io.dropwizard.jobs.annotations.Every;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

@Every
public class RateUpdaterTask extends Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateUpdaterTask.class);

    final InstantRateProvider rateProvider;
    final InstantRateUpdater rateUpdater;

    public RateUpdaterTask(final InstantRateProvider rateProvider,
                           final InstantRateUpdater rateUpdater) {
        this.rateProvider = rateProvider;
        this.rateUpdater = rateUpdater;
    }

    @Override
    public void doJob(JobExecutionContext context) {
        LOGGER.info("doJob; starting the job");
        //TODO: currency couple list must be configurable
        Arrays.stream(CurrencyCouple.values()).forEach(this::updateRate);
    }

    public void updateRate(final CurrencyCouple currencyCouple) {
        LOGGER.info("updateRate; updating rate of the currency = {}", currencyCouple);
        try {
            rateProvider.getRate(currencyCouple)
                    .ifPresentOrElse(rate -> {
                        LOGGER.info("updateRate; caching rate = {} for currencyCouple = {}", rate, currencyCouple);
                        rateUpdater.updateRate(currencyCouple, rate);
                    }, () -> LOGGER.info("updateRate; external component has no rate for currencyCouple = {}", currencyCouple));
        } catch (Exception e) {
            LOGGER.error("updateRate; error occurred while updating rate", e);
        }
    }
}
