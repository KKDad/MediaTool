package org.westfield.action;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.ProcessingLoggingHandler;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class CommercialDetect  extends Action {
    private static final Logger logger = LoggerFactory.getLogger(CommercialDetect.class);

    private String comskip;
    private String comskipIni;
    private String threads;
    private Boolean hwassist;
    private Boolean saveCutList;

    @Override
    public void describe()
    {
        logger.warn("CommercialDetect will use comskip to locate commercials inside the media file");
        logger.warn("               Binary: {}", this.comskip);
        logger.warn("        Configuration: {}", this.comskipIni);
    }


    @Override
    public boolean configure(MediaToolConfig config) {
        this.enabled = Boolean.parseBoolean(config.getCommercialDetect().get("enabled"));
        this.comskip = config.getCommercialDetect().get("comskip");
        this.comskipIni = config.getCommercialDetect().get("configuration");
        this.threads = config.getCommercialDetect().get("threads");
        this.hwassist = Boolean.parseBoolean(config.getCommercialDetect().get("hwassist"));
        this.saveCutList = Boolean.parseBoolean(config.getCommercialDetect().get("saveCutList"));

        logger.debug("CommercialDetect is {}", this.enabled ? "enabled" : "Disabled");
        return true;
    }

    @Override
    public IMediaDetails process(IMediaDetails details)
    {
        if (!this.enabled)
            return details;

        // Reuse the preserved CutList if configured to do so
        if (this.saveCutList && loadCutList(details))
            return details;

        boolean hasCommercials = detectCommercials(details);
        if (hasCommercials)
            loadCutList(details);

        cleanup(details);

        return details;
    }

    private boolean detectCommercials(IMediaDetails details)
    {

        try {
            logger.info("Checking for Commercials...");
            Process process = new ProcessBuilder(this.comskip, "--ini", this.comskipIni, "--threads", this.threads, this.hwassist ? "--hwassist" : "", "--zpcut", details.getMediaFile().getAbsolutePath()).start();
            ProcessingLoggingHandler outputHandler = new ProcessingLoggingHandler("STDOUT", process.getInputStream(), logger, null);
            ProcessingLoggingHandler errorHandler = new ProcessingLoggingHandler("STDERR", process.getErrorStream(), logger, null);
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

    private boolean loadCutList(IMediaDetails details)
    {
        try {
            File cutFile = new File(details.getMediaFile().getParent(), Files.getNameWithoutExtension(details.getMediaFile().getAbsolutePath()) + ".cut");
            if (cutFile.exists()) {
                List<String> jumpSegments = Files.asCharSource(cutFile, Charset.forName("UTF-8")).readLines();
                details.getExtendedDetails().putIfAbsent("CutList", jumpSegments);
                return true;
            } else {
                logger.warn("CutList not located.");
            }
        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
        }
        return false;
    }

    @SuppressWarnings("squid:S899")
    void cleanup(IMediaDetails details) {
        List<String> tempExtensions = ImmutableList.of(".cut", ".txt", ".VPrj");

        for (String ext : tempExtensions) {
            if (this.saveCutList && ext.endsWith(".cut"))
                continue;
            File cutFile = new File(details.getMediaFile().getParent(), Files.getNameWithoutExtension(details.getMediaFile().getAbsolutePath()) + ext);
            if (cutFile.exists())
                cutFile.delete();
        }
    }


}
