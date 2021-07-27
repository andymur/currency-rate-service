package com.andymur.sme.challenge;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.jobs.JobConfiguration;

import java.util.Map;

public class CurrencyRateServiceConfiguration extends Configuration implements JobConfiguration {

    @JsonProperty("jobs")
    private Map<String, String> tasks;

    @Override
    public Map<String, String> getJobs() {
        return tasks;
    }
}
