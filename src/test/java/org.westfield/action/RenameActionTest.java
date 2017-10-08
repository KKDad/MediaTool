package org.westfield.action;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.westfield.media.IMediaDetails;

public class RenameActionTest
{

    @Test
    public void IsValidTest()
    {
        RenameAction subject = new RenameAction();
        IMediaDetails mock = Mockito.mock(IMediaDetails.class);

        Assert.assertTrue(!subject.isValid(mock));
    }
}
