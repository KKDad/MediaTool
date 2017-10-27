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
            LocalDateTime time = LocalDateTime.now().minusMinutes(30);
            ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
            long epoch = time.atZone(zoneId).toEpochSecond();

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
            when(fileMock.lastModified()).thenReturn(epoch);

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
            LocalDateTime time = LocalDateTime.now().minusMinutes(90);
            ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
            long epoch = time.atZone(zoneId).toEpochSecond();

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
            when(fileMock.lastModified()).thenReturn(epoch);

            IMediaDetails result = subject.process(mock);
            Assert.assertNotNull(result);
        }
        catch (Exception ex)
        {
            fail();
        }
    }
}