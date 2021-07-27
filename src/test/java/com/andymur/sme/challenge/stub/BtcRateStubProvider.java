package com.andymur.sme.challenge.stub;

import com.andymur.sme.challenge.provider.external.ExternalInstantRateProvider;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class BtcRateStubProvider implements ExternalInstantRateProvider {

    private boolean isFirstRequest = true;

    private List<BigDecimal> rateValues = Arrays.asList(new BigDecimal("50000"),
            new BigDecimal("55000"),
            new BigDecimal("60000"),
            new BigDecimal("65000"),
            new BigDecimal("70000"),
            new BigDecimal("75000"),
            new BigDecimal("80000"),
            new BigDecimal("75000"),
            new BigDecimal("70000"),
            new BigDecimal("65000"));

    final Iterator<BigDecimal> valueIterator = rateValues.iterator();

    public Optional<BigDecimal> getRate() {
        if (isFirstRequest) {
            isFirstRequest = false;
            return Optional.empty();
        }
        if (valueIterator.hasNext()) {
            return Optional.ofNullable(valueIterator.next());
        }
        return Optional.empty();
    }
}
