package trafiklabdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import trafiklabdemo.client.model.BusLine;
import trafiklabdemo.client.model.StopPoint;
import trafiklabdemo.exceptions.AppErrors;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static trafiklabdemo.TestUtil.newBusLine;
import static trafiklabdemo.TestUtil.newStopPoint;

@WebFluxTest(BusController.class)
class BusControllerTests {

    @Autowired
    WebTestClient client;

    @MockBean
    TrafiklabService service;

    @Test
    void getLines_test() {
        List<BusLine> lines = List.of(
                newBusLine("1")
        );

        List<Api.BusLine> expected = lines
                .stream()
                .map(line -> new Api.BusLine(line.getLineNumber(),
                                             line.getLineDesignation()))
                .collect(Collectors.toList());

        when(service.getTopBusLinesByNumberOfStops())
                .thenReturn(Mono.just(lines));

        client.get()
              .uri("/api/lines")
              .accept(MediaType.APPLICATION_JSON)
              .exchange()
              .expectStatus().isOk()
              .expectBodyList(Api.BusLine.class)
              .isEqualTo(expected);

        verify(service).getTopBusLinesByNumberOfStops();
    }

    @Test
    void getStops_test() {
        List<StopPoint> stops = List.of(
                newStopPoint("1")
        );

        List<Api.StopPoint> expected = stops
                .stream()
                .map(stop -> new Api.StopPoint(
                        stop.getStopPointNumber(),
                        stop.getStopPointName(),
                        stop.getStopAreaNumber(),
                        stop.getLocationNorthingCoordinate(),
                        stop.getLocationEastingCoordinate(),
                        stop.getZoneShortName(),
                        stop.getStopAreaNumber()
                ))
                .collect(Collectors.toList());

        when(service.getStopsForLine(eq("1")))
                .thenReturn(Mono.just(stops));

        client.get()
              .uri("/api/stops/1")
              .accept(MediaType.APPLICATION_JSON)
              .exchange()
              .expectStatus().isOk()
              .expectBodyList(Api.StopPoint.class)
              .isEqualTo(expected);

        verify(service).getStopsForLine(eq("1"));
    }

    @Test
    void getStops_line_not_found_error_test() {
        when(service.getStopsForLine(eq("1")))
                .thenReturn(AppErrors.notFound("not found"));

        client.get()
              .uri("/api/stops/1")
              .accept(MediaType.APPLICATION_JSON)
              .exchange()
              .expectStatus().isNotFound()
              .expectBody()
              .jsonPath("message").isEqualTo("not found")
              .jsonPath("status").isEqualTo(404);

        verify(service).getStopsForLine(eq("1"));
    }


}
