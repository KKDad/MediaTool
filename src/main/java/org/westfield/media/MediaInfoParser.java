package org.westfield.media;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;


class MediaInfoParser
{
    private static final Logger logger = LoggerFactory.getLogger(MediaInfoParser.class);

    boolean parse(Path fileName)
    {
        try {
            List<String> result = Files.readLines(fileName.toFile(), Charsets.UTF_8);

            logger.info("{}", result);

            return true;
        } catch (IOException ioe) {
            logger.error("{}", ioe.getMessage(), ioe);

        }
        return false;
    }
}
