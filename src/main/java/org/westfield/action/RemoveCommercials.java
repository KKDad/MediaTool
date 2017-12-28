package org.westfield.action;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.ProcessingLoggingHandler;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.media.IMediaDetails;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
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

    class Chapter {
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
    public void describe()
    {
        logger.warn("RemoveCommercials will use ffmpeg to remove commercials from the media. Required CommercialDetect.");
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

        boolean success = combineChapters(chapters, details.getMediaFile().getAbsolutePath());
        if (success) {
            move();
            cleanup();
        }

        return details;
    }


    /**
     * Remove all temporary files created while processing the media file
     */
    private void cleanup()
    {
        // Paranoia, never erase the root directory!
        if (this.tmpDir.isEmpty() || this.tmpDir.equals("/"))
            return;

        try {
            Path rootPath = Paths.get(this.tmpDir);
            java.nio.file.Files.walk(rootPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
        }
    }

    private void move()
    {
        throw new UnsupportedOperationException();
    }

    private boolean combineChapters(List<Chapter> chapters, String fileName)
    {
        StringBuilder concat = new StringBuilder();
        concat.append("concat:");
        for(Chapter chapter: chapters)
            concat.append(chapterFilename(fileName, chapter.chapterNumber)).append("|");
        concat.setLength(concat.length() - 1);

        try {
            logger.info("Combining Chapters...");
            Process process = new ProcessBuilder(this.ffmpeg, "-hide_banner", "-i", concat.toString(), "-c", "copy", "-y", finalFilename(fileName)).start();
            ProcessingLoggingHandler outputHandler = new ProcessingLoggingHandler(process.getInputStream(), logger, getFilterList());
            ProcessingLoggingHandler errorHandler = new ProcessingLoggingHandler(process.getErrorStream(), logger, getFilterList());
            outputHandler.start();
            errorHandler.start();
            int rc = process.waitFor();
            if (rc != 0)
                logger.warn("Command exited with code: {}", rc);
            return rc == 0;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return false;
    }


    /**
     * Extract the chapter (Video and all Audio streams) from the Media file using ffmpeg
     * @param chapter - Chapter to extract. Chapter specified the start postion in seconds and the length of the clip
     * @param fileName - Media file to extract from
     * @return  Non-zero on failure, zero for success
     */
    private int extractChapter(Chapter chapter, String fileName)
    {
        List<String> mpeg = getFilterList();
        String cmd;
        if (chapter.length > 0)
            cmd = String.format("%s -hide_banner -loglevel error -i %s -ss %s -t %s %s -c copy -y %s", this.ffmpeg, fileName, DECIMAL_FORMAT.format(chapter.start), DECIMAL_FORMAT.format(chapter.length), generateMap(3), chapterFilename(fileName, chapter.chapterNumber));
        else
            cmd = String.format("%s -hide_banner -loglevel error -i %s -ss %s %s -c copy -y %s", this.ffmpeg, fileName, DECIMAL_FORMAT.format(chapter.start), generateMap(3), chapterFilename(fileName, chapter.chapterNumber));
        logger.info("{}", cmd);
        try {
            Process process = new ProcessBuilder(cmd.split("\\s+")).start();
            ProcessingLoggingHandler outputHandler = new ProcessingLoggingHandler(process.getInputStream(), logger, mpeg);
            ProcessingLoggingHandler errorHandler = new ProcessingLoggingHandler(process.getErrorStream(), logger, mpeg);
            outputHandler.start();
            errorHandler.start();

            int rc = process.waitFor();
            if (rc != 0)
                logger.warn("Command exited with code: {}", rc);
            return rc;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return -1;
    }

    /**
     * List of string to omit when logging the output from ffmpeg
     */
    private static List<String> getFilterList()
    {
        List<String> mpeg = new ArrayList<>();
        mpeg.add("buffer underflow");
        mpeg.add("packet too large");
        mpeg.add("Last message repeated");
        mpeg.add("Invalid frame dimensions 0x0");
        return mpeg;
    }


    /**
     * Generate the map parameter for ffmpeg. See https://trac.ffmpeg.org/wiki/Map
     * @param numberOfStreams - Number of streams to generate. This should be 1 + number of Audio streams to copy.
     * @return map parameter to pass ot the ffmpeg command line
     */
    String generateMap(int numberOfStreams)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numberOfStreams; i++)
            sb.append("-map 0:").append(i).append(" ");
        sb.setLength(sb.length() - 1);
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


    /**
     * Parse the cut list and generate the list of chapters we need ot save from a media file
     * @param cutList - List of commercials to remove as created by Comskip with the -zpcut option
     * @return List of Chapters to preserve from the media file
     */
    List<Chapter> getChapterLists(List<String> cutList)
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


    String formatTime(float totalSecs)
    {
        int hours = (int)(totalSecs / 3600);
        int minutes = (int)((totalSecs % 3600) / 60);
        int seconds = (int)totalSecs % 60;

        if (hours > 0)
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        else if (minutes > 0)
            return String.format("%d:%02d", minutes, seconds);
        return String.format("%d", seconds);
    }
}
