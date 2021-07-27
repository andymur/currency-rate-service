package com.andymur.sme.challenge;

import com.andymur.sme.challenge.model.CurrencyCouple;
import com.andymur.sme.challenge.resource.model.HistoricalRatesData;
import com.andymur.sme.challenge.util.RestClientHelper;
import io.dropwizard.testing.DropwizardTestSupport;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Integration test for historical rates scenarios
 */
public class HistoricalRatesResourceIT {

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
    }

    @Test
    public void fetchHistoricalRatesHappyCase() {
        final RestClientHelper.ResponseEntityPair<HistoricalRatesData> responseAndEntityPair = REST_CLIENT_HELPER.getHistoricalRates(
                LocalDate.now().minusDays(3),
                LocalDate.now().minusDays(1),
                CurrencyCouple.XBTUSD);

        HistoricalRatesData historicalRatesData = responseAndEntityPair.getEntity();

        assertThat("Historical rates request has been failed",
                responseAndEntityPair.getHttpStatus(), is(HttpStatus.OK_200));

        LOGGER.info("Rate successfully queried; result = {}", historicalRatesData);
        Assertions.assertNotNull(historicalRatesData);
    }

    @Test
    void fetchHistoricalRatesBadRequestCase() {
        // going to use crazy interval here
        final RestClientHelper.ResponseEntityPair<HistoricalRatesData> responseAndEntityPair = REST_CLIENT_HELPER.getHistoricalRates(
                LocalDate.of(2005, 1, 1),
                LocalDate.of(2000, 1, 1),
                CurrencyCouple.XBTUSD);

        HistoricalRatesData historicalRatesData = responseAndEntityPair.getEntity();

        assertThat("Historical rates request must find nothing",
                responseAndEntityPair.getHttpStatus(), is(HttpStatus.BAD_REQUEST_400));

        Assertions.assertNull(historicalRatesData);
    }

    @Test
    public void fetchHistoricalRatesNotFoundCase() {
        final RestClientHelper.ResponseEntityPair<HistoricalRatesData> responseAndEntityPair = REST_CLIENT_HELPER.getHistoricalRates(
                LocalDate.of(2000, 1, 1),
                LocalDate.of(2005, 1, 1),
                CurrencyCouple.XBTUSD);

        HistoricalRatesData historicalRatesData = responseAndEntityPair.getEntity();

        assertThat("Historical rates request must find nothing",
                responseAndEntityPair.getHttpStatus(), is(HttpStatus.NOT_FOUND_404));

        Assertions.assertNull(historicalRatesData);
    }
}
