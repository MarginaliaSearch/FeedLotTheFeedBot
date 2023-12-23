package nu.marginalia.feedlot.filter;

import nu.marginalia.feedlot.model.FeedItem;

import java.util.List;
import java.util.stream.Collectors;

public class AveragePostRateFilter {
    public static final double MAX_AVG_POSTS_PER_YEAR = Integer.getInteger("feedlot.max-avg-posts-per-year", 24);

    public static boolean shouldKeep(List<FeedItem> items) {
        return !(getCountByYear(items) > MAX_AVG_POSTS_PER_YEAR);
    }

    private static double getCountByYear(List<FeedItem> items) {
        var countByMonth = items.stream().map(FeedItem::date).collect(Collectors.groupingBy(date -> date.substring(0, 7)));
        return 12. * countByMonth.values().stream().mapToInt(List::size).average().orElse(0);
    }
}
