package com.meltwater.puppy.external;

import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import com.meltwater.puppy.config.VHostData;
import com.meltwater.puppy.http.RabbitRestClient;
import com.meltwater.puppy.http.RestRequestBuilder;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static com.meltwater.puppy.http.RestUtils.escape;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@RunWith(JunitSuiteRunner.class)
public class RabbitRestClientSpec {
    {
        final Properties properties = new Properties(){{
            try {
                load(ClassLoader.getSystemResourceAsStream("test.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }};

        final String brokerAddress = properties.getProperty("rabbit.broker.address");
        final String brokerUser = properties.getProperty("rabbit.broker.user");
        final String brokerPass = properties.getProperty("rabbit.broker.pass");

        final RestRequestBuilder requestBuilder = new RestRequestBuilder()
                .withHost(brokerAddress)
                .withAuthentication(brokerUser, brokerPass)
                .withHeader("content-type", "application/json");

        describe("a RabbitMQ REST client with valid auth credentials", it -> {

            final RabbitRestClient rabbitRestClient = new RabbitRestClient(brokerAddress, brokerUser, brokerPass);

            it.isConcludedWith(() -> {
                requestBuilder.delete("api/vhosts/test").asString();
                requestBuilder.delete("api/vhosts/test2").asString();
                requestBuilder.delete("api/vhosts/test%2Ftest").asString();
            });

            it
                    .uses("test", false)
                    .and("test2", true)
                    .and("test/test", false)
                    .toShow("creates vhost: %s", (expect, vhost, tracing) -> {
                        rabbitRestClient.createVirtualHost(vhost, tracing);

                        expect.that(requestBuilder.get("api/vhosts/{vhost}")
                                .routeParam("vhost", escape(vhost))
                                .asJson().getBody().getObject()
                                .get("tracing"))
                                .is(tracing);
                    });

            it.should("gets all vhosts", expect -> {
                Map<String, VHostData> virtualHosts = rabbitRestClient.getVirtualHosts();
                expect
                        .that(virtualHosts.keySet())
                        .hasSize(greaterThanOrEqualTo(1))
                        .hasItem("/");

                expect
                        .that(virtualHosts.get("/"))
                        .isNotNull()
                        .instanceOf(VHostData.class);
            });

        });
    }
}