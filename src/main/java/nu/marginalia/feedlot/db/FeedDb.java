package nu.marginalia.feedlot.db;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.inject.Named;
import nu.marginalia.feedlot.model.FeedDefinition;
import nu.marginalia.feedlot.model.FeedItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Singleton
public class FeedDb {
    private static final Logger logger = LoggerFactory.getLogger(FeedDb.class);

    private final Path dbPathRoot;
    private final Path readerDbPath;
    private volatile FeedDbReader reader;

    @Inject
    public FeedDb(@Named("db-root") Path dbPathRoot) {
        this.dbPathRoot = dbPathRoot;
        readerDbPath = dbPathRoot.resolve("feeds.db");
        try {
            reader = new FeedDbReader(readerDbPath);
        } catch (Exception e) {
            reader = null;
        }
    }

    public List<FeedDefinition> getAllFeeds() {
        var reader = this.reader;

        try {
            if (reader != null) {
                return reader.getAllFeeds();
            }
        }
        catch (Exception e) {
            logger.error("Error getting all feeds", e);
        }
        return List.of();


    }

    public FeedItems getFeed(String domain) {
        var reader = this.reader;

        try {
            if (reader != null) {
                return reader.getFeed(domain);
            }
        }
        catch (Exception e) {
            logger.error("Error getting feed for " + domain, e);
        }
        return FeedItems.none();
    }

    public Optional<String> getFeedAsJson(String domain) {
        var reader = this.reader;

        try {
            if (reader != null) {
                return reader.getFeedAsJson(domain);
            }
        }
        catch (Exception e) {
            logger.error("Error getting feed for " + domain, e);
        }
        return Optional.empty();
    }

    public FeedDbWriter getWriter() {
        try {
            Path dbFile = Files.createTempFile(dbPathRoot, "feeds", ".db");
            return new FeedDbWriter(dbFile);
        } catch (Exception e) {
            logger.error("Error creating new database writer", e);
            return null;
        }
    }

    public void switchDb(FeedDbWriter writer) {
        try {
            logger.info("Switching to new feed database from " + writer.getDbPath() + " to " + readerDbPath);

            writer.close();
            reader.close();

            Files.move(writer.getDbPath(), readerDbPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

            reader = new FeedDbReader(readerDbPath);
        } catch (Exception e) {
            logger.error("Fatal error switching to new feed database", e);
            System.exit(1);
        }
    }

}
