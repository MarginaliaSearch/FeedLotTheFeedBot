package nu.marginalia.feedlot.model;

import com.apptasticsoftware.rssreader.Item;
import nu.marginalia.feedlot.format.DateCleaner;
import nu.marginalia.feedlot.format.DescriptionCleaner;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

public record FeedItem(String title,
                       String date,
                       String description,
                       String url) implements Comparable<FeedItem>
{
    public FeedItem(Item item) {
        this(item.getTitle().orElse(""),
                getItemDate(item).map(DateCleaner::cleanDate).orElse(""),
                item.getDescription().map(DescriptionCleaner::clean).orElse(""),
                item.getLink().orElse("")
        );
    }

    // e.g. http://fabiensanglard.net/rss.xml does dates like this:  1 Apr 2021 00:00:00 +0000
    private static DateTimeFormatter extraFormatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm:ss Z");
    private static Optional<ZonedDateTime> getItemDate(Item item) {
        try {
            return item.getPubDateZonedDateTime();
        }
        catch (Exception e) {
            return item.getPubDate()
                    .map(extraFormatter::parse)
                    .map(ZonedDateTime::from);
        }
    }

    @Override
    public int compareTo(@NotNull FeedItem o) {
        return o.date.compareTo(date);
    }
}
