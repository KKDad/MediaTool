package org.westfield.media;

public class MediaDetails implements IMediaDetails
{
    private String show;
    private String episodeTitle;
    private int season;
    private int episodeNumber;

    MediaDetails(String show, int season, String episodeTitle, int episodeNumber)
    {
        this.show = show;
        this.season = season;
        this.episodeNumber = episodeNumber;
        this.episodeTitle = episodeTitle;
    }

    public String getShow() {
        return show;
    }

    public String getEpisodeTitle() {
        return episodeTitle;
    }

    public int getSeason() {
        return season;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }
}
