package org.westfield.action;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

import java.io.File;

import static org.mockito.Mockito.when;

public class RenameMediaTest
{

    @Test
    public void RenameSeasonTitleTest()
    {
        MediaToolConfig config =  Mockito.mock(MediaToolConfig.class);
        when(config.getRenameMedia()).thenReturn(ImmutableMap.of(
                "format", "{Show}/Season {Season}/{Show}-S{Season}E{Episode}-{Title}.{Format}",
                "enabled", "true"
        ));
        RenameMedia subject = new RenameMedia();
        boolean configureResult = subject.configure(config);
        Assert.assertTrue (configureResult);

        IMediaDetails mock = Mockito.mock(IMediaDetails.class);
        File fileMock = Mockito.mock(File.class);
        when(mock.getShow()).thenReturn("myshow");
        when(mock.getEpisodeNumber()).thenReturn(3);
        when(mock.getEpisodeTitle()).thenReturn("theEpisodetitle");
        when(mock.getSeason()).thenReturn(9);
        when(mock.getMediaFile()).thenReturn(fileMock);
        when(fileMock.getName()).thenReturn("foo.bar.mpg");

        IMediaDetails result = subject.process(mock);
        Assert.assertNotNull(result);
        Assert.assertEquals("myshow-S09E03-theEpisodetitle.mpg", result.getMediaFile().getName());
    }
}
