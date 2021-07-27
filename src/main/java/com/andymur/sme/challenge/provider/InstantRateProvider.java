package com.andymur.sme.challenge.provider;

import com.andymur.sme.challenge.model.CurrencyCouple;

import java.math.BigDecimal;
import java.util.Optional;

public interface InstantRateProvider {
    /**
     * Gets instant rate of currency couple
     * @param currencyCouple rate we interested in
     * @return instant rate value
     */
    Optional<BigDecimal> getRate(CurrencyCouple currencyCouple);
}
