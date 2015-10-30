package com.meltwater.puppy;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.meltwater.puppy.config.RabbitConfig;
import com.meltwater.puppy.config.reader.RabbitConfigReader;
import com.meltwater.puppy.config.reader.RabbitConfigReaderException;
import com.meltwater.puppy.http.DryRunRabbitRestClient;
import com.meltwater.puppy.http.RabbitRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final RabbitConfigReader rabbitConfigReader = new RabbitConfigReader();

    private static class Arguments {
        @Parameter(names = { "-h", "--help"}, description = "Print help and exit", help = true)
        private boolean help;

        @Parameter(names = { "-c", "--config" }, description = "YAML config file path", required = true)
        private String configPath;

        @Parameter(names = { "-b", "--broker" }, description = "HTTP URL to broker", required = true)
        private String broker;

        @Parameter(names = { "-u", "--user" }, description = "Username", required = true)
        private String user;

        @Parameter(names = { "-p", "--pass" }, description = "Password", required = true)
        private String pass;

        @Parameter(names = { "-w", "--wait" }, description = "Wait until broker connection succeeds")
        private boolean wait;

        @Parameter(names = { "-d", "--dryrun" }, description = "Perform a dry run")
        private boolean dryRun;
    }

    public static void main(String[] argv) throws IOException {
        Arguments arguments = parseArguments("rabbit-puppy", argv);
        log.info("Reading configuration from " + arguments.configPath);
        try {
            RabbitConfig rabbitConfig = rabbitConfigReader.read(new File(arguments.configPath));
            RabbitRestClient rabbitRestClient = createClient(arguments);
            new RabbitPuppy(rabbitRestClient)
                    .apply(rabbitConfig);
        } catch (RabbitConfigReaderException e) {
            log.error("Failed to read configuration, exiting");
            System.exit(1);
        } catch (RabbitPuppyException e) {
            log.error(String.format("Encountered %d errors, exiting", e.getErrors().size()));
            System.exit(1);
        }
    }

    private static RabbitRestClient createClient(Arguments arguments) {
        if (arguments.dryRun) {
            log.info("Performing a dry run - no resources will be created on the rabbit broker");
            return new DryRunRabbitRestClient(arguments.broker, arguments.user, arguments.pass);
        } else {
            return new RabbitRestClient(arguments.broker, arguments.user, arguments.pass);
        }
    }

    private static Arguments parseArguments(String programName, String[] argv) {
        Arguments arguments = new Arguments();
        JCommander jc = new JCommander();
        jc.addObject(arguments);
        jc.setProgramName(programName);

        try {
            jc.parse(argv);
            if (arguments.help) {
                jc.usage();
                System.exit(1);
            }
        }
        catch (ParameterException e) {
            log.error(e.getMessage());
            jc.usage();
            System.exit(1);
        }
        return arguments;
    }
}
