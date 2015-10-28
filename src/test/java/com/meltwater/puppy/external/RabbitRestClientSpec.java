package com.meltwater.puppy.external;

import com.google.common.net.UrlEscapers;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import com.mashape.unirest.http.Unirest;
import com.meltwater.puppy.http.RabbitRestClient;
import com.meltwater.puppy.http.RestRequestBuilder;
import org.junit.runner.RunWith;

import static com.insightfullogic.lambdabehave.Suite.describe;

@RunWith(JunitSuiteRunner.class)
public class RabbitRestClientSpec {
    {

        // TODO read from properties
        final String brokerAddress = "http://localhost:15672/";
        final String brokerUser = "guest";
        final String brokerPass = "guest";

        final RestRequestBuilder requestBuilder = new RestRequestBuilder()
                .withHost(brokerAddress)
                .withAuthentication(brokerUser, brokerPass)
                .withHeader("content-type", "application/json");

        describe("a RabbitMQ REST client with valid auth credentials", it -> {

            final RabbitRestClient rabbitRestClient = new RabbitRestClient(brokerAddress, brokerUser, brokerPass);

            it.isConcludedWith(() -> {
                Unirest.delete(brokerAddress + "api/vhosts/test").basicAuth("guest", "guest").asString();
                Unirest.delete(brokerAddress + "api/vhosts/test2").basicAuth("guest", "guest").asString();
                Unirest.delete(brokerAddress + "api/vhosts/test%2ftest").basicAuth("guest", "guest").asString();
            });

            it
                    .uses("test", false)
                    .and("test2", true)
                    .and("test/test", false)
                    .toShow("vhost '%s' is created", (expect, vhost, tracing) -> {
                        rabbitRestClient.createVirtualHost(vhost, tracing);

                        expect.that(requestBuilder.get("api/vhosts/{vhost}")
                                .routeParam("vhost", escape(vhost))
                                .asJson().getBody().getObject()
                                .get("tracing"))
                                .is(tracing);
                    });

        });
    }

    private static String escape(String route) {
        return UrlEscapers.urlPathSegmentEscaper().escape(route);
    }
}