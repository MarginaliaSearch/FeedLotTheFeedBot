package nu.marginalia.feedlot.svc;

import com.apptasticsoftware.rssreader.RssReader;
import com.google.inject.Inject;
import nu.marginalia.feedlot.db.FeedDb;
import nu.marginalia.feedlot.model.FeedDefinition;
import nu.marginalia.feedlot.model.FeedItem;
import nu.marginalia.feedlot.model.FeedItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class FeedFetcherService {
    private static final int MAX_FEED_ITEMS = Integer.getInteger("feedlot.max-feed-items", 10);

    private static final Logger logger = LoggerFactory.getLogger(FeedFetcherService.class);

    private final RssReader rssReader = new RssReader();
    private final FeedDb feedDb;
    private final FeedDefinitionsService definitionsService;

    @Inject
    public FeedFetcherService(FeedDb feedDb,
                              FeedDefinitionsService definitionsService)
    {

        this.feedDb = feedDb;
        this.definitionsService = definitionsService;
    }

    public void updateFeeds() {
        try (var definitions = definitionsService.readDefinitions();
             var writer = feedDb.getWriter()
        ) {

            definitions
                    .parallel()
                    .map(this::fetch)
                    .filter(items -> !items.isEmpty())
                    .forEach(writer::saveFeed);

            feedDb.switchDb(writer);

        } catch (Exception e) {
            logger.error("Error updating feeds", e);
        }
    }

    public FeedItems fetch(FeedDefinition definition) {
        try {
            var items = rssReader.read(definition.feedUrl())
                    .map(FeedItem::new)
                    .filter(new IsFeedItemDateValid())
                    .sorted()
                    .limit(MAX_FEED_ITEMS)
                    .toList();

            return new FeedItems(definition.domain(),
                    definition.feedUrl(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    items);

        } catch (Exception e) {
            logger.warn("Failed to read feed {}: {}", definition.feedUrl(), e.getMessage());

            logger.debug("Exception", e);
            return FeedItems.none();
        }
    }

    private static class IsFeedItemDateValid implements Predicate<FeedItem> {
        private final String today = ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME);

        public boolean test(FeedItem item) {
            var date = item.date();

            if (date.isBlank()) {
                return false;
            }

            if (date.compareTo(today) > 0) {
                return false;
            }

            return true;
        }
    }
}
