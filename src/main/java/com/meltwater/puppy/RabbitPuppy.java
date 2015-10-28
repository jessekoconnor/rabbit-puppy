package com.meltwater.puppy;

import com.meltwater.puppy.config.RabbitConfig;

public class RabbitPuppy {

    private String brokerAddress;
    private String username;
    private String password;

    public RabbitPuppy(String brokerAddress, String username, String password) {
        this.brokerAddress = brokerAddress;
        this.username = username;
        this.password = password;
    }

    public RabbitPuppy() {

    }

    public boolean apply(RabbitConfig rabbitConfig) {
        return false;
    }

    private void waitUntilBrokerAvailable() { //A flag

    }
}
