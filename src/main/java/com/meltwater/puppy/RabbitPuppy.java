package com.meltwater.puppy;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class RabbitPuppy {

    private static final Logger log = LoggerFactory.getLogger(RabbitPuppy.class);

    private static class Arguments {
        @Parameter(names = { "-h", "--help"}, description = "Print help and exit", help = true)
        private boolean help;

        @Parameter(names = { "-c", "--config" }, description = "YAML config file path", required = true)
        private String configPath;
    }

    public static void main(String[] argv) throws FileNotFoundException {
        Arguments arguments = parseArguments("rabbit-puppy", argv);
        log.info("Reading configuration from " + arguments.configPath);
        Object config = new Yaml().load(new BufferedReader(new FileReader(arguments.configPath)));
        log.info("Parsed input YAML: " + config);
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
