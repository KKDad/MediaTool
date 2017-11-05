package org.westfield.lookup;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class theTvDBClientTest
{

    @Test
    public void badLoginTest()
    {
        try {

            theTvDBClient subject = new theTvDBClient();
            boolean result = subject.login("mykey");

            Assert.assertFalse(result);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void validLoginTest()
    {
        try {

            theTvDBClient subject = new theTvDBClient();
            boolean result = subject.login("70FBF9A03F0D083D");

            Assert.assertTrue(result);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void searchCriminalMindsTest()
    {
        try {

            theTvDBClient subject = new theTvDBClient();
            subject.login("70FBF9A03F0D083D");

            theTvDBClient.SearchResponse result = subject.search("Criminal Minds");

            Assert.assertEquals(4, result.data.size());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

}