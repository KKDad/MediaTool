package org.westfield;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.configuration.MediaToolConfig;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class MediaTool {

    private final MediaToolConfig config;
    private static final Logger logger = LoggerFactory.getLogger(MediaTool.class);

    public MediaTool(MediaToolConfig config)
    {
        this.config = config;
    }

    void run() {
        try {
            File source = Paths.get(config.getSource()).toFile();
            File destination = Paths.get(config.getDestination()).toFile();
            if (source.equals(destination)) {
                logger.error("Source and destination cannot be the same.");
                return;
            }
            if (!source.isDirectory()) {
                logger.error("Source directory '{}' does not exist.", config.getSource());
                return;
            }
            if (!destination.isDirectory()) {
                logger.error("Destination directory '{}' does not exist.", config.getDestination());
                return;
            }
            Files.find(source.toPath(),
                    Integer.MAX_VALUE,
                    (filePath, fileAttr) -> fileAttr.isRegularFile())
                    .forEach(this::process);
        } catch (Exception ex)
        {
            logger.error("{}", ex.getMessage(), ex);
        }
    }

    private void process(Path item)
    {
        logger.info("{}", item);

    }
}
