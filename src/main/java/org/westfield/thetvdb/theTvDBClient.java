package org.westfield.thetvdb;

import com.google.common.io.Files;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.http.ParameterBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// Documentation on theTvDB.com swagger API can be found here:
// https://api.thetvdb.com/swagger
@SuppressWarnings({ "squid:S00101", "squid:S2093"})
public class theTvDBClient
{
    private static final Logger logger = LoggerFactory.getLogger(theTvDBClient.class);
    private static final Gson json = new Gson();
    private static final long MILLIS_IN_ONE_DAY = 1000L*60*60*24;

    private static final String LOGIN_REQUEST_URL = "https://api.thetvdb.com/login";
    private static final String SEARCH_REQUEST_URL = "https://api.thetvdb.com/search/series?";
    private static final String SERIES_REQUEST_URL = "https://api.thetvdb.com/series/%d";
    private static final String EPISODE_REQUEST_URL = "https://api.thetvdb.com/series/%s/episodes/query?";
    private static final String MAKING_A_REQUEST_TO = "Making a request to: {}";
    private static final String RESULT = "Result: {}";
    private static final String FAILED_REQUEST = "Failed Request: {}";
    private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=UTF-8";
    private static final String NOT_AUTHORIZED = "Not authorized";

    private final boolean cacheKey;
    private final String cacheLocation;
    private final String apiKey;


    private String token;

    public theTvDBClient(boolean cacheKey, String cacheLocation, String apiKey) {
        this.cacheKey = cacheKey;
        this.cacheLocation = cacheLocation;
        this.apiKey = apiKey;
    }

    // Log in and get a JWT token
    public boolean login()
    {
        if (cacheKey && loadCachedKey())
            return true;

        AuthenticationRequest auth = new AuthenticationRequest();
        auth.apikey = apiKey;

        HttpURLConnection conn = null;
        try {
            logger.trace(MAKING_A_REQUEST_TO, LOGIN_REQUEST_URL);
            URL url = new URL(LOGIN_REQUEST_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", APPLICATION_JSON_CHARSET_UTF_8);
            conn.setRequestMethod("POST");
            // Post our Request
            OutputStream os = conn.getOutputStream();
            os.write(json.toJson(auth).getBytes(StandardCharsets.UTF_8.name()));
            os.close();

            int code = conn.getResponseCode();
            logger.trace("Login Request: {}", code);
            if (code == HttpURLConnection.HTTP_OK) {
                // Get Response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String result = IOUtils.toString(in, StandardCharsets.UTF_8.name());
                in.close();
                logger.debug(RESULT, result);
                saveCachedKey(result);

                AuthenticationResponse response = json.fromJson(result, AuthenticationResponse.class);
                this.token = response.token;
                return true;
            } else {
                // Get Error
                InputStream is = conn.getErrorStream();
                String result = IOUtils.toString(is, StandardCharsets.UTF_8.name());
                logger.info("Failed Request: {}", result);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return false;
    }

    private void saveCachedKey(String result) throws IOException
    {
        // Cache the key for reuse later  if requested.
        if (this.cacheKey) {
            File cacheFile = new File(this.cacheLocation);
            if (cacheFile.exists()) {
                if (!cacheFile.delete()) {
                    logger.warn("Cannot remove previous cached key! Caching disabled.");
                }
            } else
                Files.asCharSink(cacheFile, Charset.forName("UTF-8")).write(result);
        }
    }

    private boolean loadCachedKey()
    {
        long cacheKeyExpiryMillis = System.currentTimeMillis() - MILLIS_IN_ONE_DAY;
        try {
            File cacheFile = new File(this.cacheLocation);
            if (cacheFile.exists()) {
                if (cacheFile.lastModified() < cacheKeyExpiryMillis) {
                    logger.warn("Cached key has expired.");
                    return false;
                } else {
                    String result = Files.asCharSource(cacheFile, Charset.forName("UTF-8")).read();
                    AuthenticationResponse response = json.fromJson(result, AuthenticationResponse.class);
                    this.token = response.token;
                    logger.info("Using cached key.");
                    return true;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    private String makeRequest(String requestURL, Map<String, String> paramaters)
    {
        if (this.token == null && !this.login()) {
            logger.error("No token and Login Failed.");
            return null;
        }
        HttpURLConnection conn = null;
        try {
            String request = requestURL;
            if (paramaters != null)
                request = request + ParameterBuilder.build(paramaters);

            logger.trace(MAKING_A_REQUEST_TO, request);
            URL url = new URL(request);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestProperty("Accept", APPLICATION_JSON_CHARSET_UTF_8);
            conn.setRequestProperty("Authorization", "Bearer " + this.token);
            conn.setRequestMethod("GET");

            int code = conn.getResponseCode();
            logger.trace("Search Request: {}", code);
            if (code == HttpURLConnection.HTTP_OK) {
                // Get Response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String result = IOUtils.toString(in, StandardCharsets.UTF_8.name());
                in.close();

                logger.trace(RESULT, result);
                return result;
            } else {
                // Get Error
                InputStream is = conn.getErrorStream();
                String result = IOUtils.toString(is, StandardCharsets.UTF_8.name());
                logger.info(FAILED_REQUEST, result);

                // If we get a Not-Authorized exception, clear the token so the next time we'll try again
                if (result.contains(NOT_AUTHORIZED))
                    this.token = null;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    public SearchResponse search(String showName, String imdbId, String zap2itId)
    {
        // The API allows a search on one of three fields.
        Map<String, String> paramaters = new HashMap<>();
        if (imdbId != null && !imdbId.isEmpty())
            paramaters.put("imdbId", imdbId);
        else if (zap2itId != null && !zap2itId.isEmpty())
            paramaters.put("zap2itId", zap2itId);
        else {
            paramaters.put("name", showName);
        }
        String result = makeRequest(SEARCH_REQUEST_URL, paramaters);
        if (result == null)
            return null;

        SearchResponse original = json.fromJson(result, SearchResponse.class);

        // Take each of the results from the Search and do a Series Lookup to get the imdbId, zap2itId, genres, etc
        SearchResponse enhanced = new SearchResponse();
        for (SeriesItem i :original.data) {
            SeriesItem item = seriesLookup(i.id);
            if (item != null)
            enhanced.data.add(item);
        }
        // Return the Enhances search response
        return enhanced;
    }

    public SeriesItem seriesLookup(int seriesId) {
        String request = String.format(SERIES_REQUEST_URL, seriesId);
        String result = makeRequest(request, null);
        if (result == null)
            return null;

        SeriesResponse item = json.fromJson(result, SeriesResponse.class);
        return item == null ? null : item.data;
    }


    public EpisodeResponse searchEpisode(int showId, int season, int episode) {
        Map<String, String> paramaters = new HashMap<>();
        paramaters.put("airedSeason", Integer.toString(season));
        paramaters.put("airedEpisode", Integer.toString(episode));
        String request = String.format(EPISODE_REQUEST_URL, showId);
        String result = makeRequest(request, paramaters);
        if (result == null)
            return null;

        return json.fromJson(result, EpisodeResponse.class);
    }
}