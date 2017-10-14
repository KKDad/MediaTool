package org.westfield.media;

import java.io.File;

public class MediaDetails implements IMediaDetails
{
    private final String show;
    private final String episodeTitle;
    private final int season;
    private final int episodeNumber;
    private File mediaFile;


    public MediaDetails(String show, int season, String episodeTitle, int episodeNumber)
    {
        this.show = show;
        this.season = season;
        this.episodeNumber = episodeNumber;
        this.episodeTitle = episodeTitle;
        this.mediaFile = null;
    }

    public String getShow() {
        return show;
    }

    public String getEpisodeTitle() {
        return episodeTitle;
    }

    @Override
    public File getMediaFile() {
        return this.mediaFile;
    }

    @Override
    public MediaDetails setMediaFile(File file)
    {
        this.mediaFile = file;
        return this;
    }

    public int getSeason() {
        return season;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }


}
