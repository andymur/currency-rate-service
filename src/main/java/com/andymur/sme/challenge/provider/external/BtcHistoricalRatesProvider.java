package com.andymur.sme.challenge.provider.external;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * External component which is stub for now
 * Must be replaced with a real one
 */
public class BtcHistoricalRatesProvider implements ExternalHistoricalRateProvider {


    @Override
    public List<BigDecimal> getRates(final LocalDate fromDate,
                                     final LocalDate toDate) {
        final List<BigDecimal> result = new ArrayList<>();
        final Random randomSource = new Random(System.currentTimeMillis());

        LocalDate currentDate = fromDate;
        while (toDate.isAfter(currentDate)) {
            currentDate = currentDate.plusDays(1);
            result.add(BigDecimal.valueOf(
                    50000 + randomSource.nextInt(20000)));
        }
        return result;
    }
}
