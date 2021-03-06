package org.westfield.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

import java.util.Date;

public class ProcessDelay extends Action
{
    private static final Logger logger = LoggerFactory.getLogger(ProcessDelay.class);

    private static final long ONE_MINUTE_IN_MILLIS=60000;

    private int minutesIdle;
    private long millisIdle;

    @Override
    public void describe()
    {
        logger.warn("ProcessDelay will skip processing of file recorded within the last {} minutes.", this.minutesIdle);
    }

    @Override
    public boolean configure(MediaToolConfig config)
    {
        this.enabled = Boolean.parseBoolean(config.getProcessDelay().get("enabled"));
        this.minutesIdle = Integer.parseInt(config.getProcessDelay().get("minutesIdle"));
        this.millisIdle = (ONE_MINUTE_IN_MILLIS) * this.minutesIdle;
        logger.debug("ProcessDelay is {}", this.enabled ? "enabled" : "Disabled");
        return this.enabled;
    }

    @Override
    public IMediaDetails process(IMediaDetails details)
    {
        if (!this.enabled)
            return details;
        Date waitUntil =  new Date(System.currentTimeMillis() - this.millisIdle);
        Date mediaLastModified = new Date(details.getMediaFile().lastModified());
        if (mediaLastModified.before(waitUntil))
            return details;
        else {
            logger.info("Not processing {}, waiting until last modified is > {} minutes.", details.getMediaFile().getName(), this.minutesIdle);
            logger.info("    Skipping media modified after: {}", waitUntil);
            logger.info("                           Media : {}", mediaLastModified);
            return null;
        }
    }
}
