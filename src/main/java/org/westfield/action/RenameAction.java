package org.westfield.action;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.media.IMediaDetails;
import org.westfield.media.MediaDetails;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RenameAction implements IAction
{
    private static final Logger logger = LoggerFactory.getLogger(RenameAction.class);

    private String formatString;
    private List<String> tokens;

    private final char START_TOKEN = '{';
    private final char END_TOKEN = '}';
    private final String SEPARATORS = "/\\.";

    @Override
    public boolean configure(Map<String, String> config)
    {
        try {
            this.formatString = config.get("format");
            this.tokens = parseTokens(this.formatString);
            return true;
        } catch (ParseException pe) {
            logger.error(pe.getMessage());
        }
        return false;
    }

    private List<String> parseTokens(String formatString) throws ParseException
    {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        StringBuilder token = new StringBuilder();
        boolean inToken = false;
        do {
            char ch = formatString.charAt(i);
            if (ch == START_TOKEN) {
                if (inToken)
                    throw new ParseException(formatString, i);
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token = new StringBuilder();
                }
                token.append(ch);
               inToken = true;
            }
            else if (ch == END_TOKEN) {
                if (!inToken)
                    throw new ParseException(formatString, i);
                token.append(ch);
                tokens.add(token.toString());
                token = new StringBuilder();
                inToken = false;
            }
            else if (SEPARATORS.indexOf(ch)> -1) {
                token.append(ch);
                tokens.add(token.toString());
                token = new StringBuilder();
            } else {
                token.append(ch);
            }
            i++;
        } while(i < formatString.length());
        return tokens;
    }

    @Override
    public IMediaDetails process(IMediaDetails details) {
        try {
            StringBuilder destFileName = new StringBuilder();
            for (String token : this.tokens) {
                destFileName.append(getMediaToken(details, token));
            }
            if (logger.isDebugEnabled())
                logger.debug("Generated new name: {}", destFileName.toString());


            MediaDetails md = new MediaDetails(details.getShow(), details.getSeason(), details.getEpisodeTitle(), details.getEpisodeNumber());
            md.setMediaFile(new File(destFileName.toString()));
            return md;
        } catch (UnknownTokenException u) {
            logger.error("Unknown Token {} in format string: {}", u.getToken(), this.formatString);
        }
        return null;
    }

    private String getMediaToken(IMediaDetails details, String token) throws UnknownTokenException
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
                return details.getEpisodeTitle();

            case "{Format}":
                String ext = FilenameUtils.getExtension(details.getMediaFile().getName());
                return ext;

            default:
                if (token.charAt(0) == START_TOKEN)
                    throw new UnknownTokenException(token);
                return token;
        }
    }
}