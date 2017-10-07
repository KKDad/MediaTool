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
            Path item = TestHelper.getResourcePath("criminal-s11e03.dat");
            HDHomeRunTag subject = HDHomeRunTag.fromFile(item.toFile());

            Assert.assertNotNull(subject);
            Assert.assertEquals("S09E13", subject.EpisodeNumber);
            Assert.assertEquals("Criminal Minds", subject.Title);
        }

    @Test
    public void HDHomeRunTagParser_DiscoveryTest()
    {
        Path item = TestHelper.getResourcePath("discovery-s01e01.dat");
        HDHomeRunTag subject = HDHomeRunTag.fromFile(item.toFile());

        Assert.assertNotNull(subject);
        Assert.assertEquals("S01E01", subject.EpisodeNumber);
        Assert.assertEquals("Star Trek: Discovery", subject.Title);
    }

    @Test
    public void HDHomeRunTagParser_JulianTest()
    {
        Path item = TestHelper.getResourcePath("julian-s02e12.dat");
        HDHomeRunTag subject = HDHomeRunTag.fromFile(item.toFile());

        Assert.assertNotNull(subject);
        Assert.assertEquals("S02E12", subject.EpisodeNumber);
        Assert.assertEquals("Roi Julian!", subject.Title);
    }

    @Test
    public void HDHomeRunTagParser_YouTest()
    {
        Path item = TestHelper.getResourcePath("you-s01e02.dat");
        HDHomeRunTag subject = HDHomeRunTag.fromFile(item.toFile());

        Assert.assertNotNull(subject);
        Assert.assertEquals("S01E02", subject.EpisodeNumber);
        Assert.assertEquals("You Gotta Eat Here!", subject.Title);
    }
}
