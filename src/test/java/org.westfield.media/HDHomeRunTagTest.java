package org.westfield.media;

import org.junit.Assert;
import org.junit.Test;
import org.westfield.TestHelper;

import java.nio.file.Path;

public class HDHomeRunTagTest
{
    @Test
    public void HDHomeRunTagParser_CriminalMinesTest()
    {
        Path item = TestHelper.getTestResourcePath("criminal-s11e03.dat");
        IMediaDetails subject = HDHomeRunTagParser.fromFile(item.toFile());

        Assert.assertNotNull(subject);
        Assert.assertEquals(9, subject.getSeason());
        Assert.assertEquals(13, subject.getEpisodeNumber());
        Assert.assertEquals("Criminal Minds", subject.getShow());
        Assert.assertEquals("The Road Home", subject.getEpisodeTitle());
    }

    @Test
    public void HDHomeRunTagParser_DiscoveryTest()
    {
        Path item = TestHelper.getTestResourcePath("discovery-s01e01.dat");
        IMediaDetails subject = HDHomeRunTagParser.fromFile(item.toFile());

        Assert.assertNotNull(subject);
        Assert.assertEquals(1, subject.getSeason());
        Assert.assertEquals(1, subject.getEpisodeNumber());
        Assert.assertEquals("Star Trek: Discovery", subject.getShow());
        Assert.assertEquals("The Vulcan Hello", subject.getEpisodeTitle());
    }

    @Test
    public void HDHomeRunTagParser_JulianTest()
    {
        Path item = TestHelper.getTestResourcePath("julian-s02e12.dat");
        IMediaDetails subject = HDHomeRunTagParser.fromFile(item.toFile());

        Assert.assertNotNull(subject);
        Assert.assertEquals(2, subject.getSeason());
        Assert.assertEquals(12, subject.getEpisodeNumber());
        Assert.assertEquals("Roi Julian!", subject.getShow());
        Assert.assertEquals("Momo et Julian", subject.getEpisodeTitle());
    }

    @Test
    public void HDHomeRunTagParser_YouTest()
    {
        Path item = TestHelper.getTestResourcePath("you-s01e02.dat");
        IMediaDetails subject = HDHomeRunTagParser.fromFile(item.toFile());

        Assert.assertNotNull(subject);
        Assert.assertEquals(1, subject.getSeason());
        Assert.assertEquals(2, subject.getEpisodeNumber());
        Assert.assertEquals("You Gotta Eat Here!", subject.getShow());
        Assert.assertEquals("Pizzeria Napoletana; Floyd's Diner; Haugen's Chicken & Ribs", subject.getEpisodeTitle());
    }

    @Test
    public void HDHomeRunTagParser_JungleTest()
    {
        Path item = TestHelper.getTestResourcePath("jungle-s01e53.dat");
        IMediaDetails subject = HDHomeRunTagParser.fromFile(item.toFile());

        Assert.assertNotNull(subject);
        Assert.assertEquals(1, subject.getSeason());
        Assert.assertEquals(53, subject.getEpisodeNumber());
        Assert.assertEquals("Les as de la jungle à la rescousse", subject.getShow());
        Assert.assertEquals("Drôle d'oiseau", subject.getEpisodeTitle());
    }
}
