package nu.marginalia.feedlot.format;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

public class DescriptionCleaner {
    public static final int MAX_LENGTH = Integer.getInteger("feedlot.description.maxlength", 255);

    public static String clean(String rawDescription) {
        // If the description contains likely HTML, we strip it out
        if (rawDescription.indexOf('<') >= 0) {
            rawDescription = Jsoup.parseBodyFragment(rawDescription).text();
        }

        return StringUtils.truncate(rawDescription, MAX_LENGTH);
    }
}
