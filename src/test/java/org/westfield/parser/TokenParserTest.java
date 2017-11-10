package org.westfield.parser;

import org.junit.Assert;
import org.junit.Test;

public class TokenParserTest
{
    @Test
    public void stripBracesTest() throws Exception
    {
        String result = TokenParser.stripBraces("{a}");
        Assert.assertEquals("a", result);
    }

    @Test
    public void antherStripBracesTest() throws Exception
    {
        String result = TokenParser.stripBraces("{network}");
        Assert.assertEquals("network", result);
    }

}