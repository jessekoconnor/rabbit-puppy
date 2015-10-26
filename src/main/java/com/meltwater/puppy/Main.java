package com.meltwater.puppy;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.meltwater.puppy.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static class Arguments {
        @Parameter(names = { "-h", "--help"}, description = "Print help and exit", help = true)
        private boolean help;

        @Parameter(names = { "-c", "--config" }, description = "YAML config file path", required = true)
        private String configPath;
    }

    public static void main(String[] argv) throws FileNotFoundException {
        Arguments arguments = parseArguments("rabbit-puppy", argv);
        log.info("Reading configuration from " + arguments.configPath);
        RabbitConfig rabbitConfig = (RabbitConfig) new Yaml(new Constructor(RabbitConfig.class))
                .load(new BufferedReader(new FileReader(arguments.configPath)));
        log.info("Parsed input YAML: " + rabbitConfig);
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