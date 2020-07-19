package trafiklabdemo.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusLine {
    String lineNumber;
    String lineDesignation;
    String defaultTransportMode;
    String defaultTransportModeCode;
    //LocalDateTime lastModifiedUtcDateTime;
    String lastModifiedUtcDateTime;
    //LocalDateTime existsFromDate;
    String existsFromDate;

    @JsonCreator
    public static BusLine create(@JsonProperty("LineNumber") final String lineNumber,
                                 @JsonProperty("LineDesignation") final String lineDesignation,
                                 @JsonProperty("DefaultTransportMode") final String defaultTransportMode,
                                 @JsonProperty("DefaultTransportModeCode") final String defaultTransportModeCode,
                                 @JsonProperty("LastModifiedUtcDateTime") final String lastModifiedUtcDateTime,
                                 @JsonProperty("ExistsFromDate") final String existsFromDate) {
        return new BusLine(lineNumber,
                           lineDesignation,
                           defaultTransportMode,
                           defaultTransportModeCode,
                           lastModifiedUtcDateTime,
                           existsFromDate);
    }
}
