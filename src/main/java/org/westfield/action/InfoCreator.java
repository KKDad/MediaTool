package org.westfield.action;

import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

/**
 * Create a Info file for the TV Show for use by Kodi
 */
public class InfoCreator implements IAction
{
    @Override
    public boolean configure(MediaToolConfig config) {
        return false;
    }

    @Override
    public IMediaDetails process(IMediaDetails details) {
        return null;
    }
}
