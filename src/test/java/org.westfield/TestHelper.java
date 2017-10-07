package org.westfield;


import org.junit.Assert;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestHelper
{
    private TestHelper() {}


    /**
     * Generates the expected path to a given test resource and verifies that is exists
     * @param resourceName Name of the resource to find
     * @return Fully Qualified path to the resource
     */
    public static Path getResourcePath(String resourceName)
    {
        Path resource = Paths.get(System.getProperty("user.dir"), "build/resources/test", resourceName);
        File f = resource.toFile();
        if (!f.exists()) {
            resource = Paths.get(System.getProperty("user.dir"), "out/test/resources", resourceName);
            f = resource.toFile();
        }
        Assert.assertTrue(String.format("Cannot locate expected resource %s", resourceName), f.exists());
        return resource;
    }
}
