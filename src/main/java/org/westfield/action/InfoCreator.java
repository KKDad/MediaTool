package org.westfield.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

/**
 * Create a Info file for the TV Show for use by Kodi
 */
public class InfoCreator implements IAction
{
    private static final Logger logger = LoggerFactory.getLogger(RenameMedia.class);

    private boolean enabled;

    @Override
    public boolean configure(MediaToolConfig config) {
        this.enabled = Boolean.parseBoolean(config.getRenameMedia().get("enabled"));
        return true;
    }

    @Override
    public IMediaDetails process(IMediaDetails details) {
        if (this.enabled)
            return details;

        return null;
    }
}
