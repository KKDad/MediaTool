package org.westfield.media;


import java.io.File;
import java.util.Map;

public interface IMediaDetails
{
    String getShow();
    int getEpisodeNumber();
    int getSeason();
    String getEpisodeTitle();

    File getMediaFile();
    IMediaDetails setMediaFile(File file);

    Map<String, Object> getExtendedDetails();
}
