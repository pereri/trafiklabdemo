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
    //LocalDateTime lastModifiedUtcDateTime;
    String lastModifiedUtcDateTime;
    //LocalDateTime existsFromDate;
    String existsFromDate;

    @JsonCreator
    public static StopPoint create(@JsonProperty("StopPointNumber") String stopPointNumber,
                                   @JsonProperty("StopPointName") String stopPointName,
                                   @JsonProperty("StopAreaNumber") String stopAreaNumber,
                                   @JsonProperty("LocationNorthingCoordinate") String locationNorthingCoordinate,
                                   @JsonProperty("LocationEastingCoordinate") String locationEastingCoordinate,
                                   @JsonProperty("ZoneShortName") String zoneShortName,
                                   @JsonProperty("StopAreaTypeCode") String stopAreaTypeCode,
                                   @JsonProperty("LastModifiedUtcDateTime") String lastModifiedUtcDateTime,
                                   @JsonProperty("ExistsFromDate") String existsFromDate) {
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
