package com.andymur.sme.challenge.stub;

import com.andymur.sme.challenge.provider.external.ExternalHistoricalRateProvider;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BtcHistoricalRatesStubProvider implements ExternalHistoricalRateProvider {

    @Override
    public List<BigDecimal> getRates(LocalDate fromDate, LocalDate toDate) {
        // no bitcoins before that date
        if (toDate.isBefore(LocalDate.of(2010, 1, 1))) {
            return Collections.emptyList();
        }
        // we have only 3 values for any other date interval (for test purposes)
        return Arrays.asList(new BigDecimal("50000"),
                new BigDecimal("55000"),
                new BigDecimal("60000"));
    }
}
