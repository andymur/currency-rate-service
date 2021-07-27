package com.andymur.sme.challenge.provider.external;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

/**
 * External component which is stub for now
 * Must be replaced with a real one
 */
public class BtcRateProvider implements ExternalInstantRateProvider {

    @Override
    public Optional<BigDecimal> getRate() {
        final Random randomSource = new Random(System.currentTimeMillis());

        return Optional.ofNullable(BigDecimal.valueOf(
                50000 + randomSource.nextInt(20000))
        );
    }
}
