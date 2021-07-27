package com.andymur.sme.challenge.resource.model;

import com.andymur.sme.challenge.model.CurrencyCouple;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.validation.ValidationMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class HistoricalRatesData {

    private CurrencyCouple currencyCouple;
    private Map<LocalDate, BigDecimal> values;

    public HistoricalRatesData() {
        values = new TreeMap<>();
    }

    public HistoricalRatesData(final CurrencyCouple currencyCouple, final Map<LocalDate, BigDecimal> values) {
        this.currencyCouple = currencyCouple;
        this.values = new TreeMap<>(values);
    }

    @JsonProperty
    public CurrencyCouple getCurrencyCouple() {
        return currencyCouple;
    }

    public void setCurrencyCouple(CurrencyCouple currencyCouple) {
        this.currencyCouple = currencyCouple;
    }

    @JsonProperty
    public Map<LocalDate, BigDecimal> getValues() {
        return values;
    }

    // for the sake of deserialization (a bit hacky)
    @JsonProperty
    public void setValues(Map<String, BigDecimal> values) {
        Map<LocalDate, BigDecimal> result = new TreeMap<>();
        for (Map.Entry<String, BigDecimal> entry: values.entrySet()) {
            result.put(LocalDate.parse(entry.getKey()), entry.getValue());
        }
        this.values = result;
    }

    public void addValue(LocalDate date, BigDecimal value) {
        this.values.put(date, value);
    }

    @Override
    public String toString() {
        return "HistoricalRatesData{" +
                "currencyCouple=" + currencyCouple +
                ", values=" + values +
                '}';
    }
}
