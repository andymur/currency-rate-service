package com.andymur.sme.challenge.model;

public enum CurrencyCouple {

    XBTUSD("XBTUSD");

    private final String baseCurrency;
    private final String quoteCurrency;

    CurrencyCouple(final String baseCurrency,
                   final String quoteCurrency) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
    }

    CurrencyCouple(final String currencyPair) {
        this(currencyPair.substring(0, 3), currencyPair.substring(3, 6));
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }
}
