package org.westfield.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.westfield.configuration.MediaToolConfig;
import org.westfield.lookup.theTvDBClient;
import org.westfield.media.IMediaDetails;


public class ShowLookup implements IAction {
    private static final Logger logger = LoggerFactory.getLogger(RenameMedia.class);

    private boolean enabled;
    private theTvDBClient client;

    @Override
    public boolean configure(MediaToolConfig config) {
        this.enabled = Boolean.parseBoolean(config.getShowLookup().get("enabled"));
        if (this.enabled) {
            this.client = new theTvDBClient();
            this.client.login(config.getShowLookup().get("key"));
        }
        return true;
    }

    @Override
    public IMediaDetails process(IMediaDetails details) {
        if (!this.enabled)
            return details;

        logger.info("Processing {} - {}", details.getShow(), details.getEpisodeTitle());
        theTvDBClient.SearchResponse result = this.client.search(details.getShow());
        if (result != null && !result.data.isEmpty()) {
            for(theTvDBClient.SearchItem item: result.data)
            {
                if (item.seriesName.equalsIgnoreCase(details.getShow()))
                {
                    logger.debug("  Matched: {}", item);
                    details.getExtendedDetails().putIfAbsent("aliases", item.aliases);
                    details.getExtendedDetails().putIfAbsent("banner", item.banner);
                    details.getExtendedDetails().putIfAbsent("id", item.id);
                    details.getExtendedDetails().putIfAbsent("network", item.network);
                    details.getExtendedDetails().putIfAbsent("overview", item.overview);
                    details.getExtendedDetails().putIfAbsent("status", item.status);
                } else
                    logger.debug("    Found: {}", item);

            }
        } else
            logger.info("Unable to lookup Show");
        return details;
    }
}
