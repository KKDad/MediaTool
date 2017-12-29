package org.westfield.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

import java.io.File;
import java.util.List;

import static org.mockito.Mockito.when;

public class RemoveCommercialsTest
{
    @Test
    public void configEnabledTest() {
        MediaToolConfig config = Mockito.mock(MediaToolConfig.class);
        when(config.getRemoveCommercials()).thenReturn(ImmutableMap.of(
                "enabled", "true"
        ));

        RemoveCommercials subject = new RemoveCommercials();
        subject.configure(config);
        subject.describe();

        Assert.assertTrue(subject.enabled());
    }

    @Test
    public void configDisabledTest() {
        MediaToolConfig config = Mockito.mock(MediaToolConfig.class);
        when(config.getRemoveCommercials()).thenReturn(ImmutableMap.of(
                "enabled", "false"
        ));

        RemoveCommercials subject = new RemoveCommercials();
        subject.configure(config);
        subject.describe();

        Assert.assertFalse(subject.enabled());
    }

    @Test
    public void formatTimeTest() throws Exception {

        RemoveCommercials subject = getSubject();

        Assert.assertEquals("3", subject.formatTime(3));
        Assert.assertEquals("1:01", subject.formatTime(61));
        Assert.assertEquals("2:00", subject.formatTime(120));
        Assert.assertEquals("1:00:00", subject.formatTime(3600));
    }

    @Test
    public void processNoCutListTest() throws Exception
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
    public void ParseChapterListTest() throws Exception
    {
        List<String> cutlist = ImmutableList.of(
                "JumpSegment(\"From=0.0000\",\"To=2.0687\")",
                "JumpSegment(\"From=412.1451\",\"To=587.9874\")",
                "JumpSegment(\"From=889.6888\",\"To=1035.4678\")",
                "JumpSegment(\"From=1309.3747\",\"To=1465.1303\")",
                "JumpSegment(\"From=1793.0913\",\"To=1798.9972\")");

        // Act
        RemoveCommercials subject = getSubject();
        List<RemoveCommercials.Chapter> result = subject.getChapterLists(cutlist);

        Assert.assertNotNull(result);
        Assert.assertEquals(5, result.size());
        Assert.assertEquals(2.0687, result.get(0).start, 0.0001 );
        Assert.assertEquals(410.07642, result.get(0).length, 0.0001 );
        Assert.assertEquals(587.9874, result.get(1).start, 0.0001 );
        Assert.assertEquals(301.70135, result.get(1).length, 0.0001 );
        Assert.assertEquals(1035.4678, result.get(2).start, 0.0001 );
        Assert.assertEquals(273.90698, result.get(2).length, 0.0001 );
        Assert.assertEquals(1465.1302, result.get(3).start, 0.0001 );
        Assert.assertEquals(327.96106, result.get(3).length, 0.0001 );
        Assert.assertEquals(1798.9972, result.get(4).start, 0.0001 );
        Assert.assertEquals(-1, result.get(4).length, 0.0001 );
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
    public void generateDestinationChapterNameTest()
    {
        RemoveCommercials subject = getSubject();
        String result = subject.chapterFilename("foo.bar", 3);

        // The file will be in a temporary directory and will end with
        Assert.assertTrue(result.endsWith("foo-Chapter3.bar"));
    }

    @Test
    public void generateFinalNameTest()
    {
        RemoveCommercials subject = getSubject();
        String result = subject.finalFilename("foo.bar");

        // The file will be in a temporary directory and will end with
        Assert.assertTrue(result.endsWith("foo-new.bar"));
    }

    @Test
    public void generateMapTest()
    {
        RemoveCommercials subject = getSubject();
        String rc2 = subject.generateMap(2);
        String rc3 = subject.generateMap(3);
        String rc4 = subject.generateMap(4);

        Assert.assertEquals("-map 0:0 -map 0:1", rc2);
        Assert.assertEquals("-map 0:0 -map 0:1 -map 0:2", rc3);
        Assert.assertEquals("-map 0:0 -map 0:1 -map 0:2 -map 0:3", rc4);
    }

    @Test
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