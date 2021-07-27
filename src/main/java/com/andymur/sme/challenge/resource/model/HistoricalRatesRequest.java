package com.andymur.sme.challenge.resource.model;

import com.andymur.sme.challenge.model.CurrencyCouple;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * We don't really use that class how we could
 * (decided not to break strict rule that GET request must be w/o body)
 */
public class HistoricalRatesRequest {

    private CurrencyCouple currencyCouple;
    private LocalDate fromDate;
    private LocalDate toDate;

    public HistoricalRatesRequest() {
    }

    public HistoricalRatesRequest(final LocalDate fromDate,
                                  final LocalDate toDate,
                                  final CurrencyCouple currencyCouple) {
        this.currencyCouple = currencyCouple;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @JsonProperty
    public CurrencyCouple getCurrencyCouple() {
        return currencyCouple;
    }

    public void setCurrencyCouple(CurrencyCouple currencyCouple) {
        this.currencyCouple = currencyCouple;
    }

    @JsonProperty
    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    @JsonProperty
    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public static HistoricalRatesRequest of(final LocalDate fromDate,
                                            final LocalDate toDate,
                                            final CurrencyCouple currencyCouple) {
        return new HistoricalRatesRequest(fromDate, toDate, currencyCouple);
    }

    @Override
    public String toString() {
        return "HistoricalRatesRequest{" +
                "currencyCouple=" + currencyCouple +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                '}';
    }
}
