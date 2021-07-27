package com.andymur.sme.challenge.updater;

import com.andymur.sme.challenge.cache.CacheUpdater;
import com.andymur.sme.challenge.model.CurrencyCouple;

import java.math.BigDecimal;

public class InstantRateUpdaterImpl implements InstantRateUpdater {

    private final CacheUpdater<CurrencyCouple, BigDecimal> dataSource;

    public InstantRateUpdaterImpl(CacheUpdater<CurrencyCouple, BigDecimal> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void updateRate(CurrencyCouple currencyCouple, BigDecimal newRate) {
        dataSource.updateEntry(currencyCouple, newRate);
    }
}
