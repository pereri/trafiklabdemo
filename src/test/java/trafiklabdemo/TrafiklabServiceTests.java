package trafiklabdemo;

import trafiklabdemo.client.CachedTrafiklabClient;
import trafiklabdemo.client.model.BusLine;
import trafiklabdemo.client.model.JourneyPatternPointOnLine;
import trafiklabdemo.client.model.StopPoint;
import trafiklabdemo.exceptions.InternalErrorException;
import trafiklabdemo.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.List;

import static trafiklabdemo.TestUtil.newBusLine;
import static trafiklabdemo.TestUtil.newJourneyPoint;
import static trafiklabdemo.TestUtil.newStopPoint;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
class TrafiklabServiceTests {

    @Mock
    private CachedTrafiklabClient client;

    @InjectMocks
    private TrafiklabService service;

    @Test
    void getTopBusLinesByNumberOfStops_sorts_by_number_of_stops() {
        List<BusLine> lines = List.of(
                newBusLine("1"),
                newBusLine("2"),
                newBusLine("3")
        );

        List<JourneyPatternPointOnLine> stops = List.of(
                newJourneyPoint("1", "p1"),
                newJourneyPoint("2", "p2"),
                newJourneyPoint("2", "p3")
        );

        when(client.getBusLines())
                .thenReturn(Mono.just(lines));

        when(client.getJourneyPoints())
                .thenReturn(Mono.just(stops));

        List<BusLine> result = service.getTopBusLinesByNumberOfStops()
                                      .block();


        assertThat(result, hasSize(3));
        assertThat(result, equalTo(List.of(
                lines.get(1),
                lines.get(0),
                lines.get(2)
        )));

        verify(client).getBusLines();
        verify(client).getJourneyPoints();
    }


    @Test
    void getTopBusLinesByNumberOfStops_limits_result_to_10() {
        List<BusLine> lines = List.of(
                newBusLine("1"),
                newBusLine("2"),
                newBusLine("3"),
                newBusLine("4"),
                newBusLine("5"),
                newBusLine("6"),
                newBusLine("7"),
                newBusLine("8"),
                newBusLine("9"),
                newBusLine("10"),
                newBusLine("11")
        );

        List<JourneyPatternPointOnLine> stops = List.of();

        when(client.getBusLines())
                .thenReturn(Mono.just(lines));

        when(client.getJourneyPoints())
                .thenReturn(Mono.just(stops));

        List<BusLine> result = service.getTopBusLinesByNumberOfStops()
                                      .block();

        assertThat(result, hasSize(10));

        verify(client).getBusLines();
        verify(client).getJourneyPoints();
    }

    @Test
    void getTopBusLinesByNumberOfStops_fails_when_getBusLines_fails() {
        List<JourneyPatternPointOnLine> stops = List.of();

        InternalErrorException error = new InternalErrorException("");

        when(client.getBusLines())
                .thenReturn(Mono.error(error));

        when(client.getJourneyPoints())
                .thenReturn(Mono.just(stops));

        Exception ex = assertThrows(Exception.class, () -> service.getTopBusLinesByNumberOfStops()
                                                                  .block());

        assertThat(ex.getCause(), is(error));

        verify(client).getBusLines();
        verify(client).getJourneyPoints();
    }

    @Test
    void getTopBusLinesByNumberOfStops_fails_when_getJourneyPoints_fails() {
        List<BusLine> lines = List.of();

        InternalErrorException error = new InternalErrorException("");

        when(client.getBusLines())
                .thenReturn(Mono.just(lines));

        when(client.getJourneyPoints())
                .thenReturn(Mono.error(error));

        Exception ex = assertThrows(Exception.class, () -> service.getTopBusLinesByNumberOfStops()
                                                                  .block());

        assertThat(ex.getCause(), is(error));

        verify(client).getBusLines();
        verify(client).getJourneyPoints();
    }


    @Test
    void getStopsForLine_returns_lines() {
        List<BusLine> lines = List.of(
                newBusLine("1"),
                newBusLine("2"),
                newBusLine("3")
        );

        List<StopPoint> points = List.of(
                newStopPoint("p1"),
                newStopPoint("p2"),
                newStopPoint("p3")
        );

        List<JourneyPatternPointOnLine> stops = List.of(
                newJourneyPoint("1", "p1"),
                newJourneyPoint("2", "p2"),
                newJourneyPoint("2", "p3")
        );

        when(client.getBusLines())
                .thenReturn(Mono.just(lines));

        when(client.getJourneyPoints())
                .thenReturn(Mono.just(stops));

        when(client.getBusLineStops())
                .thenReturn(Mono.just(points));

        List<StopPoint> result = service.getStopsForLine("2")
                                        .block();


        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(
                points.get(1),
                points.get(2)
        ));

        verify(client).getBusLines();
        verify(client).getJourneyPoints();
        verify(client).getBusLineStops();
    }

    @Test
    void getStopsForLine_returns_empty_lines() {
        List<BusLine> lines = List.of(
                newBusLine("1"),
                newBusLine("2"),
                newBusLine("3")
        );

        List<StopPoint> points = List.of(
                newStopPoint("p1"),
                newStopPoint("p2"),
                newStopPoint("p3")
        );

        List<JourneyPatternPointOnLine> stops = List.of(
                newJourneyPoint("1", "p1"),
                newJourneyPoint("2", "p2"),
                newJourneyPoint("2", "p3")
        );

        when(client.getBusLines())
                .thenReturn(Mono.just(lines));

        when(client.getJourneyPoints())
                .thenReturn(Mono.just(stops));

        when(client.getBusLineStops())
                .thenReturn(Mono.just(points));

        List<StopPoint> result = service.getStopsForLine("3")
                                        .block();


        assertThat(result, empty());

        verify(client).getBusLines();
        verify(client).getJourneyPoints();
        verify(client).getBusLineStops();
    }

    @Test
    void getStopsForLine_fails_for_invalid_line_number() {
        List<BusLine> lines = List.of(
                newBusLine("1"),
                newBusLine("3")
        );

        List<StopPoint> points = List.of(
                newStopPoint("p1"),
                newStopPoint("p3")
        );

        List<JourneyPatternPointOnLine> stops = List.of(
                newJourneyPoint("1", "p1")
        );

        when(client.getBusLines())
                .thenReturn(Mono.just(lines));

        when(client.getJourneyPoints())
                .thenReturn(Mono.just(stops));

        when(client.getBusLineStops())
                .thenReturn(Mono.just(points));

        Exception ex = assertThrows(Exception.class, () -> service.getStopsForLine("2")
                                                                  .block());

        assertThat(ex.getCause(), isA(NotFoundException.class));

        verify(client).getBusLines();
        verify(client).getJourneyPoints();
        verify(client).getBusLineStops();
    }


    @Test
    void getStopsForLine_fails_when_getBusLines_fails() {
        InternalErrorException error = new InternalErrorException("");

        List<StopPoint> points = List.of(
                newStopPoint("p1"),
                newStopPoint("p3")
        );

        List<JourneyPatternPointOnLine> stops = List.of(
                newJourneyPoint("1", "p1")
        );

        when(client.getBusLines())
                .thenReturn(Mono.error(error));

        when(client.getJourneyPoints())
                .thenReturn(Mono.just(stops));

        when(client.getBusLineStops())
                .thenReturn(Mono.just(points));

        Exception ex = assertThrows(Exception.class, () -> service.getStopsForLine("2")
                                                                  .block());

        assertThat(ex.getCause(), is(error));

        verify(client).getBusLines();
        verify(client).getJourneyPoints();
        verify(client).getBusLineStops();
    }
    @Test
    void getStopsForLine_fails_when_getJourneyPoints_fails() {
        List<BusLine> lines = List.of(
                newBusLine("1"),
                newBusLine("2"),
                newBusLine("3")
        );

        InternalErrorException error = new InternalErrorException("");

        List<StopPoint> points = List.of(
                newStopPoint("p1"),
                newStopPoint("p3")
        );

        when(client.getBusLines())
                .thenReturn(Mono.just(lines));

        when(client.getJourneyPoints())
                .thenReturn(Mono.error(error));

        when(client.getBusLineStops())
                .thenReturn(Mono.just(points));

        Exception ex = assertThrows(Exception.class, () -> service.getStopsForLine("2")
                                                                  .block());

        assertThat(ex.getCause(), is(error));

        verify(client).getBusLines();
        verify(client).getJourneyPoints();
        verify(client).getBusLineStops();
    }
    @Test
    void getStopsForLine_fails_when_getBusLineStops_fails() {
        List<BusLine> lines = List.of(
                newBusLine("1"),
                newBusLine("2"),
                newBusLine("3")
        );

        List<JourneyPatternPointOnLine> stops = List.of(
                newJourneyPoint("1", "p1")
        );

        InternalErrorException error = new InternalErrorException("");

        when(client.getBusLines())
                .thenReturn(Mono.just(lines));

        when(client.getJourneyPoints())
                .thenReturn(Mono.just(stops));

        when(client.getBusLineStops())
                .thenReturn(Mono.error(error));

        Exception ex = assertThrows(Exception.class, () -> service.getStopsForLine("2")
                                                                  .block());

        assertThat(ex.getCause(), is(error));

        verify(client).getBusLines();
        verify(client).getJourneyPoints();
        verify(client).getBusLineStops();
    }

}
