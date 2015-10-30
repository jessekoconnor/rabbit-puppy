package com.meltwater.puppy.config.reader;

import com.google.common.base.Joiner;
import com.meltwater.puppy.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.ConstructorException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class RabbitConfigReader {

    private static final Logger log = LoggerFactory.getLogger(RabbitConfigReader.class);

    public RabbitConfig read(String yaml) throws RabbitConfigReaderException {
        try {
            return (RabbitConfig) new Yaml(new Constructor(RabbitConfig.class)).load(yaml);
        } catch (ConstructorException e) {
            log.error("Failed reading configuration: " + e.getMessage());
            throw new RabbitConfigReaderException("Failed reading configuration", e);
        }
    }

    public RabbitConfig read(File yamlFile) throws RabbitConfigReaderException {
        try {
            List<String> lines = Files.readAllLines(Paths.get(yamlFile.getAbsolutePath()));
            return read(Joiner.on('\n').join(lines));
        } catch (IOException e) {
            log.error("Failed reading from file " + yamlFile.getPath(), e.getMessage());
            throw new RabbitConfigReaderException("Failed reading from file " + yamlFile.getPath(), e);
        }
    }
}
