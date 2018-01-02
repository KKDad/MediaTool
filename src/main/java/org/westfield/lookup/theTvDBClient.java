package org.westfield.lookup;

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
import java.util.List;
import java.util.Map;

// Documentation on theTvDB.com swagger API can be found here:
// https://api.thetvdb.com/swagger

@SuppressWarnings({ "squid:S00101", "squid:ClassVariableVisibilityCheck", "squid:S2093"})
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

    // Gson serializer and deserializer helper classes
    protected class AuthRequest
    {
        String apikey;
    }
    protected class AuthResponse
    {
        String token;
    }
    public class SeriesItem
    {
        public List<String> aliases;
        public String banner;
        public String firstAired;
        public int id;
        public String network;
        public String overview;
        public String seriesName;
        public String status;
        // Added via a call to https://api.thetvdb.com/swagger#!/Series/get_series_id
        public List<String> genre;
        public String imdbId;
        public String zap2itId;

        @Override
        public String toString()
        {
            return String.format("SeriesItem{id=%d, seriesName='%s', status='%s', imdbId='%s', zap2itId='%s'}", id, seriesName, status, imdbId, zap2itId);
        }
    }
    public class SearchResponse
    {
        public List<SeriesItem> data;
    }
    public class SeriesResponse
    {
        public SeriesItem data;
    }
    public class EpisodeItem
    {
        public String absoluteNumber;
        public String airedEpisodeNumber;
        public String airedSeason;
        public String dvdEpisodeNumber;
        public String dvdSeason;
        public String episodeName;
        public String firstAired;
        public String id;
        public String lastUpdated;
        public String overview;

        @Override
        public String toString()
        {
            return String.format("EpisodeItem{airedEpisodeNumber='%s', airedSeason='%s', episodeName='%s'}", airedEpisodeNumber, airedSeason, episodeName);
        }
    }
    public class EpisodeResponse
    {
        public List<EpisodeItem> data;
    }

    // Log in and get a JWT token
    public boolean login()
    {
        if (cacheKey && loadCachedKey())
            return true;

        AuthRequest auth = new AuthRequest();
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

                AuthResponse response = json.fromJson(result, AuthResponse.class);
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

    private boolean loadCachedKey() {
        long cacheKeyExpiryMillis = System.currentTimeMillis() - MILLIS_IN_ONE_DAY;
        try {
            File cacheFile = new File(this.cacheLocation);
            if (cacheFile.exists()) {
                if (cacheFile.lastModified() < cacheKeyExpiryMillis) {
                    logger.warn("Cached key has expired.");
                    return false;
                } else {
                    String result = Files.asCharSource(cacheFile, Charset.forName("UTF-8")).read();
                    AuthResponse response = json.fromJson(result, AuthResponse.class);
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


    public SearchResponse search(String showName, String imdbId, String zap2itId)
    {
        if (this.token == null && !this.login()) {
            logger.error("No token and Login Failed.");
            return null;
        }

        HttpURLConnection conn = null;
        try {
            // The API allows a search on one of three fields.
            Map<String, String> map = new HashMap<>();
            if (imdbId != null && !imdbId.isEmpty())
                map.put("imdbId", imdbId);
            else if (zap2itId != null && !zap2itId.isEmpty())
                map.put("zap2itId", zap2itId);
            else {
                map.put("name", showName);
            }
            String request = SEARCH_REQUEST_URL + ParameterBuilder.build(map);

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
                SearchResponse item = json.fromJson(result, SearchResponse.class);
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

    public SeriesItem seriesLookup(int seriesId)
    {
        if (this.token == null && !this.login()) {
            logger.error("No token and Login Failed.");
            return null;
        }

        HttpURLConnection conn = null;
        try {
            String request = String.format(SERIES_REQUEST_URL, seriesId);

            logger.trace(MAKING_A_REQUEST_TO, request);
            URL url = new URL(request);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestProperty("Accept", APPLICATION_JSON_CHARSET_UTF_8);
            conn.setRequestProperty("Authorization", "Bearer " + this.token);
            conn.setRequestMethod("GET");

            int code = conn.getResponseCode();
            logger.trace("Series Request: {}", code);
            if (code == HttpURLConnection.HTTP_OK) {
                // Get Response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String result = IOUtils.toString(in, StandardCharsets.UTF_8.name());
                in.close();

                logger.trace(RESULT, result);
                SeriesResponse item = json.fromJson(result, SeriesResponse.class);
                if (item != null)
                    return item.data;
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


    public EpisodeResponse searchEpisode(int showId, int season, int episode)
    {
        if (this.token == null && !this.login()) {
            logger.error("No token and Login Failed.");
            return null;
        }

        HttpURLConnection conn = null;
        try {
            Map<String, String> map = new HashMap<>();
            map.put("airedSeason", Integer.toString(season));
            map.put("airedEpisode", Integer.toString(episode));
            String request = String.format(EPISODE_REQUEST_URL, showId) + ParameterBuilder.build(map);

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
                return json.fromJson(result, EpisodeResponse.class);
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
}
