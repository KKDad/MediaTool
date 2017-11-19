package org.westfield.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

public class CommercialDetect implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(CommercialDetect.class);

    private boolean enabled;

    @Override
    public boolean configure(MediaToolConfig config) {
        this.enabled = Boolean.parseBoolean(config.getCommercialDetect().get("enabled"));
        logger.debug("CommercialDetect is {}", this.enabled ? "Enabled" : "Disabled");
        return true;
    }

    @Override
    public IMediaDetails process(IMediaDetails details) {
        if (this.enabled)
            return details;

        // Comskip/comskip --threads=6 --hwassist --zpcut Eat-s01s05.m
        return null;
    }
}
