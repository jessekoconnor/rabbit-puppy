package com.meltwater.puppy;


import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import com.meltwater.puppy.config.RabbitConfig;
import com.meltwater.puppy.config.VHostData;
import com.meltwater.puppy.http.RabbitRestClient;
import org.junit.runner.RunWith;

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
            verify(rabbitRestClient).createVirtualHost(VHOST, VHOST_DATA.isTracing());
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
}}