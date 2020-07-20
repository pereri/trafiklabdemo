package trafiklabdemo.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import trafiklabdemo.client.model.BusLine;
import trafiklabdemo.client.model.JourneyPatternPointOnLine;
import trafiklabdemo.client.model.ResponseData;
import trafiklabdemo.client.model.StopPoint;
import trafiklabdemo.client.model.TLResponse;
import trafiklabdemo.exceptions.AppErrors;
import trafiklabdemo.exceptions.ApplicationException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@Qualifier("TrafiklabClientImpl")
public class TrafiklabClientImpl implements TrafiklabClient {
    private static final ParameterizedTypeReference<TLResponse<ResponseData.LinesResponseData>> BUS_LINES =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<TLResponse<ResponseData.JourneyPointsResponseData>> JOURNEY_POINTS =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<TLResponse<ResponseData.StopPointsResponseData>> BUS_STOPS =
            new ParameterizedTypeReference<>() {
            };

    private static final Map<String, String> BUS_LINES_ARGS = Map.of("model", "lines");
    private static final Map<String, String> JOURNEY_POINTS_ARGS = Map.of("model", "jour");
    private static final Map<String, String> BUS_STOPS_ARGS = Map.of("model", "stop");

    private final WebClient client;

    @Autowired
    public TrafiklabClientImpl(@Value("${apikey}") final String key,
                               @Value("${baseUrl}") final String baseUrl) {
        this(WebClient.builder()
                      .exchangeStrategies(ExchangeStrategies.builder()
                                                            .codecs(builder -> builder.defaultCodecs()
                                                                                      .maxInMemorySize(10 * 1024 * 1024))
                                                            .build())
                      .baseUrl(baseUrl)
                      .defaultUriVariables(Map.of("key", key, "code", "BUS"))
                      .filter(logRequest())
                      .build());
    }


    TrafiklabClientImpl(final WebClient client) {
        this.client = client;
    }


    private <T> Mono<T> get(final Map<String, String> args,
                            final ParameterizedTypeReference<T> asResponseType) {
        return client.get()
                     .uri(builder -> builder.build(args))
                     .retrieve()
                     .onStatus(HttpStatus::isError, this::handleHttpError)
                     .bodyToMono(asResponseType);
    }

    @Override
    public Mono<List<BusLine>> getBusLines() {
        return get(BUS_LINES_ARGS, BUS_LINES)
                .transform(this::handleResponseError);
    }

    @Override
    public Mono<List<JourneyPatternPointOnLine>> getJourneyPoints() {
        return get(JOURNEY_POINTS_ARGS, JOURNEY_POINTS)
                .transform(this::handleResponseError);
    }

    @Override
    public Mono<List<StopPoint>> getBusLineStops() {
        return get(BUS_STOPS_ARGS, BUS_STOPS)
                .transform(this::handleResponseError);
    }

    private Mono<ApplicationException> handleHttpError(final ClientResponse response) {
        return Mono.just(AppErrors.newInternalError("Bad response from backend: " + response.statusCode()));
    }

    private <U, T extends ResponseData<U>> Mono<List<U>> handleResponseError(final Mono<TLResponse<T>> tlResponse) {
        return tlResponse
                .onErrorMap(AppErrors::notAppError,AppErrors::toResponseException)
                .flatMap(response -> {
                    Integer statusCode = response.getStatusCode();
                    if (!Objects.equals(statusCode, 0)) {
                        return AppErrors.newResponseException("Invalid status code: " + statusCode + " ," + response.getMessage());
                    }
                    ResponseData<U> data = response.getResponseData();
                    if (Objects.isNull(data) || Objects.isNull(data.getResult())) {
                        return AppErrors.newResponseException("No data returned, code: " + statusCode + " ," + response.getMessage());
                    }
                    List<U> result = data.getResult();
                    return Mono.just(result);
                });
    }

    private static ExchangeFilterFunction logRequest() {
        ExchangeFilterFunction logRequest = ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.debug("Request{}: {} {}",
                     request.logPrefix(),
                     request.method(),
                     request.url());
            return Mono.just(request);
        });

        ExchangeFilterFunction logResponse = ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.debug("Response{}: {}",
                     response.logPrefix(),
                     response.statusCode());
            return Mono.just(response);
        });
        return logRequest.andThen(logResponse);

    }


}
