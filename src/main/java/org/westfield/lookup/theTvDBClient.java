package org.westfield.lookup;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.http.ParameterBuilder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "squid:S00101", "squid:ClassVariableVisibilityCheck", "squid:S2093"})
public class theTvDBClient
{
    private static final Logger logger = LoggerFactory.getLogger(theTvDBClient.class);
    private static final Gson json = new Gson();

    private static final String LOGIN_REQUEST_URL = "https://api.thetvdb.com/login";
    private static final String SEARCH_REQUEST_URL = "https://api.thetvdb.com/search/series?";
    private static final String EPISODE_REQUEST_URL = "https://api.thetvdb.com/series/%s/episodes/query?";
    private static final String MAKING_A_REQUEST_TO = "Making a request to: {}";
    private static final String RESULT = "Result: {}";
    private static final String FAILED_REQUEST = "Failed Request: {}";
    private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=UTF-8";

    private String token;

    // Gson serializer and deserializer helper classes
    protected class AuthRequest
    {
        String apikey;
    }
    protected class AuthResponse
    {
        String token;
    }
    public class SearchItem
    {
        public List<String> aliases;
        public String banner;
        public String firstAired;
        public int id;
        public String network;
        public String overview;
        public String seriesName;
        public String status;

        @Override
        public String toString()
        {
            return String.format("SearchItem{id=%d, seriesName='%s', status='%s'}", id, seriesName, status);
        }
    }
    public class SearchResponse
    {
        public List<SearchItem> data;
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
    public boolean login(String apiKey)
    {
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


    public SearchResponse search(String showName)
    {
        if (this.token == null) {
            logger.error("No token, please log in.");
            return null;
        }

        HttpURLConnection conn = null;
        try {
            Map<String, String> map = new HashMap<>();
            map.put("name", showName);
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
                return json.fromJson(result, SearchResponse.class);
            } else {
                // Get Error
                InputStream is = conn.getErrorStream();
                String result = IOUtils.toString(is, StandardCharsets.UTF_8.name());
                logger.info(FAILED_REQUEST, result);
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
        if (this.token == null) {
            logger.error("No token, please log in.");
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
