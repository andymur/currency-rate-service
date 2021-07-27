package com.andymur.sme.challenge.cache;

import com.andymur.sme.challenge.model.CurrencyCouple;

import java.math.BigDecimal;
import java.util.Map;

/**
 * for instant rates request/updates
 */
public class InstantRateCache implements CacheRequester<CurrencyCouple, BigDecimal>, CacheUpdater<CurrencyCouple, BigDecimal> {

    private final Map<CurrencyCouple, BigDecimal> dataSource;

    public InstantRateCache(Map<CurrencyCouple, BigDecimal> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void updateEntry(CurrencyCouple currencyCouple, BigDecimal value) {
        dataSource.put(currencyCouple, value);
    }

    @Override
    public BigDecimal requestByKey(CurrencyCouple currencyCouple) {
        return dataSource.get(currencyCouple);
    }
}
