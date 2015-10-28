package com.meltwater.puppy;

import com.google.common.base.Joiner;
import com.meltwater.puppy.config.RabbitConfig;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class RabbitConfigReader {

    public RabbitConfig read(String yaml) {
        return (RabbitConfig) new Yaml(new Constructor(RabbitConfig.class)).load(yaml);
    }

    public RabbitConfig read(File yamlFile) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(yamlFile.getAbsolutePath()));
        return read(Joiner.on('\n').join(lines));
    }
}
