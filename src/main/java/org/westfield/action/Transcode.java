package org.westfield.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

public class Transcode implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(Transcode.class);

    private boolean enabled;

    @Override
    public boolean configure(MediaToolConfig config) {
        this.enabled = Boolean.parseBoolean(config.getTranscode().get("enabled"));
        logger.debug("Transcode is {}", this.enabled ? "Enabled" : "Disabled");
        return true;
    }

    @Override
    public IMediaDetails process(IMediaDetails details) {
        if (this.enabled)
            return details;

        return null;
    }
}
