package trafiklabdemo.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JourneyPatternPointOnLine {
    String lineNumber;
    String directionCode;
    String journeyPatternPointNumber;
    String lastModifiedUtcDateTime;
    String existsFromDate;

    @JsonCreator
    public static JourneyPatternPointOnLine create(@JsonProperty("LineNumber") final String lineNumber,
                                                   @JsonProperty("DirectionCode") final String directionCode,
                                                   @JsonProperty("JourneyPatternPointNumber") final String journeyPatternPointNumber,
                                                   @JsonProperty("LastModifiedUtcDateTime") final String lastModifiedUtcDateTime,
                                                   @JsonProperty("ExistsFromDate") final String existsFromDate) {
        return new JourneyPatternPointOnLine(lineNumber,
                                             directionCode,
                                             journeyPatternPointNumber,
                                             lastModifiedUtcDateTime,
                                             existsFromDate);
    }
}
