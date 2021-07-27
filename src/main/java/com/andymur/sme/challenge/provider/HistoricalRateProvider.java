package com.andymur.sme.challenge.provider;

import com.andymur.sme.challenge.model.CurrencyCouple;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface HistoricalRateProvider {

    /**
     *
     * Gets historical rates values for particular currency couple
     *
     * @param fromDate beginning of the interval
     * @param toDate end of the interval
     * @param currencyCouple rate we interested in
     * @return list of rates for currency couple, number of rates should be equal to number of days [fromDate; toDate]
     */
    List<BigDecimal> getHistoricalRates(LocalDate fromDate,
                                        LocalDate toDate,
                                        CurrencyCouple currencyCouple);
}
