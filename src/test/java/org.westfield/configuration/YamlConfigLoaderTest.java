package org.westfield.configuration;

import org.junit.Assert;
import org.junit.Test;
import org.westfield.TestHelper;

import java.nio.file.Path;

public class YamlConfigLoaderTest
{
    @Test
    public void YamlConfigLoaderParserTest()
    {
        Path item = TestHelper.getTestResourcePath("TestMediaTool.yaml");
        MediaToolConfig subject = YamlConfigLoader.get(item);

        Assert.assertNotNull(subject);
    }
}
