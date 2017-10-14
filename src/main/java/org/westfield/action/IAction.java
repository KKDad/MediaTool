package org.westfield.action;

import org.westfield.media.IMediaDetails;

import java.util.Map;

public interface IAction
{
    // Configure the Action from the supplied map
    boolean configure(Map<String, String> config);

    // Process the media file. Return null to prevent further processing.
    IMediaDetails process(IMediaDetails details);
}
