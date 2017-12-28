package org.westfield.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;

public class BackupOriginal implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(BackupOriginal.class);

    private boolean enabled;
    private String backupLocation;

    @Override
    public void describe()
    {
        logger.warn("BackupOriginal will copy media files to {}", this.backupLocation);
    }

    @Override
    public boolean configure(MediaToolConfig config) {
        this.enabled = Boolean.parseBoolean(config.getBackupOriginal().get("enabled"));
        this.backupLocation = config.getBackupOriginal().get("backup_location");
        logger.debug("BackupOriginal is {}", this.enabled ? "Enabled" : "Disabled");
        logger.debug("   Backing up to: {}", this.backupLocation);
        return true;
    }

    @Override
    public IMediaDetails process(IMediaDetails details)
    {
        if (this.enabled)
            return details;
        File destinationFile = new File(backupLocation);
        try {
            if (!destinationFile.exists() && !destinationFile.mkdirs())
                throw new InvalidPathException(destinationFile.getAbsolutePath(), String.format("Destination %s does not exist and cannot make it", this.backupLocation));

            destinationFile = new File(destinationFile, details.getMediaFile().getName());
            if (destinationFile.exists()) {
                logger.info("Media {} has already been backed up to {}...", details.getMediaFile().toPath(), destinationFile.toPath());
            }

            logger.info("Backing up {} to {}...", details.getMediaFile().toPath(), destinationFile.toPath());
            Files.copy(details.getMediaFile().toPath(), destinationFile.toPath());
            logger.debug("Backup complete");

            return details;
        } catch (IOException e) {
            logger.error("Unable to copy {} to {}", details.getMediaFile().getName(), destinationFile);
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
