package trafiklabdemo.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TLResponse<T extends ResponseData<?>> {
    Integer statusCode;
    String message;
    Long executionTime;
    T responseData;

    @JsonCreator
    public static <T extends ResponseData<?>> TLResponse<T> create(@JsonProperty("StatusCode") final Integer statusCode,
                                                                   @JsonProperty("Message") final String message,
                                                                   @JsonProperty("ExecutionTime") final Long executionTime,
                                                                   @JsonProperty("ResponseData") final T responseData) {
        return new TLResponse<>(statusCode, message, executionTime, responseData);
    }
}
