package org.westfield.media;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;


public class MediaInfoParser
{
    private static final Logger logger = LoggerFactory.getLogger(MediaInfoParser.class);

    private final File file;

    public static MediaInfoParser get(Path item) {
        Preconditions.checkNotNull(item);
        File file = item.toFile();
        Preconditions.checkArgument(file.exists() && !file.isDirectory());
        MediaInfoParser mi = new MediaInfoParser(file);
        if (mi.parse())
            return mi;
        return null;
    }

    private MediaInfoParser(File theFile)
    {
        this.file = theFile;
    }

    private boolean parse()
    {
        try {
            logger.info("Parsing : {}", file.getName());
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document xmlDocument = documentBuilder.parse(file);
            logger.info("Parsed : {}", file.getName());
            if (logger.isDebugEnabled())
                logger.debug("{}", xmlDocument);
            return true;

        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error("Caught Exception: {}", e.getMessage(), e);
        }
        return false;
    }
}
