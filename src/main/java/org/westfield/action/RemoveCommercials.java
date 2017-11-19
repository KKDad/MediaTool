package org.westfield.action;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveCommercials implements IAction
{
    private static final List<String> DEFAULT_CUTLIST = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(RemoveCommercials.class);
    static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.####");

    private boolean enabled;
    private String ffmpeg;
    private String tmpDir;

    // We expect strings like this:
    //     JumpSegment("From=628.6280","To=794.2935")
    // And we want to pull out the from and to double values
    private static final Pattern pattern = Pattern.compile("From=(\\d*.\\d*).*To=(\\d*.\\d*)");

    private class Chapter {
        int chapterNumber;
        float start;
        float length;

        Chapter(int chapter, float start, float length) {
            this.chapterNumber = chapter;
            this.start = start;
            this.length = length;

        }

        @Override
        public String toString() {
            if (length > 0)
                return String.format("Chapter %d: start=%s, length=%s", this.chapterNumber, formatTime(start), formatTime(length));
            else
                return String.format("Chapter %d: start=%s to end of file", this.chapterNumber, formatTime(start));
        }
    }


    @Override
    public boolean configure(MediaToolConfig config) {
        try {
            this.enabled = Boolean.parseBoolean(config.getRemoveCommercials().get("enabled"));
            this.ffmpeg = config.getRemoveCommercials().get("ffmpeg");
            this.tmpDir = java.nio.file.Files.createTempDirectory("media-").toString();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
        logger.debug("RemoveCommercials is {}", this.enabled ? "Enabled" : "Disabled");
        return true;
    }

    @Override
    public IMediaDetails process(IMediaDetails details) {
        if (!this.enabled)
            return details;

        @SuppressWarnings("unchecked")
        List<String> cutList = (List<String>)details.getExtendedDetails().getOrDefault("CutList", DEFAULT_CUTLIST);
        if (cutList.isEmpty()) {
            logger.warn("No commercials to remove or commercial detection disabled.");
            return details;
        }

        List<Chapter> chapters = getChapterLists(cutList);
        if (chapters.isEmpty())
            return null;

        logger.info("----");
        for(Chapter item: chapters)
            logger.info("{}", item);

        for(Chapter item: chapters)
            if (extractChapter(item, details.getMediaFile().getAbsolutePath()) != 0)
                return null;

        combineChapters(chapters, details.getMediaFile().getAbsolutePath());

        return null;
    }

    private int combineChapters(List<Chapter> chapters, String fileName)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("concat:");
        for(Chapter chapter: chapters)
            sb.append(chapterFilename(fileName, chapter.chapterNumber)).append("|");
        sb.setLength(sb.length() - 1);

        String cmd = String.format("%s -i %s -c copy %s", this.ffmpeg, sb.toString(), finalFilename(fileName));
        logger.info("{}", cmd);
        try {
            Process process = new ProcessBuilder(cmd.split("\\s+")).start();
            int rc = process.waitFor();
            if (rc != 0)
                logger.warn("Command exited with code: {}", rc);
            return rc;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return -1;
    }


    private int extractChapter(Chapter chapter, String fileName)
    {
        String cmd;
        if (chapter.length > 0)
            cmd = String.format("%s -hide_banner -loglevel fatal -i %s -ss %s -t %s %s -c copy -y %s", this.ffmpeg, fileName, DECIMAL_FORMAT.format(chapter.start), DECIMAL_FORMAT.format(chapter.length), generateMap(3), chapterFilename(fileName, chapter.chapterNumber));
        else
            cmd = String.format("%s -hide_banner -loglevel fatal -i %s -ss %s %s -c copy -y %s", this.ffmpeg, fileName, DECIMAL_FORMAT.format(chapter.start), generateMap(3), chapterFilename(fileName, chapter.chapterNumber));
        logger.info("{}", cmd);
        try {
            Process process = new ProcessBuilder(cmd.split("\\s+")).start();
            int rc = process.waitFor();
            if (rc != 0)
                logger.warn("Command exited with code: {}", rc);
            return rc;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return -1;
    }


    private String generateMap(int numberOfStreams)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= numberOfStreams; i++)
            sb.append("-map 0:)").append(i);
        return sb.toString();
    }

    String chapterFilename(String fileName, int chapterNumber)
    {
        return Paths.get(this.tmpDir, String.format("%s-Chapter%s.%s", Files.getNameWithoutExtension(fileName), Integer.toString(chapterNumber), Files.getFileExtension(fileName))).toString();
    }

    String finalFilename(String fileName)
    {
        return Paths.get(this.tmpDir, String.format("%s-new.%s", Files.getNameWithoutExtension(fileName), Files.getFileExtension(fileName))).toString();
    }

    private List<Chapter> getChapterLists(List<String> cutList)
    {
        List<Chapter> chapters = new ArrayList<>();
        int chapter = 1;
        float previousEnds = 0.0f;
        for (String item : cutList) {
            Matcher m = pattern.matcher(item);
            if (m.find()) {
                float commercialStart = Float.parseFloat(m.group(1));
                float commercialEnds =  Float.parseFloat(m.group(2));
                if (logger.isTraceEnabled())
                    logger.trace("Commercial detected from {} to {}, length {} seconds", formatTime(commercialStart), formatTime(commercialEnds), formatTime(commercialEnds - commercialStart));
                float chapterLength = commercialStart-previousEnds;
                if (chapterLength > 0)
                    chapters.add(new Chapter(chapter++, previousEnds, chapterLength));
                previousEnds = commercialEnds;
            } else {
                logger.error("Malformed JumpSegment: {}", item);
                return new ArrayList<>();
            }
        }
        chapters.add(new Chapter(chapter, previousEnds, -1));
        return chapters;
    }


    private String formatTime(float totalSecs)
    {
        int hours = (int)(totalSecs / 3600);
        int minutes = (int)((totalSecs % 3600) / 60);
        int seconds = (int)totalSecs % 60;

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else if (minutes > 0)
            return String.format("%d:%02d", minutes, seconds);
        return String.format("%d", seconds);
    }
}
