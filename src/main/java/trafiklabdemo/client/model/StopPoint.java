package trafiklabdemo.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StopPoint {
    String stopPointNumber;
    String stopPointName;
    String stopAreaNumber;
    String locationNorthingCoordinate;
    String locationEastingCoordinate;
    String zoneShortName;
    String stopAreaTypeCode;
    String lastModifiedUtcDateTime;
    String existsFromDate;

    @JsonCreator
    public static StopPoint create(@JsonProperty("StopPointNumber") final String stopPointNumber,
                                   @JsonProperty("StopPointName") final String stopPointName,
                                   @JsonProperty("StopAreaNumber") final String stopAreaNumber,
                                   @JsonProperty("LocationNorthingCoordinate") final String locationNorthingCoordinate,
                                   @JsonProperty("LocationEastingCoordinate") final String locationEastingCoordinate,
                                   @JsonProperty("ZoneShortName") final String zoneShortName,
                                   @JsonProperty("StopAreaTypeCode") final String stopAreaTypeCode,
                                   @JsonProperty("LastModifiedUtcDateTime") final String lastModifiedUtcDateTime,
                                   @JsonProperty("ExistsFromDate") final String existsFromDate) {
        return new StopPoint(stopPointNumber,
                             stopPointName,
                             stopAreaNumber,
                             locationNorthingCoordinate,
                             locationEastingCoordinate,
                             zoneShortName,
                             stopAreaTypeCode,
                             lastModifiedUtcDateTime,
                             existsFromDate);
    }
}
