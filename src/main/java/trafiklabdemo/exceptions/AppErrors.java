package trafiklabdemo.exceptions;

import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

@UtilityClass
public class AppErrors {

    public boolean notAppError(final Throwable t) {
        return !t.getClass().isAssignableFrom(ApplicationException.class);
    }

    public ApplicationException toResponseException(final Throwable t) {
        return new TLResponseException(t.getMessage());
    }

    public InternalErrorException newInternalError(final String message) {
        return new InternalErrorException(message);
    }

    public <T> Mono<T> newResponseException(final String message) {
        return Mono.error(new TLResponseException(message));
    }

    public <T> Mono<T> notFound(final String message) {
        return Mono.error(new NotFoundException(message));
    }


}
