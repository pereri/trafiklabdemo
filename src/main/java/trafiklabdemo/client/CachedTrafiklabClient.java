package trafiklabdemo.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import trafiklabdemo.client.model.BusLine;
import trafiklabdemo.client.model.JourneyPatternPointOnLine;
import trafiklabdemo.client.model.StopPoint;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Qualifier("CachedTrafiklabClient")
public class CachedTrafiklabClient implements TrafiklabClient {

    private final TrafiklabClient client;
    private final Cache cache;

    @Autowired
    public CachedTrafiklabClient(@Qualifier("TrafiklabClientImpl") final TrafiklabClient client,
                                 final CacheManager cache) {
        this.client = client;
        this.cache = Objects.requireNonNull(cache.getCache("trafiklab"));
    }

    private <VALUE> Mono<Signal<? extends VALUE>> read(final String key) {
        return Mono.create(s -> {
            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null) {
                s.success(Signal.next((VALUE) Objects.requireNonNull(wrapper.get())));
            } else {
                s.success();
            }
        });
    }

    private <VALUE> Mono<Void> write(final String key, final Signal<? extends VALUE> signal) {
        return Mono.just(signal)
                   .dematerialize()
                   .doOnNext(value -> cache.put(key, value))
                   .then();
    }

    @PostConstruct
    void load() {
        log.debug("loading data");
        Mono.zip(client.getBusLines(),
                 client.getJourneyPoints(),
                 client.getBusLineStops())
            .doOnNext(tuple -> {
                log.debug("data loaded, lines: {}, points: {}, stops: {}",
                          tuple.getT1().size(),
                          tuple.getT2().size(),
                          tuple.getT3().size());
                cache.put("lines", tuple.getT1());
                cache.put("points", tuple.getT2());
                cache.put("stops", tuple.getT3());
            })
            .then()
            .block();
    }

    @Scheduled(cron = "0 0 3 * * *")
    void reloadCache() {
        log.debug("reloading cache");
        load();
    }

    @Override
    public Mono<List<BusLine>> getBusLines() {
        return CacheMono.<String, List<BusLine>>lookup(this::read, "lines")
                .onCacheMissResume(client.getBusLines())
                .andWriteWith(this::write);
    }

    @Override
    public Mono<List<JourneyPatternPointOnLine>> getJourneyPoints() {
        return CacheMono.<String, List<JourneyPatternPointOnLine>>lookup(this::read, "points")
                .onCacheMissResume(client.getJourneyPoints())
                .andWriteWith(this::write);
    }

    @Override
    public Mono<List<StopPoint>> getBusLineStops() {
        return CacheMono.<String, List<StopPoint>>lookup(this::read, "stops")
                .onCacheMissResume(client.getBusLineStops())
                .andWriteWith(this::write);
    }


}
