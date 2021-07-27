package com.andymur.sme.challenge.provider.external;

import com.andymur.sme.challenge.model.CurrencyCouple;

import java.math.BigDecimal;
import java.util.Optional;

public interface ExternalInstantRateProvider {
    /**
     * Provides the system with instant rate value of some currency couple
     * @return instant rate value
     */
    Optional<BigDecimal> getRate();
}
