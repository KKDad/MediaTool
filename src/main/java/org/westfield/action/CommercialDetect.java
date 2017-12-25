package org.westfield.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.ProcessingLoggingHandler;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

import java.util.List;

public class CommercialDetect implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(CommercialDetect.class);

    private boolean enabled;
    private String comskip;
    private String comskipIni;
    private String threads;
    private Boolean hwassist;


    @Override
    public boolean configure(MediaToolConfig config) {
        this.enabled = Boolean.parseBoolean(config.getCommercialDetect().get("enabled"));
        this.comskip = config.getCommercialDetect().get("comskip");
        this.comskipIni = config.getCommercialDetect().get("configuration");
        this.threads = config.getCommercialDetect().get("threads");
        this.hwassist = Boolean.parseBoolean(config.getCommercialDetect().get("hwassist"));

        logger.debug("CommercialDetect is {}", this.enabled ? "Enabled" : "Disabled");
        return true;
    }

    @Override
    public IMediaDetails process(IMediaDetails details) {
        if (this.enabled)
            return details;

        if (!detectCommercials(details.getMediaFile().getAbsolutePath()))
            return details;
        return null;
    }

    private boolean detectCommercials(String fileName)
    {
        try {
            logger.info("Checking for Commercials...");
            Process process = new ProcessBuilder(this.comskip, "--ini", this.comskipIni, "--threads", this.threads, this.hwassist ? "--hwassist" : "", "-zpcut", fileName).start();
            ProcessingLoggingHandler outputHandler = new ProcessingLoggingHandler(process.getInputStream(), logger, null);
            ProcessingLoggingHandler errorHandler = new ProcessingLoggingHandler(process.getErrorStream(), logger, null);
            outputHandler.start();
            errorHandler.start();
            int rc = process.waitFor();
            if (rc != 0)
                logger.warn("Command exited with code: {}", rc);
            return rc == 0;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return false;
    }


}
