package org.westfield.action;

import org.westfield.media.IMediaDetails;

public class RenameAction implements IAction
{
    @Override
    public boolean configure() {
        return false;
    }

    @Override
    public boolean isValid(IMediaDetails details) {
        return false;
    }
}
