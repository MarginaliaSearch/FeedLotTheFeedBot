package nu.marginalia.feedlot;

import com.google.inject.Guice;
import nu.marginalia.feedlot.svc.FeedlotApiService;

public class FeedlotMain {
    public static void main(String... args) {
        var injector = Guice.createInjector(new FeedlotModule());
        var service = injector.getInstance(FeedlotApiService.class);

        service.start();
    }
}
