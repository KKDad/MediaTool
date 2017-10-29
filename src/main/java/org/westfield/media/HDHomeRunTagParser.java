package org.westfield.media;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HDHomeRunTagParser
{
    @SuppressWarnings("squid:S00116")
    private class HDHomeRunTag
    {
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
    }

    private static final Logger logger = LoggerFactory.getLogger(HDHomeRunTag.class);


    public static IMediaDetails fromFile(File file)
    {
        Preconditions.checkNotNull(file);
        Preconditions.checkArgument(file.exists() && !file.isDirectory() && file.canRead());

        try {
            String json = extractHDHomeRunJson(file);
            if (json != null) {
                Gson gson = new Gson();
                HDHomeRunTag g = gson.fromJson(json, HDHomeRunTag.class);
                logger.info("Show: {}, Episode: {}, Title: {}", g.Title, g.EpisodeNumber, g.EpisodeTitle);
                if (g.RecordSuccess == 0)
                    logger.warn("Recording was not successful!");
                int season = Integer.parseInt(g.EpisodeNumber.substring(1,3));
                int episodeNumber = Integer.parseInt(g.EpisodeNumber.substring(4,6));
                return new MediaDetails(g.Title, season, g.EpisodeTitle, episodeNumber).setMediaFile(file);
            }
        }
        catch (NullPointerException | IllegalArgumentException ex)
        {
            logger.error("Unable to extract Recording {}", ex.getMessage(), ex);
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

            List<Byte> bytes = new ArrayList<>();
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
                    if (buffer[idx] > 240) {
                        int i  = 0;
                    }

                    bytes.add(buffer[idx]);
                    idx++;
                }

                while (idx < length && buffer[idx] != 0xFFFFFFFF);
                String result = new String(toPrimitive(bytes), Charsets.UTF_8);
                if (logger.isDebugEnabled()) {
                    logger.debug("Found: {}", result);
                    dumpDebug(toPrimitive(bytes));
                }
                return result;
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

    private static byte[] toPrimitive(List<Byte> Bytes)
    {
        byte[] bytes = new byte[Bytes.size()];

        int j=0;
        for(Byte b: Bytes)
            bytes[j++] = b;

        return bytes;
    }

    private static void dumpDebug(byte[] bytes)
    {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("/tmp/debug_dump.txt"))) {
            bos.write(bytes);
            bos.flush();
            bos.close();

        }
        catch (IOException e)
        {
            logger.error("Caught Exception: {}", e.getMessage(), e);
        }
    }

}