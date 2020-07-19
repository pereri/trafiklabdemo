package trafiklabdemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import trafiklabdemo.client.TrafiklabClientInf;
import trafiklabdemo.client.model.BusLine;
import trafiklabdemo.client.model.JourneyPatternPointOnLine;
import trafiklabdemo.client.model.StopPoint;
import trafiklabdemo.exceptions.AppErrors;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TrafiklabService {

    private static final Collection<JourneyPatternPointOnLine> EMPTY_SET = Set.of();
    private final TrafiklabClientInf client;


    @Autowired
    public TrafiklabService(@Qualifier("CachedTrafiklabClient") final TrafiklabClientInf client) {
        this.client = client;
    }

    public Mono<List<BusLine>> getTopBusLinesByNumberOfStops() {
        Mono<Map<String, Collection<JourneyPatternPointOnLine>>> busStops =
                client.getJourneyPoints()
                      .flatMapIterable(Function.identity())
                      .collectMultimap(JourneyPatternPointOnLine::getLineNumber);

        Mono<Map<String, BusLine>> busLines =
                client.getBusLines()
                      .flatMapIterable(Function.identity())
                      .collectMap(BusLine::getLineNumber);

        return Mono.zip(busStops, busLines, this::topBusLinesByStops);
    }

    public Mono<List<StopPoint>> getStopsForLine(String lineNumber) {
        Mono<Void> validLineNumber = client.getBusLines()
                                    .flatMapIterable(Function.identity())
                                    .any(line -> lineNumber.equals(line.getLineNumber()))
                                    .flatMap(lineExists -> lineExists ? Mono.empty() : AppErrors.notFound("Line number not found"));


        Mono<Set<String>> stopPointNumbers = client.getJourneyPoints()
                                                   .flatMapIterable(Function.identity())
                                                   .filter(stopPoint -> lineNumber.equals(stopPoint.getLineNumber()))
                                                   .map(JourneyPatternPointOnLine::getJourneyPatternPointNumber)
                                                   .collect(Collectors.toSet());

        Mono<List<StopPoint>> stopPoints = client.getBusLineStops();

        Mono<List<StopPoint>> stopPointsForLine = Mono.zip(stopPointNumbers, stopPoints, this::filterNumbers);

        return validLineNumber.then(stopPointsForLine);
    }

    List<BusLine> topBusLinesByStops(final Map<String, Collection<JourneyPatternPointOnLine>> busStops,
                                     final Map<String, BusLine> busLinesByLineNumber) {
        Comparator<String> numberOfStops = Comparator
                .comparingInt(lineNumber -> busStops.getOrDefault(lineNumber, EMPTY_SET).size());

        Comparator<Map.Entry<String, BusLine>> numberOfStopsByLineNumberReversed =
                Map.Entry.<String, BusLine>comparingByKey(numberOfStops).reversed();

        return busLinesByLineNumber.entrySet()
                                   .stream()
                                   .sorted(numberOfStopsByLineNumberReversed)
                                   .limit(10)
                                   .map(Map.Entry::getValue)
                                   .collect(Collectors.toList());

    }


    List<StopPoint> filterNumbers(final Set<String> stopPointNumbers,
                                  final List<StopPoint> stopPoints) {
        return stopPoints.stream()
                         .filter(stopPoint -> stopPointNumbers.contains(stopPoint.getStopPointNumber()))
                         .collect(Collectors.toList());
    }


}
