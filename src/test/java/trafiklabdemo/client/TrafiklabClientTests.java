package trafiklabdemo.client;

import trafiklabdemo.client.model.BusLine;
import trafiklabdemo.client.model.JourneyPatternPointOnLine;
import trafiklabdemo.client.model.StopPoint;
import trafiklabdemo.exceptions.TLResponseException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;


@SpringBootTest
class TrafiklabClientTests {

    @Value("classpath:buslines.json")
    private Resource busLinesResponse;
    @Value("classpath:journeypoints.json")
    private Resource journeyPointsResponse;
    @Value("classpath:stoppoints.json")
    private Resource stopPointsResponse;

    @Value("classpath:error.json")
    private Resource errorResponse;

    public static MockWebServer mockBackEnd;

    private String read(Resource resource) throws IOException {
        return Files.readString(resource.getFile().toPath());
    }

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    private TrafiklabClient client;

    @BeforeEach
    void beforeEach() {
        client = new TrafiklabClient(UUID.randomUUID().toString(),
                                     String.format("http://%s:%s", mockBackEnd.getHostName(), mockBackEnd.getPort()));
    }


    @Test
    void getBussLines() throws IOException {
        MockResponse response = new MockResponse()
                .setBody(read(busLinesResponse))
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json; charset=utf-8");
        mockBackEnd.enqueue(response);
        List<BusLine> busLines = client.getBusLines()
                                       .block();

        Matcher<BusLine> isAValidBusLine = allOf(
                hasProperty("lineNumber", notNullValue()),
                hasProperty("lineNumber", isA(String.class)),
                hasProperty("lineDesignation", notNullValue()),
                hasProperty("lineDesignation", isA(String.class)),
                hasProperty("defaultTransportModeCode", notNullValue()),
                hasProperty("defaultTransportModeCode", isA(String.class)),
                hasProperty("lastModifiedUtcDateTime", notNullValue()),
                hasProperty("lastModifiedUtcDateTime", isA(String.class)),
                hasProperty("existsFromDate", notNullValue()),
                hasProperty("existsFromDate", isA(String.class))
        );

        assertThat(busLines, notNullValue());
        assertThat(busLines, hasSize(567));
        assertThat(busLines, everyItem(isAValidBusLine));

    }

    @Test
    void getJourneyPoints() throws IOException {
        MockResponse response = new MockResponse()
                .setBody(read(journeyPointsResponse))
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json; charset=utf-8");
        mockBackEnd.enqueue(response);
        List<JourneyPatternPointOnLine> journeyPoints = client.getJourneyPoints()
                                                              .block();

        Matcher<JourneyPatternPointOnLine> isAValidJourneyPoint = allOf(
                hasProperty("lineNumber", notNullValue()),
                hasProperty("lineNumber", isA(String.class)),
                hasProperty("directionCode", notNullValue()),
                hasProperty("directionCode", isA(String.class)),
                hasProperty("journeyPatternPointNumber", notNullValue()),
                hasProperty("journeyPatternPointNumber", isA(String.class)),
                hasProperty("lastModifiedUtcDateTime", notNullValue()),
                hasProperty("lastModifiedUtcDateTime", isA(String.class)),
                hasProperty("existsFromDate", notNullValue()),
                hasProperty("existsFromDate", isA(String.class))
        );

        assertThat(journeyPoints, notNullValue());
        assertThat(journeyPoints, hasSize(24798));
        assertThat(journeyPoints, everyItem(isAValidJourneyPoint));
    }

    @Test
    void getStopPoints() throws IOException {
        MockResponse response = new MockResponse()
                .setBody(read(stopPointsResponse))
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json; charset=utf-8");
        mockBackEnd.enqueue(response);
        List<StopPoint> stopPoints = client.getBusLineStops()
                                           .block();


        Matcher<StopPoint> isAValidStopPoint = allOf(
                hasProperty("stopPointNumber", notNullValue()),
                hasProperty("stopPointNumber", isA(String.class)),
                hasProperty("stopPointName", notNullValue()),
                hasProperty("stopPointName", isA(String.class)),
                hasProperty("locationNorthingCoordinate", notNullValue()),
                hasProperty("locationNorthingCoordinate", isA(String.class)),
                hasProperty("locationEastingCoordinate", notNullValue()),
                hasProperty("locationEastingCoordinate", isA(String.class)),
                hasProperty("zoneShortName", notNullValue()),
                hasProperty("zoneShortName", isA(String.class)),
                hasProperty("stopAreaTypeCode", notNullValue()),
                hasProperty("stopAreaTypeCode", isA(String.class)),
                hasProperty("lastModifiedUtcDateTime", notNullValue()),
                hasProperty("lastModifiedUtcDateTime", isA(String.class)),
                hasProperty("existsFromDate", notNullValue()),
                hasProperty("existsFromDate", isA(String.class))
        );

        assertThat(stopPoints, notNullValue());
        assertThat(stopPoints, hasSize(13269));
        assertThat(stopPoints, everyItem(isAValidStopPoint));
    }

    @Test
    void errorResponseTest() throws IOException {
        MockResponse response = new MockResponse()
                .setBody(read(errorResponse))
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json; charset=utf-8");
        mockBackEnd.enqueue(response);


        Exception ex = Assertions.assertThrows(Exception.class, () -> client.getBusLines()
                                                                            .block());

        assertThat(ex.getCause(), isA(TLResponseException.class));
        assertThat(ex.getCause().getMessage(), equalTo("Invalid status code: 1002 ,Key is invalid"));
    }

    @Test
    void basStatusResponseTest() throws IOException {
        MockResponse response = new MockResponse()
                .setBody(read(errorResponse))
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json; charset=utf-8");
        mockBackEnd.enqueue(response);


        Exception ex = Assertions.assertThrows(Exception.class, () -> client.getBusLines()
                                                                            .block());

        assertThat(ex.getCause(), isA(TLResponseException.class));
        assertThat(ex.getCause().getMessage(), equalTo("Bad response from backend: 400 BAD_REQUEST"));
    }
}
