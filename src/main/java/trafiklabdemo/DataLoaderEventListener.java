package trafiklabdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import trafiklabdemo.client.CachedTrafiklabClient;
import trafiklabdemo.client.TrafiklabClient;

import java.util.Objects;

@Slf4j
@Component
@Profile("!test")
public class DataLoaderEventListener implements ApplicationListener<ApplicationReadyEvent> {

    private final Cache cache;
    private final TrafiklabClient client;

    @Autowired
    public DataLoaderEventListener(final CacheManager cacheManager,
                                   @Qualifier("TrafiklabClientImpl") final TrafiklabClient client) {
        this.cache = Objects.requireNonNull(cacheManager.getCache(CachedTrafiklabClient.CACHE_NAME));
        this.client = client;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        reloadCache();
    }

    @Scheduled(cron = "0 0 3 * * *")
    void reloadCache() {
        log.debug("loading data");
        Mono.zip(client.getBusLines(),
                 client.getJourneyPoints(),
                 client.getBusLineStops())
            .doOnNext(tuple -> {
                log.debug("data loaded, lines: {}, points: {}, stops: {}",
                          tuple.getT1().size(),
                          tuple.getT2().size(),
                          tuple.getT3().size());
                cache.put(CachedTrafiklabClient.LINES, tuple.getT1());
                cache.put(CachedTrafiklabClient.POINTS, tuple.getT2());
                cache.put(CachedTrafiklabClient.STOPS, tuple.getT3());
            })
            .then()
            .block();
    }

}
