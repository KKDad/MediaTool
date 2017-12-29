package org.westfield.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

public class Transcode extends Action {
    private static final Logger logger = LoggerFactory.getLogger(Transcode.class);


    @Override
    public void describe()
    {
        logger.warn("Transcode use ffmpeg to convert media to H265 to reduce storage requirements.");
    }


    @Override
    public boolean configure(MediaToolConfig config) {
        this.enabled = Boolean.parseBoolean(config.getTranscode().get("enabled"));
        logger.debug("Transcode is {}", this.enabled ? "enabled" : "Disabled");
        return true;
    }

    @Override
    public IMediaDetails process(IMediaDetails details) {
        if (this.enabled)
            return details;

        return null;
    }
}
