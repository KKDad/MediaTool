package org.westfield.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.configuration.LookupHint;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.lookup.theTvDBClient;
import org.westfield.media.IMediaDetails;

import java.util.List;
import java.util.Optional;


public class ShowLookup extends Action {
    private static final Logger logger = LoggerFactory.getLogger(ShowLookup.class);

    private theTvDBClient client;
    private List<LookupHint> overides;
    private int retries = 2;

    @Override
    public void describe()
    {
        logger.warn("ShowLookup will augment the tags available for InfoCreator and RenameMedia with details from theTvDB.com");
    }

    @Override
    public boolean configure(MediaToolConfig config) {
        this.enabled = Boolean.parseBoolean(config.getShowLookup().get("enabled"));
        this.overides = config.getLookupHints();
        boolean cacheKey = Boolean.parseBoolean(config.getShowLookup().get("cache_key"));
        String cacheLocation = config.getShowLookup().get("cache_location");
        String apiKey = config.getShowLookup().get("key");
        if (this.enabled) {
            this.client = new theTvDBClient(cacheKey, cacheLocation, apiKey);
            if (!this.client.login()) {
                logger.error("Login to theTvDB.com failed.");
            }
        }
        return true;
    }

    @Override
    public IMediaDetails process(IMediaDetails details) {
        if (!this.enabled || details.getSeason() == 0 || details.getEpisodeNumber() == 0)
            return details;

        logger.info("Processing {} - {}", details.getShow(), details.getEpisodeTitle());
        Optional<LookupHint> lookupHint = this.overides.stream().filter(p -> details.getShow().equalsIgnoreCase(p.getShow())).findFirst();

        // Search for the Show with a retry
        theTvDBClient.SearchResponse result = null;
        for (int i = 0; i < this.retries && result == null; i++) {
            if (lookupHint.isPresent())
                result = this.client.search(details.getShow(), lookupHint.get().getImdbId(), lookupHint.get().getZap2itId());
            else
                result = this.client.search(details.getShow(), null, null);
        }

        // Process the result
        if (result != null && !result.data.isEmpty()) {
            if (lookupHint.isPresent())
                getShowDetails(details, result.data.get(0));
            else
                for (theTvDBClient.SeriesItem item : result.data) {
                    if (item.seriesName.equalsIgnoreCase(details.getShow()))
                        getShowDetails(details, item);
                    else
                        logger.info("    No match: {}", item);
            }
        } else
            logger.info("Unable to lookup Show");
        return details;
    }

    private void getShowDetails(IMediaDetails details, theTvDBClient.SeriesItem item)
    {
            logger.info("  Loading details items from Matched: {}", item);
            details.getExtendedDetails().putIfAbsent("aliases", item.aliases);
            details.getExtendedDetails().putIfAbsent("banner", item.banner);
            details.getExtendedDetails().putIfAbsent("id", item.id);
            details.getExtendedDetails().putIfAbsent("network", item.network);
            details.getExtendedDetails().putIfAbsent("showOverview", item.overview);
            details.getExtendedDetails().putIfAbsent("status", item.status);
            details.getExtendedDetails().putIfAbsent("seriesName", item.seriesName);
            details.getExtendedDetails().putIfAbsent("imdbId", item.imdbId);
            details.getExtendedDetails().putIfAbsent("zap2itId", item.zap2itId);
            details.getExtendedDetails().putIfAbsent("genre", item.genre);
            getEpisodeDetails(details, item);
    }

    private void getEpisodeDetails(IMediaDetails details, theTvDBClient.SeriesItem item)
    {
        theTvDBClient.EpisodeResponse episodes = this.client.searchEpisode(item.id, details.getSeason(), details.getEpisodeNumber());
        if (episodes == null)
            episodes = this.client.searchEpisode(item.id, details.getSeason(), details.getEpisodeNumber());
        if (episodes != null && !episodes.data.isEmpty()) {
            theTvDBClient.EpisodeItem episode = episodes.data.get(0);
            logger.info("Found Episode Information: {}", episode);
            details.getExtendedDetails().putIfAbsent("absoluteNumber", episode.absoluteNumber);
            details.getExtendedDetails().putIfAbsent("airedEpisodeNumber", episode.airedEpisodeNumber);
            details.getExtendedDetails().putIfAbsent("airedSeason", episode.airedSeason);
            details.getExtendedDetails().putIfAbsent("dvdEpisodeNumber", episode.dvdEpisodeNumber);
            details.getExtendedDetails().putIfAbsent("dvdSeason", episode.dvdSeason);
            details.getExtendedDetails().putIfAbsent("episodeName", episode.episodeName);
            details.getExtendedDetails().putIfAbsent("firstAired", episode.firstAired);
            details.getExtendedDetails().putIfAbsent("episodeOverview", episode.overview);
            details.getExtendedDetails().putIfAbsent("id", episode.id);
        }
    }
}