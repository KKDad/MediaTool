package org.westfield.parser;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.media.IMediaDetails;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TokenParser
{
    private static final char START_TOKEN = '{';
    private static final char END_TOKEN = '}';
    private static final String SEPARATORS = "/\\.";

    private static final Logger logger = LoggerFactory.getLogger(TokenParser.class);

    private TokenParser() {}

    public static List<String> parseTokens(String formatString) throws ParseException
    {
        List<String> collectedTokens = new ArrayList<>();
        int i = 0;
        StringBuilder token = new StringBuilder();
        boolean inToken = false;
        do {
            char ch = formatString.charAt(i);
            if (ch == START_TOKEN) {
                if (inToken)
                    throw new ParseException(formatString, i);
                if (token.length() > 0) {
                    collectedTokens.add(token.toString());
                    token = new StringBuilder();
                }
                token.append(ch);
                inToken = true;
            }
            else if (ch == END_TOKEN) {
                if (!inToken)
                    throw new ParseException(formatString, i);
                token.append(ch);
                collectedTokens.add(token.toString());
                token = new StringBuilder();
                inToken = false;
            }
            else if (SEPARATORS.indexOf(ch)> -1) {
                token.append(ch);
                collectedTokens.add(token.toString());
                token = new StringBuilder();
            } else {
                token.append(ch);
            }
            i++;
        } while(i < formatString.length());
        return collectedTokens;
    }

    public static String getMediaToken(IMediaDetails details, String token) throws UnknownTokenException
    {
        switch (token) {
            case "/":
            case "-":
            case ".":
                return token;

            case "{Show}":
                return details.getShow();

            case "{Season}":
                return String.format("%02d", details.getSeason());

            case "{Episode}":
                return String.format("%02d", details.getEpisodeNumber());

            case "{Title}":
                String title = details.getEpisodeTitle();
                if (title == null || title.length() == 0) {
                    Object result = details.getExtendedDetails().getOrDefault("episodeName", null);
                    if (result instanceof ArrayList) {
                        logger.error("result: {}", result);
                        return (String)((ArrayList) result).get(0);
                    } else
                        title = (String)result;
                }
                return title;

            case "{Format}":
                return FilenameUtils.getExtension(details.getMediaFile().getName());

            case "{absoluteNumber}":
            case "{airedEpisodeNumber}":
            case "{airedSeason}":
            case "{dvdEpisodeNumber}":
            case "{dvdSeason}":
            case "{episodeName}":
            case "{firstAired}":
            case "{episodeOverview}":
            case "{banner}":
            case "{id}":
            case "{network}":
            case "{showOverview}":
            case "{status}":

                return (String)details.getExtendedDetails().getOrDefault(stripBraces(token), null);

            default:
                if (token.charAt(0) == START_TOKEN)
                    throw new UnknownTokenException(token);
                return token;
        }
    }

    public static String stripBraces(String token)
    {
        return token.substring(1, token.length() - 1);
    }

    public static boolean hasEpisodeTitle(IMediaDetails details)
    {
        try {
            String title = getMediaToken(details, "{Title}");
            return title != null && !title.isEmpty();
        }  catch (UnknownTokenException e) {
            return false;
        }


    }
}
