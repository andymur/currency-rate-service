package com.andymur.sme.challenge.updater;

import com.andymur.sme.challenge.model.CurrencyCouple;

import java.math.BigDecimal;

public interface InstantRateUpdater {
    /**
     * Updates instant rate cache value for given currency couple
     * @param currencyCouple to be updated
     * @param newRate value to be updated
     */
    void updateRate(final CurrencyCouple currencyCouple, final BigDecimal newRate);
}
