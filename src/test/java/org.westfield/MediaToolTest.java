package org.westfield;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.westfield.action.MockAction;
import org.westfield.configuration.MediaToolConfig;

import static org.mockito.Mockito.when;

public class MediaToolTest
{
    @Test
    public void GetEnabledActionTest()
    {
        MediaToolConfig mock_config = Mockito.mock(MediaToolConfig.class);
        when(mock_config.getActions()).thenReturn(ImmutableList.of("MockAction"));

        MediaTool subject = new MediaTool(mock_config);
        subject.getAction("org.westfield.action.MockAction");
        Assert.assertEquals(1, subject.actions.size());
        Assert.assertEquals(MockAction.class, (subject.actions.get(0)).getClass());
    }

    @Test
    public void GetDisabledActionTest()
    {
        MediaToolConfig mock_config = Mockito.mock(MediaToolConfig.class);
        when(mock_config.getActions()).thenReturn(ImmutableList.of("No MockAction"));

        MediaTool subject = new MediaTool(mock_config);
        subject.getAction("org.westfield.action.MockAction");
        Assert.assertEquals(0, subject.actions.size());
    }

    @Test
    public void GetInvalidActionTest()
    {
        MediaToolConfig mock_config = Mockito.mock(MediaToolConfig.class);

        MediaTool subject = new MediaTool(mock_config);
        subject.getAction("org.westfield.action.DoesNotExist");
        Assert.assertEquals(0, subject.actions.size());
    }

}
