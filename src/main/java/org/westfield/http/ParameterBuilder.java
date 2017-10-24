package org.westfield.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class ParameterBuilder
{

    private ParameterBuilder() {}

    public static String build(Map<String, String> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();

        for (Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String> entry = iterator.next();
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            if (iterator.hasNext())
                result.append("&");
        }

        return result.toString();
    }
}