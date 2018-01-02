package org.westfield.thetvdb;

import java.util.List;


@SuppressWarnings("squid:ClassVariableVisibilityCheck")
public class SeriesItem
{
    public List<String> aliases;
    public String banner;
    public String firstAired;
    public int id;
    public String network;
    public String overview;
    public String seriesName;
    public String status;
    // Added via a call to https://api.thetvdb.com/swagger#!/Series/get_series_id
    public List<String> genre;
    public String imdbId;
    public String zap2itId;

    @Override
    public String toString()
    {
        return String.format("SeriesItem{id=%d, seriesName='%s', status='%s', imdbId='%s', zap2itId='%s'}", id, seriesName, status, imdbId, zap2itId);
    }
}
