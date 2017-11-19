package org.westfield.media;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class FFProbeWrapperTest
{

    @Test
    public void probeTest() throws Exception
    {
        File f = new File("/data/git/pitchin-s04s10.mpg");
        FFProbeWrapper.ShowFormat result = FFProbeWrapper.probe(f);

        Assert.assertNotNull(result);
        Assert.assertEquals(new Integer(4), result.format.nb_streams);
        Assert.assertEquals(new Integer(1), result.format.nb_programs);
        Assert.assertEquals("/data/git/pitchin-s04s10.mpg", result.format.filename);
    }
}