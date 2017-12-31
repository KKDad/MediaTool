package org.westfield.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class YamlConfigLoader
{
    private static final Logger logger = LoggerFactory.getLogger(YamlConfigLoader.class);

    private YamlConfigLoader() { }

    public static MediaToolConfig get(Path config)
    {
        try {
            Yaml yaml = new Yaml();
            try (InputStream in = Files.newInputStream(config))
            {
                MediaToolConfig mediaConfig = yaml.loadAs(in, MediaToolConfig.class);
                if (logger.isInfoEnabled())
                    logger.info("{}", mediaConfig.toString());
                return mediaConfig;
            }
        } catch (IOException oie) {
            logger.error("{}", oie.getMessage(), oie);
        }
        return null;
    }
}