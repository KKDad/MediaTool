package org.westfield.action;

import org.westfield.media.IMediaDetails;

public interface IAction
{
    boolean configure();
    boolean isValid(IMediaDetails details);
}
