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

    List<IAction> actions = new ArrayList<>();

    public MediaTool(MediaToolConfig config)
    {
        this.config = config;
    }

    MediaTool setup()
    {
        logger.info("Setting up");
        this.config.getActions().forEach(this::getAction);
        return this;
    }

    void getAction(String actionClass)
    {
        IAction action = instantiate(actionClass, IAction.class);
        if (action != null) {
            logger.error("Configuring {}.", actionClass);
            if (action.configure(this.config)) {
                logger.error("Adding {} to actions.", actionClass);
                this.actions.add(action);
            } else {
                logger.error("{} is disabled.", actionClass);
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
        try {
            IMediaDetails media = HDHomeRunTagParser.fromFile(item.toFile());
            if (media != null) {
                logger.info("Processing {}", item.getFileName());
                for (IAction action : this.actions) {
                    if (media != null)
                        media = action.process(media);
                }
                logger.info("{}", item);
            }
        }
        catch (Exception ex)
        {
            logger.error("{}", ex.getMessage(), ex);
        }
        logger.info("File finished\n\n");

    }
}
