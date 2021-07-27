package com.andymur.sme.challenge.reader;

import com.andymur.sme.challenge.model.CurrencyCouple;

import java.math.BigDecimal;
import java.util.Optional;

public interface InstantRateReader {
    /**
     * Reads currency couple's rate from some source of data
     * @param currencyCouple which rate will be returned
     * @return currencyCouple's instant rate if available
     */
    Optional<BigDecimal> getRate(CurrencyCouple currencyCouple);
}
