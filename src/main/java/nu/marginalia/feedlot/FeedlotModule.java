package nu.marginalia.feedlot;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.nio.file.Path;

public class FeedlotModule extends AbstractModule {
    public void configure() {
        bind(Path.class).annotatedWith(Names.named("db-root")).toInstance(Path.of(System.getProperty("feedlot.db-root", "/db")));
        bind(Path.class).annotatedWith(Names.named("definitions-path")).toInstance(Path.of(System.getProperty("feedlot.definitions", "/data/definitions.txt")));
    }
}
