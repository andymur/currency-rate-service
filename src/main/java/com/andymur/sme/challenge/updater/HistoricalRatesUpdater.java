package com.andymur.sme.challenge.updater;

import com.andymur.sme.challenge.model.CurrencyCouple;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface HistoricalRatesUpdater {

    /**
     * Updates historical rates in the cache for given cache key (date interval + currency couple)
     *
     * @param fromDate beginning of the interval
     * @param toDate end of the interval
     * @param currencyCouple rate we interested in
     * @param newRates update for interval & currency couple
     */
    void updateHistoricalRates(LocalDate fromDate, LocalDate toDate, CurrencyCouple currencyCouple, List<BigDecimal> newRates);
}
