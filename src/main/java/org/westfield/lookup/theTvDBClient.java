package org.westfield.lookup;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@SuppressWarnings("squid:S00101")
public class theTvDBClient
{
    private static final Logger logger = LoggerFactory.getLogger(theTvDBClient.class);
    private static final Gson json = new Gson();

    private static final String LOGIN_REQUEST_URL = "https://api.thetvdb.com/login";

    // Gson serializer and deserializer helper classes
    protected class AuthRequest
    {
        String apikey;
    }
    protected class AuthResponse
    {
        String token;
    }

    private String token;
    private LocalDate tokenExpiry;


    // Log in and get a JWT token
    public boolean login(String apiKey)
    {
        AuthRequest auth = new AuthRequest();
        auth.apikey = apiKey;

        HttpURLConnection conn = null;
        try {
            logger.trace("Making a request to: {}", LOGIN_REQUEST_URL);
            URL url = new URL(LOGIN_REQUEST_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
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

                logger.debug("Result: {}", result);
                AuthResponse response = json.fromJson(result, AuthResponse.class);
                this.token = response.token;
                this.tokenExpiry = LocalDate.now().plusDays(1);
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
}
