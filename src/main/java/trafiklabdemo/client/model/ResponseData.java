package trafiklabdemo.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class ResponseData<T> {
    private final String version;
    private final String type;
    private final List<T> result;

    ResponseData(final String version,
                 final String type,
                 final List<T> result) {
        this.version = version;
        this.type = type;
        this.result = result;
    }

    public static class LineList extends ArrayList<BusLine> {
    }

    public static class LinesResponseData extends ResponseData<BusLine> {
        public LinesResponseData(final String version,
                                 final String type,
                                 final List<BusLine> result) {
            super(version, type, result);
        }

        @JsonCreator
        public static LinesResponseData create(@JsonProperty("Version") final String version,
                                               @JsonProperty("Type") final String type,
                                               @JsonProperty("Result") final LineList result) {
            return new LinesResponseData(version, type, result);
        }
    }

    public static class JourneyPointsList extends ArrayList<JourneyPatternPointOnLine> {
    }

    public static class JourneyPointsResponseData extends ResponseData<JourneyPatternPointOnLine> {
        public JourneyPointsResponseData(final String version,
                                         final String type,
                                         final List<JourneyPatternPointOnLine> result) {
            super(version, type, result);
        }

        @JsonCreator
        public static JourneyPointsResponseData create(@JsonProperty("Version") final String version,
                                                       @JsonProperty("Type") final String type,
                                                       @JsonProperty("Result") final JourneyPointsList result) {
            return new JourneyPointsResponseData(version, type, result);
        }
    }

    public static class StopPointList extends ArrayList<StopPoint> {
    }

    public static class StopPointsResponseData extends ResponseData<StopPoint> {
        public StopPointsResponseData(final String version,
                                      final String type,
                                      final List<StopPoint> result) {
            super(version, type, result);
        }

        @JsonCreator
        public static StopPointsResponseData create(@JsonProperty("Version") final String version,
                                                    @JsonProperty("Type") final String type,
                                                    @JsonProperty("Result") final StopPointList result) {
            return new StopPointsResponseData(version, type, result);
        }
    }
}