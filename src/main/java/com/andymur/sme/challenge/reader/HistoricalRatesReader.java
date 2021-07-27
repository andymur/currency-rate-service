package com.andymur.sme.challenge.reader;

import com.andymur.sme.challenge.model.CurrencyCouple;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface HistoricalRatesReader {
    /**
     *
     * @param fromDate beginning of the interval
     * @param toDate end of the interval
     * @param currencyCouple rate we interested in
     * @return
     */
    List<BigDecimal> getRates(LocalDate fromDate, LocalDate toDate,
                              CurrencyCouple currencyCouple);
}
