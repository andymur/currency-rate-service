package com.andymur.sme.challenge.provider.external;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExternalHistoricalRateProvider {
    /**
     * Provides the system with historical rates values of some currency couple
     *
     * @param fromDate beginning of the interval
     * @param toDate end of the interval
     * @return list of rates, number of rates should be equal to number of days [fromDate; toDate]
     */
    List<BigDecimal> getRates(LocalDate fromDate, LocalDate toDate);
}
