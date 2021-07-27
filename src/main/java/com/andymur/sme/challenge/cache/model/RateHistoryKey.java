package com.andymur.sme.challenge.cache.model;

import com.andymur.sme.challenge.model.CurrencyCouple;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

public class RateHistoryKey {
    // time stamp in ms
    private final long fromTimestamp;
    // time stamp in ms
    private final long toTimestamp;
    private final CurrencyCouple currencyCouple;

    public RateHistoryKey(final long fromTimestamp,
                          final long toTimestamp,
                          final CurrencyCouple currencyCouple) {
        this.fromTimestamp = fromTimestamp;
        this.toTimestamp = toTimestamp;
        this.currencyCouple = currencyCouple;
    }

    public static RateHistoryKey of(final LocalDate from,
                                    final LocalDate to,
                                    final CurrencyCouple currencyCouple) {
        return new RateHistoryKey(convertLocalDate(from), convertLocalDate(to),
                currencyCouple);
    }

    private static long convertLocalDate(final LocalDate localDate) {
        return Timestamp.from(localDate.atStartOfDay().atZone(ZoneId.of("UTC"))
                .toInstant()).getTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RateHistoryKey that = (RateHistoryKey) o;
        return fromTimestamp == that.fromTimestamp &&
                toTimestamp == that.toTimestamp &&
                currencyCouple == that.currencyCouple;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromTimestamp, toTimestamp, currencyCouple);
    }
}
