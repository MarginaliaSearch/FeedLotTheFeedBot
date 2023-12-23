package nu.marginalia.feedlot.svc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import nu.marginalia.feedlot.db.FeedDb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Spark;

public class FeedlotApiService {
    private static final String bindIp = System.getProperty("bind.ip", "0.0.0.0");
    private static final int bindPort = Integer.getInteger("bind.port", 8080);
    private static final Logger logger = LoggerFactory.getLogger(FeedlotApiService.class);

    private static final Gson gson = new GsonBuilder().create();

    private final FeedDb feedDb;
    private final FeedFetcherService feedFetcherService;

    @Inject
    public FeedlotApiService(FeedDb feedDb, FeedFetcherService feedFetcherService) {
        this.feedDb = feedDb;
        this.feedFetcherService = feedFetcherService;
    }

    public void start() {

        logger.info("Starting FeedlotApiService[{}:{}]", bindIp, bindPort);

        Spark.ipAddress(bindIp);
        Spark.port(bindPort);

        Spark.get("/feed/:domain", this::getFeedForDomain);
        Spark.get("/feeds", this::getAllFeeds, gson::toJson);
        Spark.post("/update", this::updateFeeds);
        Spark.init();
    }

    private Object getAllFeeds(Request request, Response response) {
        response.type("application/json");

        return feedDb.getAllFeeds();
    }

    private Object updateFeeds(Request request, Response response) {
        response.type("application/json");
        Thread.ofPlatform().start(feedFetcherService::updateFeeds);
        return "{ \"status\": \"ok\" }";
    }

    private Object getFeedForDomain(Request request, Response response) {
        response.type("application/json");

        var maybeJson = feedDb.getFeedAsJson(request.params(":domain"));

        if (maybeJson.isEmpty()) {
            response.status(404);
            return "";
        } else {
            return maybeJson.get();
        }
    }
}
