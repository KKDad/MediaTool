package org.westfield.media;

import com.google.common.base.Preconditions;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

@SuppressWarnings("squid:S00116")
public class HDHomeRunTag implements IMediaDetails
{
    private static final Logger logger = LoggerFactory.getLogger(HDHomeRunTag.class);


    String Category;
    String ChannelImageURL;
    String ChannelName;
    long EndTime;
    String EpisodeNumber;
    String EpisodeTitle;
    String ImageURL;
    long OriginalAirdate;
    String ProgramID;
    String RecordEndTime;
    String RecordStartTime;
    int RecordSuccess;
    String SeriesID;
    long StartTime;
    String Title;


    public static HDHomeRunTag fromFile(File file)
    {
        Preconditions.checkNotNull(file);
        Preconditions.checkArgument(file.exists() && !file.isDirectory() && file.canRead());

        String json = extractHDHomeRunJson(file);
        if (json != null) {
            Gson gson = new Gson();
            HDHomeRunTag g = gson.fromJson(json, HDHomeRunTag.class);
            logger.info("Show: {}, Episode: {}, Title: {}", g.Title, g.EpisodeNumber, g.EpisodeTitle);
            if (g.RecordSuccess == 0)
                logger.warn("Recording was not successful!");
            return g;
        }

        return null;
    }

    /**
     * Attempt to parse a HDHomeRun json string out of the file
     * @param file File to parse
     * @return Located json string
     */
    private static String extractHDHomeRunJson(File file) {
        try {

            StringBuilder sb = new StringBuilder();
            try (InputStream is = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int length = is.read(buffer);
                if (!isHDHomeRunStartTag(buffer)) {
                    logger.warn("Magic Header not detected on file: {}", file.getName());
                    return null;
                }
                int idx = 4;
                do {
                    if (isHDHomeRunContinueTag(buffer, idx))
                        idx += 4;
                    sb.append((char)buffer[idx]);
                    idx++;
                }
                while (idx < length && buffer[idx] != 0xFFFFFFFF);
                if (logger.isDebugEnabled())
                    logger.debug("Found: {}", sb.toString());
                return sb.toString();
            }
        } catch (IOException e) {
            logger.error("Caught Exception: {}", e.getMessage(), e);
        }
        return null;
    }

    private static final int[] startTag = { 0x47, 0x5F, 0xFFFFFFFA };
    private static boolean isHDHomeRunStartTag(byte[] buffer)
    {
        if (logger.isDebugEnabled())
            logger.debug("Magic Actual: {}, {}, {}", Integer.toHexString(buffer[0]), Integer.toHexString(buffer[1]), Integer.toHexString(buffer[2]));
        return (buffer[0] == startTag[0] && buffer[1] == startTag[1] && buffer[2] == startTag[2]);
    }

    private static final int[] continueTag = {0x47, 0x1F, 0xFFFFFFFA };
    private static boolean isHDHomeRunContinueTag(byte[] buffer, int idx)
    {
        return (buffer[idx] == continueTag[0] && buffer[idx + 1] == continueTag[1] && buffer[idx + 2] == continueTag[2]);
    }

    // IMediaDetails Implementation

    @Override
    public String getTitle() {
        return this.Title;
    }

    @Override
    public String getEpisodeNumber() {
        return this.EpisodeNumber;
    }

    @Override
    public String getEpisodeTitle() {
        return this.EpisodeTitle;
    }
}