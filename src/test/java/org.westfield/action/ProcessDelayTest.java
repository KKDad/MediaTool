package org.westfield.action;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ProcessDelayTest
{



    @Test
    public void processShowSkip()
    {
        try {
            long tenMinutesAgo = System.currentTimeMillis() - (60000 * 10);

            MediaToolConfig config = Mockito.mock(MediaToolConfig.class);
            when(config.getProcessDelay()).thenReturn(ImmutableMap.of(
                    "enabled", "true",
                    "minutesIdle", "60"
            ));

            ProcessDelay subject = new ProcessDelay();
            subject.configure(config);

            IMediaDetails mock = Mockito.mock(IMediaDetails.class);
            File fileMock = Mockito.mock(File.class);
            when(mock.getMediaFile()).thenReturn(fileMock);
            when(fileMock.lastModified()).thenReturn(tenMinutesAgo);

            IMediaDetails result = subject.process(mock);
            Assert.assertNull(result);
        }
        catch (Exception ex)
        {
            fail();
        }
    }


    @Test
    public void processShowProcess()
    {
        try {
            long nintyMinutesAgo = System.currentTimeMillis() - (60000 * 90);


            MediaToolConfig config = Mockito.mock(MediaToolConfig.class);
            when(config.getProcessDelay()).thenReturn(ImmutableMap.of(
                    "enabled", "true",
                    "minutesIdle", "45"
            ));

            ProcessDelay subject = new ProcessDelay();
            subject.configure(config);

            IMediaDetails mock = Mockito.mock(IMediaDetails.class);
            File fileMock = Mockito.mock(File.class);
            when(mock.getMediaFile()).thenReturn(fileMock);
            when(mock.getMediaFile().getName()).thenReturn("processShowProcess");
            when(fileMock.lastModified()).thenReturn(nintyMinutesAgo);

            IMediaDetails result = subject.process(mock);
            Assert.assertNotNull(result);
        }
        catch (Exception ex)
        {
            fail();
        }
    }
}