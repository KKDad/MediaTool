package org.westfield.action;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.westfield.configuration.MediaToolConfig;

import static org.mockito.Mockito.when;

public class TranscodeTest
{
    @Test
    public void configEnabledTest() {
        MediaToolConfig config = Mockito.mock(MediaToolConfig.class);
        when(config.getTranscode()).thenReturn(ImmutableMap.of(
                "enabled", "true"
        ));

        IAction subject = new Transcode();
        subject.configure(config);
        subject.describe();

        Assert.assertTrue(subject.enabled());
    }

    @Test
    public void configDisabledTest() {
        MediaToolConfig config = Mockito.mock(MediaToolConfig.class);
        when(config.getTranscode()).thenReturn(ImmutableMap.of(
                "enabled", "false"
        ));

        IAction subject = new Transcode();
        subject.configure(config);
        subject.describe();

        Assert.assertFalse(subject.enabled());
    }

}