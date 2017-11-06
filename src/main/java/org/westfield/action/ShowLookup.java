package org.westfield.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.configuration.LookupHint;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.lookup.theTvDBClient;
import org.westfield.media.IMediaDetails;

import java.util.List;
import java.util.Optional;


public class ShowLookup implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(RenameMedia.class);

    private boolean enabled;
    private theTvDBClient client;
    private List<LookupHint> overides;

    @Override
    public boolean configure(MediaToolConfig config) {
        this.enabled = Boolean.parseBoolean(config.getShowLookup().get("enabled"));
        this.overides = config.getLookupHints();
        if (this.enabled) {
            this.client = new theTvDBClient();
            this.client.login(config.getShowLookup().get("key"));
        }
        return true;
    }

    @Override
    public IMediaDetails process(IMediaDetails details) {
        if (!this.enabled || details.getSeason() == 0 || details.getEpisodeNumber() == 0)
            return details;


        logger.info("Processing {} - {}", details.getShow(), details.getEpisodeTitle());
        theTvDBClient.SearchResponse result = this.client.search(details.getShow());
        if (result != null && !result.data.isEmpty()) {
            for (theTvDBClient.SearchItem item : result.data) {
                getShowDetails(details, item);

            }
        } else
            logger.info("Unable to lookup Show");
        return details;
    }

    private void getShowDetails(IMediaDetails details, theTvDBClient.SearchItem item) {
        Optional<LookupHint> overide = this.overides.stream().filter(p -> details.getShow().equalsIgnoreCase(p.getShow())).findFirst();

        if (overide.isPresent() && overide.get().getId() == item.id || !overide.isPresent() && item.seriesName.equalsIgnoreCase(details.getShow())) {
            logger.debug("  Matched: {}", item);
            details.getExtendedDetails().putIfAbsent("aliases", item.aliases);
            details.getExtendedDetails().putIfAbsent("banner", item.banner);
            details.getExtendedDetails().putIfAbsent("id", item.id);
            details.getExtendedDetails().putIfAbsent("network", item.network);
            details.getExtendedDetails().putIfAbsent("showOverview", item.overview);
            details.getExtendedDetails().putIfAbsent("status", item.status);
            getEpisodeDetails(details, item);
        } else
            logger.debug("    Found: {}", item);
    }

    private void getEpisodeDetails(IMediaDetails details, theTvDBClient.SearchItem item)
    {
        theTvDBClient.EpisodeResponse episodes = this.client.searchEpisode(item.id, details.getSeason(), details.getEpisodeNumber());
        if (episodes != null && !episodes.data.isEmpty()) {
            theTvDBClient.EpisodeItem episode = episodes.data.get(0);
            logger.info("Found Episode Information: {}", episode);
            details.getExtendedDetails().putIfAbsent("absoluteNumber", item.aliases);
            details.getExtendedDetails().putIfAbsent("airedEpisodeNumber", item.aliases);
            details.getExtendedDetails().putIfAbsent("airedSeason", item.aliases);
            details.getExtendedDetails().putIfAbsent("dvdEpisodeNumber", item.aliases);
            details.getExtendedDetails().putIfAbsent("dvdSeason", item.aliases);
            details.getExtendedDetails().putIfAbsent("episodeName", item.aliases);
            details.getExtendedDetails().putIfAbsent("firstAired", item.aliases);
            details.getExtendedDetails().putIfAbsent("episodeOverview", item.aliases);
        }
    }
}
