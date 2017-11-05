package org.westfield.action;

import com.google.common.collect.ImmutableMap;
import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.westfield.TestHelper;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.HDHomeRunTagParser;
import org.westfield.media.IMediaDetails;

import java.nio.file.Path;

import static org.mockito.Mockito.when;

public class ShowLookupTest
{

    @Test
    public void processCriminalMinds() throws Exception
    {
        MediaToolConfig config =  Mockito.mock(MediaToolConfig.class);
        when(config.getShowLookup()).thenReturn(ImmutableMap.of(
                "key", "70FBF9A03F0D083D",
                "enabled", "true"
        ));
        Path item = TestHelper.getTestResourcePath("criminal-s11e03.dat");
        IMediaDetails media_details = HDHomeRunTagParser.fromFile(item.toFile());

        ShowLookup subject = new ShowLookup();
        subject.configure(config);
        IMediaDetails result = subject.process(media_details);

        Assert.assertEquals(6, result.getExtendedDetails().size());
    }
}