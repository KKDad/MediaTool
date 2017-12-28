package org.westfield.lookup;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class theTvDBClientTest
{
    private static final String CACHE_LOCATION = "/tmp/theTvdbKey.dat";

    @Test
    public void badLoginTest()
    {
        try {

            theTvDBClient subject = new theTvDBClient(false, CACHE_LOCATION);
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

            theTvDBClient subject = new theTvDBClient(false, CACHE_LOCATION);
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

            theTvDBClient subject = getSubject();
            theTvDBClient.SearchResponse result = subject.search("Criminal Minds");

            Assert.assertEquals(4, result.data.size());

        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

    private theTvDBClient getSubject() {
        theTvDBClient subject = new theTvDBClient(true, CACHE_LOCATION);
        subject.login("70FBF9A03F0D083D");
        return subject;
    }


    @Test
    public void episodeCriminalMindsTest()
    {
        try {

            theTvDBClient subject = getSubject();
            theTvDBClient.EpisodeResponse result = subject.searchEpisode(75710, 3 ,2);

            Assert.assertEquals(1, result.data.size());
            Assert.assertEquals("In Name and Blood", result.data.get(0).episodeName);
            Assert.assertEquals("2007-10-03", result.data.get(0).firstAired);

        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    public void episodeUnknownShowTest()
    {
        try {

            theTvDBClient subject = getSubject();
            theTvDBClient.EpisodeResponse result = subject.searchEpisode(-2, 3 ,2);

            Assert.assertEquals(null, result);

        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }
}