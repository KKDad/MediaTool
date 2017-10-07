package org.westfield.media;


import org.junit.*;
import org.westfield.TestHelper;

import java.nio.file.Path;

public class MediaInfoParserTest
{

    @Test
    public void ParserTest()
    {
        MediaInfoParser subject = new MediaInfoParser();
        Path item = TestHelper.getResourcePath("output.xml");

        boolean result = subject.parse(item);

        Assert.assertTrue(result);
    }

}