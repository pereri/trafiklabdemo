package trafiklabdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@RestController()
@RequestMapping("api")
public class BusController {

    final TrafiklabService service;

    @Autowired
    public BusController(TrafiklabService service) {
        this.service = service;
    }

    @GetMapping("lines")
    public Mono<List<Api.BusLine>> lines() {
        return service.getTopBusLinesByNumberOfStops()
                      .flatMapIterable(Function.identity())
                      .map(Api::toApiModel)
                      .collectList();
    }

    @GetMapping("stops/{lineNumber}")
    public Mono<List<Api.StopPoint>> lines(@PathVariable("lineNumber") String lineNumber) {
        return service.getStopsForLine(lineNumber)
                      .flatMapIterable(Function.identity())
                      .map(Api::toApiModel)
                      .collectList();
    }
}
