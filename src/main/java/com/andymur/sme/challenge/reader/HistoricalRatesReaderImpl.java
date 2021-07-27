package com.andymur.sme.challenge.reader;

import com.andymur.sme.challenge.cache.CacheRequester;
import com.andymur.sme.challenge.cache.model.RateHistoryKey;
import com.andymur.sme.challenge.model.CurrencyCouple;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class HistoricalRatesReaderImpl implements HistoricalRatesReader {

    private final CacheRequester<RateHistoryKey, List<BigDecimal>> dataSource;

    public HistoricalRatesReaderImpl(CacheRequester<RateHistoryKey, List<BigDecimal>> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<BigDecimal> getRates(final LocalDate fromDate, final LocalDate toDate, final CurrencyCouple currencyCouple) {
        return dataSource.requestByKey(RateHistoryKey.of(fromDate, toDate, currencyCouple));
    }
}
