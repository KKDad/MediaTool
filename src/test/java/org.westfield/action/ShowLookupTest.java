package org.westfield.action;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.westfield.TestHelper;
import org.westfield.configuration.LookupHint;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.HDHomeRunTagParser;
import org.westfield.media.IMediaDetails;
import org.westfield.parser.TokenParser;

import java.nio.file.Path;

import static org.mockito.Mockito.when;

public class ShowLookupTest
{

    @Test
    public void showLookupNoHintsTest() throws Exception
    {
        MediaToolConfig config =  Mockito.mock(MediaToolConfig.class);
        when(config.getShowLookup()).thenReturn(ImmutableMap.of(
                "key", "70FBF9A03F0D083D",
                "enabled", "true",
                "cache_key", "true",
                "cache_location", "/tmp/theTvdbKey.dat"
        ));
        Path item = TestHelper.getTestResourcePath("criminal-s11e03.dat");
        IMediaDetails media_details = HDHomeRunTagParser.fromFile(item.toFile());

        ShowLookup subject = new ShowLookup();
        subject.configure(config);
        subject.describe();
        IMediaDetails result = subject.process(media_details);

        Assert.assertEquals(18, result.getExtendedDetails().size());
        Assert.assertEquals("199", result.getExtendedDetails().getOrDefault("absoluteNumber", -1));
    }

    @Test
    public void showLookupWithIDHints() throws Exception
    {
        MediaToolConfig config =  Mockito.mock(MediaToolConfig.class);
        when(config.getShowLookup()).thenReturn(ImmutableMap.of(
                "key", "70FBF9A03F0D083D",
                "enabled", "true",
                "cache_key", "true",
                "cache_location", "/tmp/theTvdbKey.dat"
        ));
        when(config.getLookupHints()).thenReturn(ImmutableList.of(
                new LookupHint("S.W.A.T.", 328687, null, null)
        ));
        Path item = TestHelper.getTestResourcePath("swat-s01e02.dat");
        IMediaDetails media_details = HDHomeRunTagParser.fromFile(item.toFile());

        ShowLookup subject = new ShowLookup();
        subject.configure(config);
        IMediaDetails result = subject.process(media_details);

        org.junit.Assert.assertEquals("2", TokenParser.getMediaToken(media_details, "{absoluteNumber}"));
        org.junit.Assert.assertEquals("1", TokenParser.getMediaToken(media_details, "{airedSeason}"));
        org.junit.Assert.assertEquals("2", TokenParser.getMediaToken(media_details, "{dvdEpisodeNumber}"));
        org.junit.Assert.assertEquals("1", TokenParser.getMediaToken(media_details, "{dvdSeason}"));
        org.junit.Assert.assertEquals("Cuchillo", TokenParser.getMediaToken(media_details, "{episodeName}"));
        org.junit.Assert.assertEquals("2017-11-09", TokenParser.getMediaToken(media_details, "{firstAired}"));
        org.junit.Assert.assertEquals("328687", TokenParser.getMediaToken(media_details, "{id}"));
        org.junit.Assert.assertEquals("CBS", TokenParser.getMediaToken(media_details, "{network}"));
        org.junit.Assert.assertEquals("Continuing", TokenParser.getMediaToken(media_details, "{status}"));
    }


    @Test
    public void showLookupWithImDbTest() throws Exception
    {
        MediaToolConfig config =  Mockito.mock(MediaToolConfig.class);
        when(config.getShowLookup()).thenReturn(ImmutableMap.of(
                "key", "70FBF9A03F0D083D",
                "enabled", "true",
                "cache_key", "true",
                "cache_location", "/tmp/theTvdbKey.dat"
        ));
        when(config.getLookupHints()).thenReturn(ImmutableList.of(
                new LookupHint("Food Factory", 268860, "tt2558662", null)
        ));
        Path item = TestHelper.getTestResourcePath("food-s03e03.dat");
        IMediaDetails media_details = HDHomeRunTagParser.fromFile(item.toFile());

        ShowLookup subject = new ShowLookup();
        subject.configure(config);
        IMediaDetails result = subject.process(media_details);

        org.junit.Assert.assertNull(TokenParser.getMediaToken(media_details, "{absoluteNumber}"));
        org.junit.Assert.assertEquals("3", TokenParser.getMediaToken(media_details, "{airedSeason}"));
        org.junit.Assert.assertNull( TokenParser.getMediaToken(media_details, "{dvdEpisodeNumber}"));
        org.junit.Assert.assertNull( TokenParser.getMediaToken(media_details, "{dvdSeason}"));
        org.junit.Assert.assertEquals("1", TokenParser.getMediaToken(media_details, "{airedEpisodeNumber}"));
        org.junit.Assert.assertEquals("Smell The Coffee", TokenParser.getMediaToken(media_details, "{episodeName}"));
        org.junit.Assert.assertEquals("2014-01-11", TokenParser.getMediaToken(media_details, "{firstAired}"));
        org.junit.Assert.assertEquals("268860", TokenParser.getMediaToken(media_details, "{id}"));
        org.junit.Assert.assertEquals("Food Network Canada", TokenParser.getMediaToken(media_details, "{network}"));
        org.junit.Assert.assertEquals("Continuing", TokenParser.getMediaToken(media_details, "{status}"));
    }
}
