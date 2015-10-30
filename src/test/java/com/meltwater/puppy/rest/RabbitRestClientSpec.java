package com.meltwater.puppy.rest;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import com.meltwater.puppy.config.ExchangeData;
import com.meltwater.puppy.config.PermissionsData;
import com.meltwater.puppy.config.VHostData;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static com.meltwater.puppy.rest.RabbitRestClient.PATH_PERMISSIONS_CREATE;
import static com.meltwater.puppy.rest.RabbitRestClient.PATH_VHOSTS_CREATE;
import static com.meltwater.puppy.rest.RestUtils.escape;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@RunWith(JunitSuiteRunner.class)
public class RabbitRestClientSpec {
    {
        final Properties properties = new Properties() {{
            try {
                load(ClassLoader.getSystemResourceAsStream("test.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }};

        final Gson gson = new Gson();

        final String brokerAddress = properties.getProperty("rabbit.broker.address");
        final String brokerUser = properties.getProperty("rabbit.broker.user");
        final String brokerPass = properties.getProperty("rabbit.broker.pass");

        final RestRequestBuilder requestBuilder = new RestRequestBuilder()
                .withHost(brokerAddress)
                .withAuthentication(brokerUser, brokerPass)
                .withHeader("content-type", "application/json");

        describe("a RabbitMQ REST client with valid auth credentials", it -> {

            final RabbitRestClient rabbitRestClient = new RabbitRestClient(brokerAddress, brokerUser, brokerPass);

            it.isSetupWith(() -> {
                requestBuilder.put(PATH_VHOSTS_CREATE).routeParam("vhost", escape("test")).asString();
                requestBuilder.put(PATH_PERMISSIONS_CREATE)
                        .routeParam("vhost", escape("test"))
                        .routeParam("user", escape("guest"))
                        .body(gson.toJson(new PermissionsData()))
                        .asString();
            });

            it.isConcludedWith(() -> {
                requestBuilder.delete(PATH_VHOSTS_CREATE).routeParam("vhost", escape("test")).asString();
                requestBuilder.delete(PATH_VHOSTS_CREATE).routeParam("vhost", escape("test1")).asString();
                requestBuilder.delete(PATH_VHOSTS_CREATE).routeParam("vhost", escape("test2")).asString();
                requestBuilder.delete(PATH_VHOSTS_CREATE).routeParam("vhost", escape("test/test")).asString();
            });

            it
                    .uses("test1", new VHostData(false))
                    .and("test2", new VHostData(true))
                    .and("test/test", new VHostData(false))
                    .toShow("creates vhost: %s", (expect, vhost, data) -> {
                        rabbitRestClient.createVirtualHost(vhost, data);

                        expect.that(requestBuilder.get(PATH_VHOSTS_CREATE)
                                .routeParam("vhost", escape(vhost))
                                .asJson().getBody().getObject()
                                .get("tracing"))
                                .is(data.isTracing());
                    });

            it.should("gets existing vhosts", expect -> {
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

            it
                    .uses("ex1", "test", exchangeOfType("fanout"))
                    .and("ex2", "test", exchangeOfType("direct"))
                    .and("ex3", "test", exchangeOfType("headers"))
                    .and("ex4", "test", new ExchangeData("topic", false, true, true, ImmutableMap.of("foo", "bar")))
                    .toShow("creates vhost: %s", (expect, exchange, vhost, data) -> {
                        rabbitRestClient.createExchange(exchange, vhost, data);
                        expect.that(new Gson().fromJson(requestBuilder.get("api/exchanges/{vhost}/{name}")
                                .routeParam("vhost", escape(vhost))
                                .routeParam("name", escape(exchange))
                                .asString().getBody(), ExchangeData.class))
                                .is(data);
                    });

            it.should("gets existing exchanges", expect -> {
                Map<String, ExchangeData> exchanges = rabbitRestClient.getExchanges();
                expect
                        .that(exchanges.keySet())
                        .hasSize(greaterThanOrEqualTo(8))
                        .hasItem("amq.direct@/");

                expect
                        .that(exchanges.get("amq.direct@/"))
                        .isNotNull()
                        .instanceOf(ExchangeData.class);
            });

        });
    }

    private static ExchangeData exchangeOfType(String type) {
        ExchangeData exchangeData = new ExchangeData();
        exchangeData.setType(type);
        return exchangeData;
    }
}