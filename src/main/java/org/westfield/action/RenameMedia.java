package org.westfield.action;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

import java.io.File;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class RenameMedia implements IAction
{
    private static final Logger logger = LoggerFactory.getLogger(RenameMedia.class);

    private String formatString;
    private List<String> tokens;
    private String destination;
    private boolean enabled;

    private static final char START_TOKEN = '{';
    private static final char END_TOKEN = '}';
    private static final String SEPARATORS = "/\\.";

    @Override
    public boolean configure(MediaToolConfig config)
    {
        try {
            this.formatString = config.getRenameMedia().get("format");
            this.destination = config.getDestination();
            this.tokens = parseTokens(this.formatString);
            this.enabled = Boolean.parseBoolean(config.getRenameMedia().get("enabled"));
            return true;
        } catch (ParseException pe) {
            logger.error(pe.getMessage());
        }
        return false;
    }

    private List<String> parseTokens(String formatString) throws ParseException
    {
        List<String> collectedTokens = new ArrayList<>();
        int i = 0;
        StringBuilder token = new StringBuilder();
        boolean inToken = false;
        do {
            char ch = formatString.charAt(i);
            if (ch == START_TOKEN) {
                if (inToken)
                    throw new ParseException(formatString, i);
                if (token.length() > 0) {
                    collectedTokens.add(token.toString());
                    token = new StringBuilder();
                }
                token.append(ch);
               inToken = true;
            }
            else if (ch == END_TOKEN) {
                if (!inToken)
                    throw new ParseException(formatString, i);
                token.append(ch);
                collectedTokens.add(token.toString());
                token = new StringBuilder();
                inToken = false;
            }
            else if (SEPARATORS.indexOf(ch)> -1) {
                token.append(ch);
                collectedTokens.add(token.toString());
                token = new StringBuilder();
            } else {
                token.append(ch);
            }
            i++;
        } while(i < formatString.length());
        return collectedTokens;
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
            destFileName.append(getMediaToken(details, token));
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

    private String getMediaToken(IMediaDetails details, String token) throws UnknownTokenException
    {
        switch (token) {
            case "/":
            case "-":
            case ".":
                return token;

            case "{Show}":
                return details.getShow();

            case "{Season}":
                return String.format("%02d", details.getSeason());

            case "{Episode}":
                return String.format("%02d", details.getEpisodeNumber());

            case "{Title}":
                return details.getEpisodeTitle();

            case "{Format}":
                return FilenameUtils.getExtension(details.getMediaFile().getName());

            default:
                if (token.charAt(0) == START_TOKEN)
                    throw new UnknownTokenException(token);
                return token;
        }
    }
}