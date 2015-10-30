package com.meltwater.puppy;


import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import com.meltwater.puppy.config.ExchangeData;
import com.meltwater.puppy.config.RabbitConfig;
import com.meltwater.puppy.config.VHostData;
import com.meltwater.puppy.rest.RabbitRestClient;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static com.google.common.collect.ImmutableMap.of;
import static com.insightfullogic.lambdabehave.Suite.describe;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JunitSuiteRunner.class)
public class RabbitPuppySpec {{

    RabbitRestClient rabbitRestClient = mock(RabbitRestClient.class);

    describe("a rabbit-puppy configured to create a vhost", it -> {

        final String VHOST = "vhost";
        final VHostData VHOST_DATA = new VHostData(true);

        RabbitPuppy puppy = new RabbitPuppy(rabbitRestClient);
        RabbitConfig rabbitConfig = new RabbitConfig().addVhost(VHOST, VHOST_DATA);

        it.should("creates vhost if it doesn't exist", expect -> {
            when(rabbitRestClient.getVirtualHosts())
                    .thenReturn(of());

            puppy.apply(rabbitConfig);

            verify(rabbitRestClient).getVirtualHosts();
            verify(rabbitRestClient).createVirtualHost(VHOST, VHOST_DATA);
            verifyNoMoreInteractions(rabbitRestClient);
        });

        it.should("doesn't create vhost if it exists with same config", expect -> {
            when(rabbitRestClient.getVirtualHosts())
                    .thenReturn(of(VHOST, VHOST_DATA));

            puppy.apply(rabbitConfig);

            verify(rabbitRestClient).getVirtualHosts();
            verifyNoMoreInteractions(rabbitRestClient);

        });

        it.should("throw exception if vhosts exists with different config", expect -> {
            when(rabbitRestClient.getVirtualHosts())
                    .thenReturn(of(VHOST, new VHostData(!VHOST_DATA.isTracing())));

            expect.exception(RabbitPuppyException.class, () -> puppy.apply(rabbitConfig));

            verify(rabbitRestClient).getVirtualHosts();
            verifyNoMoreInteractions(rabbitRestClient);
        });
    });

    describe("a rabbit-puppy configured to create an exchange", it -> {

        final String EXCHANGE_NAME = "foo";
        final String VHOST = "vhost";
        final String EXCHANGE = EXCHANGE_NAME + "@" + VHOST;
        final ExchangeData EXCHANGE_DATA = new ExchangeData("topic", true, false, false, new HashMap<>());

        RabbitPuppy puppy = new RabbitPuppy(rabbitRestClient);
        RabbitConfig rabbitConfig = new RabbitConfig().addExchange(EXCHANGE, EXCHANGE_DATA);

        it.should("create exchange if it doesn't exist", expect -> {
            when(rabbitRestClient.getExchanges())
                    .thenReturn(of());

            puppy.apply(rabbitConfig);

            verify(rabbitRestClient).getExchanges();
            verify(rabbitRestClient).createExchange(EXCHANGE_NAME, VHOST, EXCHANGE_DATA);
            verifyNoMoreInteractions(rabbitRestClient);
        });

        it.should("doesn't create exchange if it exists with same config", expect -> {
            when(rabbitRestClient.getExchanges())
                    .thenReturn(of(EXCHANGE, EXCHANGE_DATA));

            puppy.apply(rabbitConfig);

            verify(rabbitRestClient).getExchanges();
            verifyNoMoreInteractions(rabbitRestClient);

        });

        it.should("throw exception if vhosts exists with different config", expect -> {
            when(rabbitRestClient.getExchanges())
                    .thenReturn(of(EXCHANGE, new ExchangeData().addArgument("foo", "bar")));

            expect.exception(RabbitPuppyException.class, () -> puppy.apply(rabbitConfig));

            verify(rabbitRestClient).getExchanges();
            verifyNoMoreInteractions(rabbitRestClient);
        });
    });
}}