package org.westfield.action;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.westfield.TestHelper;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.HDHomeRunTagParser;
import org.westfield.media.IMediaDetails;

import java.io.File;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class RenameMediaTest
{
    @Test
    public void ensureDirectoryExists() throws Exception
    {
        RenameMedia subject = getSubject();

        File parentFileMock = Mockito.mock(File.class);
        when(parentFileMock.getName()).thenReturn("/data/testing/TV Shows/Criminal Minds/Season 09");
        when(parentFileMock.isFile()).thenReturn(false);
        when(parentFileMock.exists()).thenReturn(true);
        when(parentFileMock.isDirectory()).thenReturn(true);

        File fileMock = Mockito.mock(File.class);
        when(fileMock.getName()).thenReturn("Criminal Minds - s09s13 - The Road Home.mpg");
        when(fileMock.isFile()).thenReturn(false);
        when(fileMock.getParentFile()).thenReturn(parentFileMock);

        boolean result = subject.ensureDirectoryExists(fileMock);
        assertTrue(result);
    }

    @Test
    public void RenameSeasonTitleTest()
    {
        RenameMedia subject = getSubject();

        IMediaDetails mock = Mockito.mock(IMediaDetails.class);
        File fileMock = Mockito.mock(File.class);
        when(mock.getShow()).thenReturn("myshow");
        when(mock.getEpisodeNumber()).thenReturn(3);
        when(mock.getEpisodeTitle()).thenReturn("theEpisodetitle");
        when(mock.getSeason()).thenReturn(9);
        when(mock.getMediaFile()).thenReturn(fileMock);
        when(fileMock.getName()).thenReturn("foo.bar.mp4");

        try {
            File result = subject.generateDestinationFilename(mock);
            Assert.assertNotNull(result);
            Assert.assertEquals("myshow-S09E03-theEpisodetitle.mp4", result.getName());
        } catch (Exception ex) {
            fail();
        }
    }


    @Test
    public void FunnyCharactersTest()
    {
        try {
            RenameMedia subject = getSubject();

            Path item = TestHelper.getTestResourcePath("jungle-s01e53.dat");
            IMediaDetails media_details = HDHomeRunTagParser.fromFile(item.toFile());


            File result = subject.generateDestinationFilename(media_details);
            Assert.assertNotNull("Les as de la jungle à la rescousse-S01E53-Drôle d'oiseau.dat", result.getName());
        } catch (Exception ex) {
            fail();
        }
    }



    private RenameMedia getSubject() {
        MediaToolConfig config =  Mockito.mock(MediaToolConfig.class);
        when(config.getRenameMedia()).thenReturn(ImmutableMap.of(
                "format", "{Show}/Season {Season}/{Show}-S{Season}E{Episode}-{Title}.{Format}",
                "enabled", "true"
        ));
        RenameMedia subject = new RenameMedia();
        boolean configureResult = subject.configure(config);
        Assert.assertTrue (configureResult);
        return subject;
    }
}
