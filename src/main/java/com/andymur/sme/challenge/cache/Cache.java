package com.andymur.sme.challenge.cache;

import com.andymur.sme.challenge.cache.model.RateHistoryKey;
import com.andymur.sme.challenge.model.CurrencyCouple;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Naïve cache implementation.
 * Must be replaced with something (e.g. Redis)
 * TODO: replace me
 */
public class Cache {
    public static final Map<CurrencyCouple, BigDecimal> INSTANT_RATE_INSTANCE = new HashMap<>();
    public static final Map<RateHistoryKey, List<BigDecimal>> HISTORICAL_RATE_INSTANCE = new HashMap<>();
}
