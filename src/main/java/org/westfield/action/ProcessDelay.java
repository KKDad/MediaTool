package org.westfield.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ProcessDelay implements IAction
{
    private static final Logger logger = LoggerFactory.getLogger(ProcessDelay.class);


    private boolean enabled;
    private int minutesIdle;


    @Override
    public boolean configure(MediaToolConfig config)
    {
        this.enabled = Boolean.parseBoolean(config.getProcessDelay().get("enabled"));
        this.minutesIdle = Integer.parseInt(config.getProcessDelay().get("minutesIdle"));
        return this.enabled;
    }

    @Override
    public IMediaDetails process(IMediaDetails details)
    {
        if (!this.enabled)
            return details;
        LocalDateTime waitUntil = LocalDateTime.now().minusMinutes(this.minutesIdle);
        LocalDateTime lastModified = Instant.ofEpochSecond(details.getMediaFile().lastModified()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (lastModified.isBefore(waitUntil))
            return details;
        else {
            logger.info("Not processing {}, waiting until last modified is > {} minutes.", details.getMediaFile().getName(), this.minutesIdle);
            return null;
        }
    }
}
