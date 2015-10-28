package com.meltwater.puppy;

import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import com.meltwater.puppy.config.BindingData;
import com.meltwater.puppy.config.ExchangeData;
import com.meltwater.puppy.config.PermissionsData;
import com.meltwater.puppy.config.QueueData;
import com.meltwater.puppy.config.RabbitConfig;
import com.meltwater.puppy.config.UserData;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.HashMap;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static org.hamcrest.Matchers.hasEntry;

@SuppressWarnings("ConstantConditions")
@RunWith(JunitSuiteRunner.class)
public class RabbitConfigReaderSpec {{

    RabbitConfigReader rabbitConfigReader = new RabbitConfigReader();

    File configFile = new File(RabbitConfigReaderSpec.class.getClassLoader().getResource("rabbitconfig.yaml").getFile());
    File configFileBad = new File(RabbitConfigReaderSpec.class.getClassLoader().getResource("rabbitconfig.bad.yaml").getFile());

    describe("a RabbitConfigReader reading valid config file ", it -> {
        RabbitConfig config = rabbitConfigReader.read(configFile);

        it.should("read vhosts", expect -> {
            expect
                    .that(config.getVhosts())
                    .hasSize(2)
                    .contains("input", "output");
        });


        it.should("read users", expect -> {
            expect
                    .that(config.getUsers().size())
                    .is(4)
                    .and(config.getUsers())
                    .has(hasEntry("dan", new UserData("torrance", true)))
                    .has(hasEntry("ultron", new UserData(null, false)))
                    .has(hasEntry("jack", new UserData("bauer", false)))
                    .has(hasEntry("ali", null));

        });

        it.should("read permissions", expect -> {
            expect
                    .that(config.getPermissions().size())
                    .is(2)
                    .and(config.getPermissions())
                    .has(hasEntry("dan@input", new PermissionsData(".*", ".*", ".*")))
                    .has(hasEntry("dan@output", new PermissionsData(".*", ".*", ".*")));
        });

        it.should("read exchanges", expect -> {
            expect
                    .that(config.getExchanges().size())
                    .is(2)
                    .and(config.getExchanges())
                    .has(hasEntry("exchange.in@input", new ExchangeData("topic", false, true, true, new HashMap<>())
                            .addArgument("hash-header", "abc")))
                    .has(hasEntry("exchange.out@output", new ExchangeData("fanout", true, false, false, new HashMap<>())));
        });

        it.should("read queues", expect -> {
            expect
                    .that(config.getQueues().size())
                    .is(2)
                    .and(config.getQueues())
                    .has(hasEntry("queue-in@input", new QueueData(false, true, new HashMap<>())
                            .addArgument("x-message-ttl", 123)
                            .addArgument("x-dead-letter-exchange", "other")))
                    .has(hasEntry("queue-out@output", new QueueData(true, false, null)));
        });

        it.should("read bindings", expect -> {
            expect
                    .that(config.getBindings().size())
                    .is(2)
                    .and(config.getBindings())
                    .has(hasEntry("exchange.in@queue-in@input", new BindingData("queue", "#")))
                    .has(hasEntry("exchange.out@queue-out@output", null));
        });
    });

    describe("a RabbitConfigReader reading invalid config file should", it -> {

        it.should("fail nicely", expect -> {
            expect.exception(RabbitConfigReaderException.class, () ->
                    rabbitConfigReader.read(configFileBad)
            );
        });
    });

}}
