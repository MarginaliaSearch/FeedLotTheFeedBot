# 🐄&nbsp;FeedLot, The FeedBot&nbsp;🤖

FLTFB is a RSS and Atom fetcher.  It fetches feeds and stores them in a SQLite database.  It's designed to run in
a container and for easy integration with other tools and processes.  It doesn't do much of anything else.  

It's designed for use with [Marginalia Search](https://search.marginalia.nu/), but could very well be used for other
things, such as a personal feed reader or podcast aggregator.

## Container Mount Points

Mount Point | Description | Notes
----------- | ----------- | ----
/db         | SQLite database | 
/data       | Data directory |
/data/definitions.txt | List of feeds to fetch | a list of feeds to fetch on the format `domain_name feed_url`

If the definitions are intended to be mutable over the course of the runtime of the container,
the `definitions.txt` file needs to be updated by an external process, such as a git pull.

The container will automatically reload the `definitions.txt` on each update.

## Environment Variables

Properties are set via FEEDLOT_OPTS. Set these properties like -Dproperty=value in the FEEDLOT_OPTS environment variable in the container.

| Variable | Default | Description |
| -------- |---------| ----------- |
feedlot.max-avg-posts-per-year | 24      | Maximum average number of posts per year.  Feeds with more posts than this will be ignored.  Useful to avoid feeds with a lot of noise.
feedlot.max-feed-items | 10      | Maximum number of items to persist from a feed.
bind.ip | 0.0.0.0 | IP address to bind the API to.
bind.port | 8080    | Port to bind the API to.

## API endpoints

By default the API listens on port 8080.

### /feeds [GET]

```json
[
  {
    "domain":"www.marginalia.nu",
    "feedUrl":"https://www.marginalia.nu/log/index.xml"
  }, 
  {...}
]
```

### /feed/:domain_name [GET]

```json
{
  "domain": "www.marginalia.nu",
  "feedUrl": "https://www.marginalia.nu/log/index.xml",
  "updated": "2023-12-23T15:05:03.961564566",
  "items": [
    {
      "title": "A Frivolous Feature",
      "date": "2023-12-22T00:00:00.000+0000",
      "description": "Marginalia Search very recently gained the ability to filter results by Autonomous System, not only searching by ASN but by the organization information for that AS. At a glance this seems like a somewhat frivolous feature, but it has interesting effects.",
      "url": "https://www.marginalia.nu/log/96_frivolous_asn/"
    },
    { ... }
  ]
}
```

### /update [POST]

Re-fetch all feeds.  Will immediately return a 200 response and 
start fetching feeds in the background.  The new feeds are fetched into
a new database, and which isn't made acessible until all feeds have 
been fetched.

```json
{"status": "ok"}
```
