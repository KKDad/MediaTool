package org.westfield.media;


import org.junit.*;
import org.westfield.TestHelper;

import java.nio.file.Path;

public class MediaInfoParserTest
{

    @Test
    public void MediaInfoParserGetTest()
    {
        Path item = TestHelper.getTestResourcePath("grizzy_et_les_lemmings.xml");
        MediaInfoParser subject = MediaInfoParser.get(item);


        Assert.assertNotNull(subject);
    }

}