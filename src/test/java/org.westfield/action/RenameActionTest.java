package org.westfield.action;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.westfield.media.IMediaDetails;

import java.util.Map;

public class RenameActionTest
{

    @Test
    public void RenameSeasonTitleTest()
    {
        final Map<String, String> test_config = ImmutableMap.of(
                "format", "{Show}/{Season}/{Show}-{Episode}-{Title}.{Format}",
                "enabled", "true"
        );
        RenameAction subject = new RenameAction();
        subject.configure(test_config);
        IMediaDetails mock = Mockito.mock(IMediaDetails.class);

        IMediaDetails result = subject.process(mock);

        Assert.assertNotNull(result);
    }
}
