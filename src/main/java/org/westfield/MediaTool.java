package org.westfield;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.action.IAction;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.HDHomeRunTagParser;
import org.westfield.media.IMediaDetails;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


class MediaTool {

    private final MediaToolConfig config;
    private static final Logger logger = LoggerFactory.getLogger(MediaTool.class);
    private Args options;

    List<IAction> actions = new ArrayList<>();

    public MediaTool(MediaToolConfig config)
    {
        this.config = config;
    }

    MediaTool setOptions(Args options) {
        this.options = options;
        return this;
    }

    MediaTool setup()
    {
        logger.debug("Setting up");
        this.config.getActions().forEach(this::getAction);
        logger.debug("Setup Complete\n--------------------------------------------\n\n");
        return this;
    }

    void getAction(String actionClass)
    {
        IAction action = instantiate(actionClass, IAction.class);
        if (action != null) {
            logger.debug("Configuring {}.", actionClass);
            if (action.configure(this.config)) {
                logger.debug("Adding {} to actions.", actionClass);
                this.actions.add(action);
            } else {
                logger.debug("{} is disabled.", actionClass);
            }
        }
    }

    private <T> T instantiate(final String className, final Class<T> type){
        try
        {
            return type.cast(Class.forName(className).newInstance());
        }
        catch(InstantiationException | IllegalAccessException | ClassNotFoundException e)
        {
            logger.error("Could not instantiate: '{}':", className, e);
        }
        return null;
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
                if (destination.mkdirs()) {
                    logger.error("Created Destination directory '{}'.", config.getDestination());
                } else {
                    logger.error("Destination directory '{}' does not exist, and cannot make it.", config.getDestination());
                    return;
                }
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

    void run(String item) {
        Path source = Paths.get(item);
        this.process(source);
    }

    @SuppressWarnings("squid:S1181")
    private void process(Path item)
    {
        long sizeBefore;
        long sizeAfter;
        try {
            IMediaDetails media = HDHomeRunTagParser.fromFile(item.toFile());
            if (media != null) {
                sizeBefore= media.getMediaFile().length();
                logger.info("Processing {}", item.getFileName());
                for (IAction action : this.actions) {
                    if (media != null)
                        media = action.process(media);
                }
                logger.info("{}", item);
                if (media != null) {
                    sizeAfter = media.getMediaFile().length();
                    logger.info("   Before Processing: {} bytes", sizeBefore);
                    logger.info("   Before Processing: {} bytes", sizeAfter);
                }

            }
        }
        catch (Exception ex)
        {
            logger.error("Exception: {}", ex.getMessage());
            if (this.options.failOnError)
                System.exit(1);
        }
        catch (Throwable rte) {
            logger.error("Throwable: {}", rte.getMessage());
            System.exit(1);
        }
        logger.info("File finished\n\n");
    }

    void showOrder()
    {
        logger.info("The following actions will be applied in this order: ");
        logger.info("*****************************************************");
        for (IAction action : this.actions)
                action.describe();
        System.exit(0);
    }
}
