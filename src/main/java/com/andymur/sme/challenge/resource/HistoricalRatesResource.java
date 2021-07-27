package com.andymur.sme.challenge.resource;

import com.andymur.sme.challenge.model.CurrencyCouple;
import com.andymur.sme.challenge.provider.RateProvider;
import com.andymur.sme.challenge.resource.exceptions.RateServiceException;
import com.andymur.sme.challenge.resource.model.HistoricalRatesData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Path("/historical")
@Produces(MediaType.APPLICATION_JSON)
public class HistoricalRatesResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoricalRatesResource.class);

    private final RateProvider rateProvider;

    public HistoricalRatesResource(final RateProvider rateProvider) {
        this.rateProvider = rateProvider;
    }

    @GET
    @Path("{fromDate}/{toDate}/{currencyCouple}")
    public HistoricalRatesData historicalData(@PathParam("fromDate") final LocalDate fromDate,
                                              @PathParam("toDate") final LocalDate toDate,
                                              @PathParam("currencyCouple") final CurrencyCouple currencyCouple) {
        validation(fromDate, toDate);
        LOGGER.info("historicalData; fetching historical rates for currencyCouple, fromDate = {}, toDate = {}, currencyCouple = {}",
                fromDate, toDate, currencyCouple);
        final List<BigDecimal> rates = rateProvider.getHistoricalRates(fromDate, toDate, currencyCouple);
        if (rates.isEmpty()) {
            throw new RateServiceException(404, "Historical rates have not been found");
        }
        final HistoricalRatesData result = new HistoricalRatesData(currencyCouple,
                createDateToValueMapping(fromDate, toDate, rates));
        LOGGER.info("historicalData; rate has been successfully fetched, result = {}", result);
        return result;
    }

    private void validation(final LocalDate fromDate,
                            final LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            LOGGER.error("From and to dates don't form correct interval; fromDate = {}, toDate = {}",
                    fromDate, toDate);
            throw new RateServiceException(400, "Dates from request don't form correct interval");
        }
    }

    private Map<LocalDate, BigDecimal> createDateToValueMapping(final LocalDate fromDate,
                                                                final LocalDate toDate,
                                                                final List<BigDecimal> values) {
        final Map<LocalDate, BigDecimal> result = new TreeMap<>();

        LocalDate currentDate = fromDate;
        final Iterator<BigDecimal> valueIterator = values.iterator();
        while (currentDate.isBefore(toDate)) {
            currentDate = currentDate.plusDays(1);
            if (!valueIterator.hasNext()) {
                LOGGER.error("Fetched values number is less than interval; fromDate = {}, toDate = {}, values = {}",
                        fromDate, toDate, values);
                throw new RateServiceException(500, "Fetched values number is less than interval");
            }
            result.put(currentDate, valueIterator.next());
        }

        return result;
    }
}
