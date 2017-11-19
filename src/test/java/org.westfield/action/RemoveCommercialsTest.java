package org.westfield.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.Mockito.when;

public class RemoveCommercialsTest
{
    @Test
    public void processNoCutList() throws Exception
    {
        File fileMock = Mockito.mock(File.class);
        when(fileMock.getName()).thenReturn("pitchin-s04s10.mpg");

        IMediaDetails mock = Mockito.mock(IMediaDetails.class);
        when(mock.getMediaFile()).thenReturn(fileMock);

        RemoveCommercials subject = getSubject();
        IMediaDetails result = subject.process(mock);

        when(mock.getExtendedDetails()).thenReturn(ImmutableMap.of("foo", "bar"));

        Assert.assertNotNull(result);
    }

    @Test
    @Ignore
    public void process() throws Exception
    {
        File fileMock = Mockito.mock(File.class);
        when(fileMock.getName()).thenReturn("pitchin-s04s10.mpg");
        when(fileMock.getAbsolutePath()).thenReturn("/data/git/pitchin-s04s10.mpg");

        IMediaDetails mock = Mockito.mock(IMediaDetails.class);
        when(mock.getMediaFile()).thenReturn(fileMock);

        List<String> cutlist = ImmutableList.of(
                "JumpSegment(\"From=0.0000\",\"To=2.0687\")",
                "JumpSegment(\"From=412.1451\",\"To=587.9874\")",
                "JumpSegment(\"From=889.6888\",\"To=1035.4678\")",
                "JumpSegment(\"From=1309.3747\",\"To=1465.1303\")",
                "JumpSegment(\"From=1793.0913\",\"To=1798.9972\")");
        when(mock.getExtendedDetails()).thenReturn(ImmutableMap.of("CutList", cutlist));

        // Act
        RemoveCommercials subject = getSubject();
        IMediaDetails result = subject.process(mock);


        Assert.assertNotNull(result);
    }

    @Test
    public void decimalFormatTest()
    {
        String result = RemoveCommercials.DECIMAL_FORMAT.format(3.5671);
        Assert.assertEquals("3.5671", result);

        result = RemoveCommercials.DECIMAL_FORMAT.format(1f);
        Assert.assertEquals("1", result);
    }

    @Test
    public void generateDestinationChapterName()
    {
        RemoveCommercials subject = getSubject();
        String result = subject.chapterFilename("foo.bar", 3);

        // The file will be in a temporary directory and will end with
        Assert.assertTrue(result.endsWith("foo-Chapter3.bar"));
    }



    private RemoveCommercials getSubject()
    {
        MediaToolConfig config =  Mockito.mock(MediaToolConfig.class);
        when(config.getRemoveCommercials()).thenReturn(ImmutableMap.of(
                "enabled", "true",
                "ffmpeg", "/usr/bin/ffmpeg"
        ));
        RemoveCommercials subject = new RemoveCommercials();
        boolean configureResult = subject.configure(config);
        Assert.assertTrue (configureResult);
        return subject;
    }
}