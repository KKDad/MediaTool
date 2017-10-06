package org.westfield.media;

import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.nio.file.Files;
import java.util.stream.Stream;

public class MediaInfoParser
{
    private static final Logger logger = LoggerFactory.getLogger(MediaInfoParser.class);

    public boolean parse(String file)
    {
        Stream<String> lines = Files.readAllLines(new File("file"), Charsets.UTF_8);
        logger.info("{}", lines);
        return true;
    }
}
