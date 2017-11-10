package org.westfield.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.parser.TokenParser;
import org.westfield.parser.UnknownTokenException;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

import java.io.File;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;

public class RenameMedia implements IAction
{
    private static final Logger logger = LoggerFactory.getLogger(RenameMedia.class);

    private String formatString;
    private List<String> tokens;
    private String destination;
    private boolean enabled;

    @Override
    public boolean configure(MediaToolConfig config)
    {
        try {
            this.formatString = config.getRenameMedia().get("format");
            this.destination = config.getDestination();
            this.tokens = TokenParser.parseTokens(this.formatString);
            this.enabled = Boolean.parseBoolean(config.getRenameMedia().get("enabled"));
            return this.enabled;
        } catch (ParseException pe) {
            logger.error(pe.getMessage());
        }
        return false;
    }


    @Override
    public IMediaDetails process(IMediaDetails details) {
        try {
            File originalFile = details.getMediaFile();
            File destinationFile = generateDestinationFilename(details);
            if (logger.isDebugEnabled()) {
                logger.debug("----------------------------------");
                logger.debug("Old Name: {}", originalFile);
                logger.debug("New Name: {}", destinationFile);
            }
            if (destinationFile.isDirectory()) {
                logger.error("Generated destination file exists & it is a directory!");
                System.exit(1);
            }
            if (!ensureDirectoryExists(destinationFile)) {
                logger.error("Cannot ensure the destination parent directory exists.");
                return details;
            }
            if (this.enabled) {
                if (originalFile.renameTo(destinationFile)) {
                    logger.debug("Rename successful");
                    details.setMediaFile(destinationFile);
                } else {
                    logger.debug("Rename failed");
                }
            } else 
            {
                logger.debug("Rename disabled by configuration.");
            }
            return details;
        }
        catch (UnknownTokenException u)
        {
            logger.error("Unknown Token {} in format string: {}", u.getToken(), this.formatString);
        }
        return null;
    }

    public File generateDestinationFilename(IMediaDetails details) throws UnknownTokenException
    {
        StringBuilder destFileName = new StringBuilder();
        for (String token : this.tokens) {
            destFileName.append(TokenParser.getMediaToken(details, token));
        }
        return Paths.get(this.destination, destFileName.toString()).toFile();
    }

    boolean ensureDirectoryExists(File fileName)
    {
        File parent = fileName.getParentFile();
        if (parent.isFile()) {
            logger.error("Parent exists and is a file?");
            return false;
        }
        if (!parent.exists() && parent.mkdirs()) {
            logger.info("Parent created: {}", parent);
        }
        return parent.exists() && parent.isDirectory();
    }

}