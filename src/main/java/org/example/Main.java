package org.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Mono.empty();
        Flux.empty();


        Mono<Integer> mono = Mono.just(1);
        System.out.println(mono);
        mono.subscribe(System.out::println);

        System.out.println();
        Flux<Integer> flux = Flux.just(1, 2, 3);
        flux.subscribe(System.out::println);

        System.out.println();
        Flux<Integer> fluxFromMono = mono.flux();
        fluxFromMono.subscribe(System.out::println);

        Mono<Boolean> monoFromFlux = flux.any(element -> element.equals(2));
        monoFromFlux.subscribe(System.out::println);

        Mono<Integer> integerMonoFromFlux = flux.elementAt(0);
        integerMonoFromFlux.subscribe(System.out::println);


        Flux.range(1, 10).subscribe(System.out::println);

        Flux.fromIterable(Arrays.asList(1, 2, 4)).subscribe(System.out::println);

        System.out.println();

        Flux.generate(sink -> {
                            sink.next("Hello");
                        }
                )
                .delayElements(Duration.ofMillis(500))
                .take(5)
                .subscribe(System.out::println);

        Thread.sleep(4000L);
    }
}


class UserService {
    UUID id = UUID.randomUUID();

    private boolean userExists(UUID id) {
        return id != null && id.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    public Mono<String> findUserById(UUID id) {
        if (userExists(id)) {
            return Mono.just("User found");
        } else {
            return Mono.empty();
        }
    }
}