package nu.marginalia.feedlot.svc;

import com.google.errorprone.annotations.MustBeClosed;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import nu.marginalia.feedlot.model.FeedDefinition;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class FeedDefinitionsService {
    private final Path definitionsPath;

    @Inject
    public FeedDefinitionsService(@Named("definitions-path") Path definitionsPath) {
        this.definitionsPath = definitionsPath;
    }

    @MustBeClosed
    public Stream<FeedDefinition> readDefinitions() throws IOException {
        return Files.lines(definitionsPath)
                .map(this::parse)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private Optional<FeedDefinition> parse(String line) {
        if (StringUtils.isBlank(line)) {
            return Optional.empty();
        }

        line = line.trim();

        if (line.startsWith("#")) {
            return Optional.empty();
        }

        var parts = StringUtils.split(line, " \t", 2);
        if (parts.length != 2) {
            return Optional.empty();
        }
        var domain = parts[0].trim();
        var feedUrl = parts[1].trim();

        return Optional.of(new FeedDefinition(domain, feedUrl));
    }

}
