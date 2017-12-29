package org.westfield.action;

import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

public class MockAction extends Action
{
    @Override
    public boolean configure(MediaToolConfig config)
    {
        // Allow the MockAction to be enabled/disabled for different tests
        return config.getActions().contains("MockAction");
    }

    @Override
    public IMediaDetails process(IMediaDetails details) {
        return null;
    }

    @Override
    public void describe() {
        // MockAction, does nothing
    }
}
