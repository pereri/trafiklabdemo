package trafiklabdemo;

import trafiklabdemo.client.model.BusLine;
import trafiklabdemo.client.model.JourneyPatternPointOnLine;
import trafiklabdemo.client.model.StopPoint;

import java.util.UUID;

public class TestUtil {

    public static BusLine newBusLine(String lineNumber) {
        return new BusLine(lineNumber,
                           lineNumber,
                           "",
                           "BUS",
                           UUID.randomUUID().toString(),
                           UUID.randomUUID().toString());
    }

    public static JourneyPatternPointOnLine newJourneyPoint(String lineNumber, String journeyPatternPointNumber) {
        return new JourneyPatternPointOnLine(lineNumber,
                                             "",
                                             journeyPatternPointNumber,
                                             UUID.randomUUID().toString(),
                                             UUID.randomUUID().toString());
    }

    public static StopPoint newStopPoint(String stopPointNumber) {
        return new StopPoint(stopPointNumber,
                             stopPointNumber,
                             stopPointNumber,
                             UUID.randomUUID().toString(),
                             UUID.randomUUID().toString(),
                             UUID.randomUUID().toString(),
                             UUID.randomUUID().toString(),
                             UUID.randomUUID().toString(),
                             UUID.randomUUID().toString());
    }


}
