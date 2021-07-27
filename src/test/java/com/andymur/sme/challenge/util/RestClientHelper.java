package com.andymur.sme.challenge.util;

import com.andymur.sme.challenge.model.CurrencyCouple;
import com.andymur.sme.challenge.resource.model.HistoricalRatesData;
import com.andymur.sme.challenge.resource.model.HistoricalRatesRequest;
import com.andymur.sme.challenge.resource.model.InstantCurrencyRate;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RestClientHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientHelper.class);
    private static final String INSTANT_RATE_URL_PATTERN = "http://%s:%d/instant/%s/";
    private static final String HISTORICAL_RATES_URL_PATTERN = "http://%s:%d/historical/%s/%s/%s/";

    private final Client restClient = new JerseyClientBuilder().build();

    private final String host;
    private final int port;

    private RestClientHelper(final String host,
                             final int port) {
        this.host = host;
        this.port = port;
    }

    public static RestClientHelper of(final String host,
                                      final int port) {
        return new RestClientHelper(host, port);
    }

    public ResponseEntityPair<InstantCurrencyRate> getInstantRate(final CurrencyCouple currencyCouple) {
        return getInstantRate(currencyCouple.toString());
    }

    public ResponseEntityPair<InstantCurrencyRate> getInstantRate(final String currencyCouple) {
        LOGGER.info("Fetching instant rate in the test; currencyCouple = {}", currencyCouple);
        Response response = instantRateRequestResponse(currencyCouple.toString());
        if (response.getStatus() == HttpStatus.OK_200) {
            return ResponseEntityPair.of(response.getStatus(), response.readEntity(InstantCurrencyRate.class));
        } else {
            return ResponseEntityPair.of(response.getStatus());
        }
    }

    public ResponseEntityPair<HistoricalRatesData> getHistoricalRates(final LocalDate fromDate,
                                                  final LocalDate toDate,
                                                  final CurrencyCouple currencyCouple) {
        LOGGER.info("Fetching historical rates in the test; fromDate = {}, toDate = {}, currencyCouple = {}",
                fromDate, toDate, currencyCouple);
        Response response = historicalRatesRequestResponse(HistoricalRatesRequest.of(fromDate, toDate, currencyCouple));
        if (response.getStatus() == HttpStatus.OK_200) {
            return ResponseEntityPair.of(response.getStatus(), response.readEntity(HistoricalRatesData.class));
        } else {
            return ResponseEntityPair.of(response.getStatus());
        }
    }

    public Response instantRateRequestResponse(final String currencyCouple) {
        return restClient.target(
                String.format(INSTANT_RATE_URL_PATTERN, host, port, currencyCouple))
                .request()
                .get();
    }

    public Response historicalRatesRequestResponse(final HistoricalRatesRequest historicalRatesRequest) {
        return restClient.target(
                String.format(HISTORICAL_RATES_URL_PATTERN, host, port, historicalRatesRequest.getFromDate(),
                        historicalRatesRequest.getToDate(), historicalRatesRequest.getCurrencyCouple()))
                .request()
                .get();
    }

    public static class ResponseEntityPair<E> {
        private final int httpStatus;
        private final E entity;

        private ResponseEntityPair(final int httpStatus, final E entity) {
            this.httpStatus = httpStatus;
            this.entity = entity;
        }

        public int getHttpStatus() {
            return httpStatus;
        }

        public E getEntity() {
            return entity;
        }

        public static <E> ResponseEntityPair<E> of(int httpStatus, E entity) {
            return new ResponseEntityPair<>(httpStatus, entity);
        }

        public static <E> ResponseEntityPair<E> of(int httpStatus) {
            return new ResponseEntityPair<>(httpStatus, null);
        }
    }
}
