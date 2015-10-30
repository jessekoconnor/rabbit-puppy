package com.meltwater.puppy.rest;

import com.meltwater.puppy.config.VHostData;

public class DryRunRabbitRestClient extends RabbitRestClient {
    public DryRunRabbitRestClient(String brokerAddress, String brokerUsername, String brokerPassword) {
        super(brokerAddress, brokerUsername, brokerPassword);
    }

    @Override
    public void createVirtualHost(String virtualHost, VHostData vHostData) throws RestClientException {
    }
}
