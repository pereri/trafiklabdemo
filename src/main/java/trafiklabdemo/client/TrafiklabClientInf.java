package trafiklabdemo.client;

import reactor.core.publisher.Mono;
import trafiklabdemo.client.model.BusLine;
import trafiklabdemo.client.model.JourneyPatternPointOnLine;
import trafiklabdemo.client.model.StopPoint;

import java.util.List;

public interface TrafiklabClientInf {
    Mono<List<BusLine>> getBusLines();

    Mono<List<JourneyPatternPointOnLine>> getJourneyPoints();

    Mono<List<StopPoint>> getBusLineStops();
}
