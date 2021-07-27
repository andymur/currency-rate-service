package com.andymur.sme.challenge.resource.model;

import com.andymur.sme.challenge.model.CurrencyCouple;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class InstantCurrencyRate {

    private CurrencyCouple currencyCouple;
    private BigDecimal value;

    public InstantCurrencyRate() {
    }

    @JsonProperty
    public CurrencyCouple getCurrencyCouple() {
        return currencyCouple;
    }

    public void setCurrencyCouple(CurrencyCouple currencyCouple) {
        this.currencyCouple = currencyCouple;
    }

    @JsonProperty
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "InstantCurrencyRate{" +
                "currencyCouple=" + currencyCouple +
                ", value=" + value +
                '}';
    }
}
