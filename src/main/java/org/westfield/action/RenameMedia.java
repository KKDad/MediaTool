package org.westfield.action;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.parser.TokenParser;
import org.westfield.parser.UnknownTokenException;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;

public class RenameMedia extends Action
{
    private static final Logger logger = LoggerFactory.getLogger(RenameMedia.class);

    private String  regular;
    private String  regularNoTitle;
    private String  specials;
    private String  specialsNoTitle;

    private String destination;
    private boolean skipDuplicateFiles;

    @Override
    public void describe()
    {
        logger.warn("RenameMedia rename media into a standard format as configured.");
        logger.warn("         regular: {}", this.regular);
        logger.warn("  regularNoTitle: {}", this.regularNoTitle);
        logger.warn("        specials: {}", this.specials);
        logger.warn(" specialsNoTitle: {}", this.specialsNoTitle);
    }


    @Override
    public boolean configure(MediaToolConfig config)
    {
        this.regular = config.getRenameMedia().get("regular");
        this.regularNoTitle = config.getRenameMedia().get("regularNoTitle");
        this.specials = config.getRenameMedia().get("specials");
        this.specialsNoTitle = config.getRenameMedia().get("specialsNoTitle");
        this.destination = config.getDestination();
        this.enabled = Boolean.parseBoolean(config.getRenameMedia().get("enabled"));
        this.skipDuplicateFiles = Boolean.parseBoolean(config.getRenameMedia().get("skip_duplicate_files"));
        return this.enabled;
    }


    @Override
    public IMediaDetails process(IMediaDetails details) {
        if (!this.enabled)
            return details;

        String formatString = getNameFormatForMediaFile(details);
        File originalFile = details.getMediaFile();
        File destinationFile = generateDestinationFilename(details, formatString);
        if (destinationFile == null)
            return null;

        if (destinationFile.isDirectory())
            throw new InvalidPathException(destinationFile.getAbsolutePath(), "Destination is a directory");

        if (!ensureDirectoryExists(destinationFile))
            throw new InvalidPathException(destinationFile.getAbsolutePath(), "Cannot create destination parent directory");

        if (destinationFile.exists()) {
            if (this.skipDuplicateFiles)
                throw new InvalidPathException(destinationFile.getAbsolutePath(), "Destination already exists");
            else {
                logger.info("File has already been recorded and processed, skipping...");
                return null;
            }
        }

        try {
            Files.move(originalFile, destinationFile);
            logger.info("Moved.");
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
        }
        return details;
    }

    private String getNameFormatForMediaFile(IMediaDetails details) {
        String formatString;
        if (TokenParser.isSpecial(details)) {
            formatString = TokenParser.hasEpisodeTitle(details) ? this.specials : this.specialsNoTitle;
        } else {
            formatString = TokenParser.hasEpisodeTitle(details) ? this.regular : this.regularNoTitle;
        }
        return formatString;
    }

    File generateDestinationFilename(IMediaDetails details, String formatString)
    {
        try {
            List<String> tokens = TokenParser.parseTokens(formatString);
            StringBuilder destFileName = new StringBuilder();
            for (String token : tokens) {
                destFileName.append(TokenParser.getMediaToken(details, token));
            }
            File destinationFile = Paths.get(this.destination, destFileName.toString()).toFile();
            if (logger.isInfoEnabled()) {
                logger.info("----------------------------------");
                logger.info("Moving {}", details.getMediaFile());
                logger.info("    to {}", destinationFile);
            }
            return destinationFile;
        }
        catch (ParseException p) {
            logger.error(p.getMessage());
            return null;
        }
        catch (UnknownTokenException u) {
            logger.error("Unknown Token {} in format string: {}", u.getToken(), formatString);
            return null;
        }
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