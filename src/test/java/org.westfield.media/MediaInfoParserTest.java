package org.westfield.media;


import org.junit.Test;

public class MediaInfoParserTest
{

    @Test
    public void ParserTest()
    {
        MediaInfoParser subject = new MediaInfoParser();
        boolean result = subject.parse("output.xml");

        assert(result);

    }

}