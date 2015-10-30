package com.meltwater.puppy.http;

public class DryRunRabbitRestClient extends RabbitRestClient {
    public DryRunRabbitRestClient(String brokerAddress, String brokerUsername, String brokerPassword) {
        super(brokerAddress, brokerUsername, brokerPassword);
    }

    @Override
    public void createVirtualHost(String virtualHost, boolean tracing) throws RestClientException {
    }
}
