package org.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorkWithAPI {

    // Имитация API
    private static Integer simulatedAPICall(Integer number) {
        try {
            TimeUnit.MILLISECONDS.sleep(200 + (int) (Math.random() * 300));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return number * number;
    }

    // ExecutorService
    public static void processWithExecutorService(List<Integer> numbers) throws ExecutionException, InterruptedException {
        try (ExecutorService executorService = Executors.newFixedThreadPool(Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE)) {
            long startTime = System.currentTimeMillis();

            List<Future<Integer>> futures = numbers.stream()
                    .map(number -> executorService.submit(() -> simulatedAPICall(number)))
                    .toList();

            System.out.println("ExecutorService results: ");
            for (Future<Integer> future : futures) {
                System.out.println(future.get());
            }

            long endTime = System.currentTimeMillis();

            System.out.println("ExecutorService finished in " + (endTime - startTime) + " ms");
        }
    }

    // Метод для обработки с использованием Reactor-Core
    public static void processWithReactor(List<Integer> numbers) {
        // Фиксируем стартовое время
        long startTime = System.currentTimeMillis();

        // Создаём Flux из списка чисел
        Flux.fromIterable(numbers)
                // Преобразуем каждое число в Mono, обрабатываем асинхронно на elastic-потоке
                .flatMap(number -> Mono.fromCallable(() -> simulatedAPICall(number))  // Каждый элемент обрабатывается вызовом
                        .subscribeOn(Schedulers.boundedElastic()))  // Используем elastic scheduler для асинхронной обработки
                // Выводим результат для каждого элемента
                .doOnNext(result -> System.out.println("Reactor result: " + result))
                // После завершения обработки всех элементов фиксируем время
                .doOnComplete(() -> {
                    long endTime = System.currentTimeMillis();
                    System.out.println("Reactor processing finished in: " + (endTime - startTime) + "ms");
                })
                // blockLast() ожидает завершения обработки всех элементов
                .blockLast();
    }



    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Генерируем список чисел от 1 до 100
        List<Integer> numbers = IntStream.rangeClosed(1, 100).boxed().collect(Collectors.toList());

        // Обработка традиционным многопоточным методом
        System.out.println("Processing with ExecutorService:");
        processWithExecutorService(numbers);

        // Пауза для разделения вывода
        System.out.println("\nProcessing with Reactor-Core:");

        // Реактивная обработка
        processWithReactor(numbers);
    }
}
