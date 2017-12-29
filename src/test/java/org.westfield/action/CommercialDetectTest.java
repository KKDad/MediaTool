package org.westfield.action;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.when;

public class CommercialDetectTest
{
    @Test
    public void configEnabledTest() {
        MediaToolConfig config = Mockito.mock(MediaToolConfig.class);
        when(config.getCommercialDetect()).thenReturn(ImmutableMap.of(
                "enabled", "true"
        ));

        CommercialDetect subject = new CommercialDetect();
        subject.configure(config);

        Assert.assertTrue(subject.enabled());
    }

    @Test
    public void configDisabledTest() {
        MediaToolConfig config = Mockito.mock(MediaToolConfig.class);
        when(config.getCommercialDetect()).thenReturn(ImmutableMap.of(
                "enabled", "false"
        ));

        CommercialDetect subject = new CommercialDetect();
        subject.configure(config);
        subject.describe();

        Assert.assertFalse(subject.enabled());
    }


    @Test
    public void cleanupTest() throws IOException {
        MediaToolConfig config = Mockito.mock(MediaToolConfig.class);
        when(config.getBackupOriginal()).thenReturn(ImmutableMap.of(
                "enabled", "true"
        ));

        CommercialDetect subject = new CommercialDetect();
        subject.configure(config);
        subject.describe();

        Files.touch(new File("/tmp/foo.bar.cut"));
        Assert.assertTrue(new File("/tmp/foo.bar.cut").exists());
        subject.cleanup(testMedia());
        Assert.assertFalse(new File("/tmp/foo.bar.cut").exists());
    }


    private IMediaDetails testMedia()
    {
        File fileMock = Mockito.mock(File.class);
        when(fileMock.getName()).thenReturn("foo.bar.mp4");
        when(fileMock.getAbsolutePath()).thenReturn("/tmp/foo.bar.mp4");
        when(fileMock.getParent()).thenReturn("/tmp");

        IMediaDetails mock = Mockito.mock(IMediaDetails.class);
        when(mock.getMediaFile()).thenReturn(fileMock);

        return mock;
    }

}