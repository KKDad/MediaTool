package org.westfield.action;

import org.westfield.media.IMediaDetails;

public interface IAction
{
    boolean IsValid(IMediaDetails details);
}
