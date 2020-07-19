package trafiklabdemo;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Api {

    @Value
    @AllArgsConstructor
    public static class BusLine {
        String number;
        String designation;
    }

    @Value
    @AllArgsConstructor
    public static class StopPoint {
        String number;
        String name;
        String areaNumber;
        String locationNorthingCoordinate;
        String locationEastingCoordinate;
        String zoneShortName;
        String areaTypeCode;
    }


    public BusLine toApiModel(trafiklabdemo.client.model.BusLine busLine) {
        return new BusLine(
                busLine.getLineNumber(),
                busLine.getLineDesignation()
        );
    }

    public StopPoint toApiModel(trafiklabdemo.client.model.StopPoint stopPoint) {
        return new StopPoint(
                stopPoint.getStopPointNumber(),
                stopPoint.getStopPointName(),
                stopPoint.getStopAreaNumber(),
                stopPoint.getLocationNorthingCoordinate(),
                stopPoint.getLocationEastingCoordinate(),
                stopPoint.getZoneShortName(),
                stopPoint.getStopAreaNumber()
        );
    }
}
