package com.andymur.sme.challenge.resource;

import com.andymur.sme.challenge.model.CurrencyCouple;
import com.andymur.sme.challenge.provider.RateProvider;
import com.andymur.sme.challenge.resource.exceptions.RateServiceException;
import com.andymur.sme.challenge.resource.model.InstantCurrencyRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/instant")
@Produces(MediaType.APPLICATION_JSON)
public class InstantRateResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstantRateResource.class);

    private final RateProvider rateProvider;

    public InstantRateResource(final RateProvider rateProvider) {
        this.rateProvider = rateProvider;
    }

    @GET
    @Path("{currencyCouple}")
    public InstantCurrencyRate instantRate(@PathParam("currencyCouple") final CurrencyCouple currencyCouple) {
        LOGGER.info("instantRate; fetching rate for currencyCouple = {}", currencyCouple);
        final InstantCurrencyRate result = new InstantCurrencyRate();
        result.setCurrencyCouple(currencyCouple);
        result.setValue(rateProvider.getRate(currencyCouple)
                .orElseThrow(() -> new RateServiceException(404, "Rate has not been found")));
        LOGGER.info("instantRate; rate has been successfully fetched, result = {}", result);
        return result;
    }
}
