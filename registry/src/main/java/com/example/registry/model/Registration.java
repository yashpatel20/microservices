package com.example.registry.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

//maps the methodNames to the serviceName
public class Registration {
    private final String serviceName;
    private final String[] methodNames;

    @JsonCreator
    public Registration(@JsonProperty("serviceName") final String serviceName,
            @JsonProperty("methodNames") final String[] methodNames) {
        this.serviceName = serviceName;
        this.methodNames = methodNames;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String[] getMethodNames() {
        return methodNames;
    }

}