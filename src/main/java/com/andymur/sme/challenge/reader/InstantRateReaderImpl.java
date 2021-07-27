package com.andymur.sme.challenge.reader;

import com.andymur.sme.challenge.cache.CacheRequester;
import com.andymur.sme.challenge.model.CurrencyCouple;

import java.math.BigDecimal;
import java.util.Optional;

public class InstantRateReaderImpl implements InstantRateReader {

    private final CacheRequester<CurrencyCouple, BigDecimal> dataSource;

    public InstantRateReaderImpl(final CacheRequester<CurrencyCouple, BigDecimal> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<BigDecimal> getRate(CurrencyCouple currencyCouple) {
        return Optional.ofNullable(dataSource.requestByKey(currencyCouple));
    }
}
