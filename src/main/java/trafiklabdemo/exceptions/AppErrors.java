package trafiklabdemo.exceptions;

import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

@UtilityClass
public class AppErrors {

    public boolean notAppError(Throwable t) {
        return !t.getClass().isAssignableFrom(ApplicationException.class);
    }

    public ApplicationException toResponseException(Throwable t) {
        return new TLResponseException(t.getMessage());
    }

    public <T> Mono<T> newResponseException(String message) {
        return Mono.error(new TLResponseException(message));
    }

    public <T> Mono<T> notFound(String message) {
        return Mono.error(new NotFoundException(message));
    }


}
