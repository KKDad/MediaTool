package org.westfield.thetvdb;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class theTvDBClientTest
{
    private static final String CACHE_LOCATION = "/tmp/theTvdbKey.dat";

    private theTvDBClient getSubject() {
        theTvDBClient subject = new theTvDBClient(true, CACHE_LOCATION, "70FBF9A03F0D083D");
        subject.login();
        return subject;
    }

    @Test
    public void badLoginTest()
    {
        try {

            theTvDBClient subject = new theTvDBClient(false, CACHE_LOCATION, "mykey");
            boolean result = subject.login();

            Assert.assertFalse(result);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void validLoginTest()
    {
        try {

            theTvDBClient subject = new theTvDBClient(false, CACHE_LOCATION, "70FBF9A03F0D083D");
            boolean result = subject.login();

            Assert.assertTrue(result);
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void searchCriminalMindsTestByName()
    {
        try {

            theTvDBClient subject = getSubject();
            SearchResponse result = subject.search("Criminal Minds", null, null);

            Assert.assertEquals(4, result.data.size());

        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    public void searchFoodFactoryByimdbId()
    {
        try {

            theTvDBClient subject = getSubject();
            SearchResponse result = subject.search("Food Factory", "tt2558662", null);

            Assert.assertEquals(1, result.data.size());

        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }


    @Test
    public void episodeCriminalMindsTest()
    {
        try {

            theTvDBClient subject = getSubject();
            EpisodeItem result = subject.searchEpisode(75710, 3 ,2, "In Name and Blood");

            Assert.assertNotNull(result);
            Assert.assertEquals("In Name and Blood", result.episodeName);
            Assert.assertEquals("2007-10-03", result.firstAired);

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
            EpisodeItem result = subject.searchEpisode(-2, 3 ,2, null);

            Assert.assertNull(result);

        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

    @Test
    public void seriesLookupTest()
    {
        try {

            theTvDBClient subject = getSubject();
            SeriesItem result = subject.seriesLookup(268860);

            Assert.assertNotNull(result);
            Assert.assertEquals("Food Factory (CA)", result.seriesName);
            Assert.assertEquals("Continuing", result.status);
            Assert.assertEquals("tt2558662", result.imdbId);
            Assert.assertEquals(3, result.genre.size());

        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }


}