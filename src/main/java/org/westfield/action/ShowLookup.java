package org.westfield.action;

import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

public class ShowLookup implements IAction {
    @Override
    public boolean configure(MediaToolConfig config) {
        return false;
    }

    @Override
    public IMediaDetails process(IMediaDetails details) {
        return null;
    }
}
