package com.andymur.sme.challenge;

import com.andymur.sme.challenge.cache.Cache;
import com.andymur.sme.challenge.cache.CacheUpdater;
import com.andymur.sme.challenge.cache.InstantRateCache;
import com.andymur.sme.challenge.resource.model.InstantCurrencyRate;
import com.andymur.sme.challenge.util.RestClientHelper;
import com.andymur.sme.challenge.util.RestClientHelper.ResponseEntityPair;
import io.dropwizard.testing.DropwizardTestSupport;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.*;

import static com.andymur.sme.challenge.model.CurrencyCouple.XBTUSD;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Integration test for instance rates scenarios
 *
 * Attention!!
 *
 * BtcStub has empty value for the first request
 * following 10 requests values are [50k, 55k, 60k, 65k, 70k, 75k, 80k, 75k, 70k, 65k]
 * after it always returns empty value
 *
 * for more info see com.andymur.sme.challenge.stub.BtcRateStubProvider
 */
class InstantRateResourceIT {
    private static final String HOST = "localhost";
    private static final String CONFIG_PATH = "test-configuration.yml";

    private static final Logger LOGGER = LoggerFactory.getLogger(InstantRateResourceIT.class);

    private static final DropwizardTestSupport<CurrencyRateServiceConfiguration> SUPPORT =
            new DropwizardTestSupport<>(CurrencyRateTestApplication.class,
                    CONFIG_PATH);

    private static RestClientHelper REST_CLIENT_HELPER;

    @BeforeEach
    public void beforeClass() throws Exception {
        SUPPORT.before();
        REST_CLIENT_HELPER = RestClientHelper.of(HOST, SUPPORT.getLocalPort());
    }

    @AfterEach
    public void afterClass() {
        SUPPORT.after();
        Cache.INSTANT_RATE_INSTANCE.clear();
    }

    @Test
    public void fetchInstantRateHappyCase() {
        // empty value response
        ResponseEntityPair<InstantCurrencyRate> responseAndEntityPair = REST_CLIENT_HELPER.getInstantRate(XBTUSD);

        responseAndEntityPair = REST_CLIENT_HELPER.getInstantRate(XBTUSD);
        final InstantCurrencyRate bitcoinRateResult = responseAndEntityPair.getEntity();
        LOGGER.info("Rate successfully queried; result = {}", bitcoinRateResult);

        assertThat("Instant rate request has been failed",
                responseAndEntityPair.getHttpStatus(), is(HttpStatus.OK_200));

        Assertions.assertNotNull(bitcoinRateResult);
        Assertions.assertEquals(new BigDecimal("50000"), bitcoinRateResult.getValue());
    }

    @Test
    public void fetchInstantRateBadRequestCase() {
        // unknown currency couple
        final ResponseEntityPair<InstantCurrencyRate> responseAndEntityPair = REST_CLIENT_HELPER.getInstantRate("EURUSD");
        final InstantCurrencyRate eurUsdRateResult = responseAndEntityPair.getEntity();
        assertThat("Instant rate request should be bad in that case",
                responseAndEntityPair.getHttpStatus(), is(HttpStatus.BAD_REQUEST_400));
        Assertions.assertNull(eurUsdRateResult);
    }

    @Test
    public void fetchInstantRateNotFoundCase() {
        // for the first request response is empty value
        final ResponseEntityPair<InstantCurrencyRate> responseAndEntityPair = REST_CLIENT_HELPER.getInstantRate(XBTUSD);
        final InstantCurrencyRate eurUsdRateResult = responseAndEntityPair.getEntity();
        assertThat("Instant rate should be not found",
                responseAndEntityPair.getHttpStatus(), is(HttpStatus.NOT_FOUND_404));
        Assertions.assertNull(eurUsdRateResult);
    }

    @Test
    void fetchInstantRateAfterRateUpdaterTask() throws InterruptedException, ExecutionException, TimeoutException {
        // we are waiting some time so rate updater task can read from external component before the request
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        final ScheduledFuture<ResponseEntityPair<InstantCurrencyRate>> taskFuture = scheduledExecutorService.schedule(() -> {
            // after 5 seconds we're ready to read what updater task cached for us
            return REST_CLIENT_HELPER.getInstantRate(XBTUSD);
        }, 5, TimeUnit.SECONDS);

        final ResponseEntityPair<InstantCurrencyRate> responseAndEntityPair = taskFuture.get(10, TimeUnit.SECONDS);
        final InstantCurrencyRate bitcoinRateResult = responseAndEntityPair.getEntity();

        assertThat("Instant rate request has been failed",
                responseAndEntityPair.getHttpStatus(), is(HttpStatus.OK_200));
        Assertions.assertNotNull(bitcoinRateResult);
        // should be second value given by stub (read from cache)
        Assertions.assertEquals(new BigDecimal("50000"), bitcoinRateResult.getValue());
    }
}