package org.westfield.action;

import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

public interface IAction
{
    // Configure the Action from the supplied map
    boolean configure(MediaToolConfig config);

    // Process the media file. Return null to prevent further processing.
    IMediaDetails process(IMediaDetails details);

    // Describe what the configured action will do.
    void describe();
}
