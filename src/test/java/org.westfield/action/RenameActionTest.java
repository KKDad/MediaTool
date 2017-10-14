package org.westfield.action;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.westfield.media.IMediaDetails;

import java.io.File;
import java.util.Map;

import static org.mockito.Mockito.when;

public class RenameActionTest
{

    @Test
    public void RenameSeasonTitleTest()
    {
        final Map<String, String> test_config = ImmutableMap.of(
                "format", "{Show}/Season {Season}/{Show}-S{Season}E{Episode}-{Title}.{Format}",
                "enabled", "true"
        );
        RenameAction subject = new RenameAction();
        boolean configureResult = subject.configure(test_config);
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
