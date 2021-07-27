package com.andymur.sme.challenge.updater;

import com.andymur.sme.challenge.cache.CacheUpdater;
import com.andymur.sme.challenge.cache.model.RateHistoryKey;
import com.andymur.sme.challenge.model.CurrencyCouple;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class HistoricalRatesUpdaterImpl implements HistoricalRatesUpdater {

    private final CacheUpdater<RateHistoryKey, List<BigDecimal>> dataSource;

    public HistoricalRatesUpdaterImpl(CacheUpdater<RateHistoryKey, List<BigDecimal>> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void updateHistoricalRates(final LocalDate fromDate, final LocalDate toDate, final CurrencyCouple currencyCouple,
                                      final List<BigDecimal> newRates) {
        dataSource.updateEntry(RateHistoryKey.of(fromDate, toDate, currencyCouple), newRates);
    }
}
