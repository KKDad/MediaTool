package org.westfield.thetvdb;

@SuppressWarnings("squid:ClassVariableVisibilityCheck")
public class EpisodeItem
{
    public String absoluteNumber;
    public String airedEpisodeNumber;
    public String airedSeason;
    public String dvdEpisodeNumber;
    public String dvdSeason;
    public String episodeName;
    public String firstAired;
    public String id;
    public String lastUpdated;
    public String overview;

    @Override
    public String toString()
    {
        return String.format("EpisodeItem{airedEpisodeNumber='%s', airedSeason='%s', episodeName='%s', lastUpdated='%s'}", airedEpisodeNumber, airedSeason, episodeName, lastUpdated);
    }
}
