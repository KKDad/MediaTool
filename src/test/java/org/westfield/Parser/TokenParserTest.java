package org.westfield.Parser;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

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