package com.meltwater.puppy.external;

import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import com.meltwater.puppy.config.reader.RabbitConfigReader;
import com.meltwater.puppy.RabbitPuppy;
import com.meltwater.puppy.http.RestRequestBuilder;
import org.json.JSONObject;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static com.meltwater.puppy.http.RestUtils.escape;

// TODO Should use Main method to be truly E2E

@RunWith(JunitSuiteRunner.class)
public class RabbitPuppyEndToEndSpec {
    {
        final Properties properties = new Properties() {{
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

        File configFile = new File(ClassLoader.getSystemResource("rabbitconfig.yaml").getFile());

        describe("a rabbit-puppy with configuration and external rabbit", it -> {

            RabbitPuppy puppy = new RabbitPuppy(brokerAddress, brokerUser, brokerPass);

            it.isSetupWith(() -> puppy.apply(new RabbitConfigReader().read(configFile)));

            it.isConcludedWith(() -> {
                requestBuilder.delete("api/vhosts/input").asString();
                requestBuilder.delete("api/vhosts/output").asString();
            });

            it.uses("input")
                    .and("output")
                    .toShow("creates vhost '%s'", (expect, vhost) -> {
                        JSONObject json = requestBuilder.get("api/vhosts/{vhost}")
                                .routeParam("vhost", escape(vhost))
                                .asJson().getBody().getObject();

                        expect
                                .that(json.get("name"))
                                .is(vhost);
                    });
        });

    }
}
