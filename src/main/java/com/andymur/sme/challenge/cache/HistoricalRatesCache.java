package com.andymur.sme.challenge.cache;

import com.andymur.sme.challenge.cache.model.RateHistoryKey;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * for historical rates requests/update
 */
public class HistoricalRatesCache implements CacheRequester<RateHistoryKey, List<BigDecimal>>,
        CacheUpdater<RateHistoryKey, List<BigDecimal>> {

    final Map<RateHistoryKey, List<BigDecimal>> dataSource;

    public HistoricalRatesCache(final Map<RateHistoryKey, List<BigDecimal>> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<BigDecimal> requestByKey(final RateHistoryKey rateHistoryKey) {
        return dataSource.getOrDefault(rateHistoryKey, Collections.emptyList());
    }

    @Override
    public void updateEntry(RateHistoryKey rateHistoryKey, List<BigDecimal> rates) {
        dataSource.put(rateHistoryKey, rates);
    }
}
