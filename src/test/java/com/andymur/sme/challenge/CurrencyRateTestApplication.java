package com.andymur.sme.challenge;

import com.andymur.sme.challenge.model.CurrencyCouple;
import com.andymur.sme.challenge.provider.RateProviderFacade;
import com.andymur.sme.challenge.stub.BtcHistoricalRatesStubProvider;
import com.andymur.sme.challenge.stub.BtcRateStubProvider;

public class CurrencyRateTestApplication extends CurrencyRateApplication {

    @Override
    protected RateProviderFacade createRateProviderFacade() {
        RateProviderFacade providerFacade = new RateProviderFacade();
        providerFacade.addInstantRateProvider(CurrencyCouple.XBTUSD, new BtcRateStubProvider());
        providerFacade.addHistoricalRateProvider(CurrencyCouple.XBTUSD, new BtcHistoricalRatesStubProvider());
        return providerFacade;
    }
}
