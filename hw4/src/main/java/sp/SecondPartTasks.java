package sp;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class SecondPartTasks {

    private SecondPartTasks() {}

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        return paths.parallelStream().flatMap(
                path -> {
                            try {
                                return Files.readAllLines(
                                        Paths.get(path),
                                        Charset.defaultCharset()
                                ).parallelStream();
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        }
                )
                .filter(s -> s.contains(sequence))
                .collect(Collectors.toList());
    }

    // В квадрат с длиной стороны 1 вписана мишень.
    // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
    // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать, какова вероятность попасть в мишень.
    public static double piDividedBy4() {
        Random rand = new Random();
        final int cnt = (int)1e5;

        long hit = Stream.generate(() ->
                Math.pow(rand.nextDouble() - 0.5, 2) + Math.pow(rand.nextDouble() - 0.5, 2) <= 0.25
        ).limit(cnt).filter(b -> b).count();
        return (double) hit / cnt;
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) {
        Optional<Map.Entry<String, List<String>>> max = compositions.entrySet().stream().max(
                (lhs, rhs) -> sumLength(lhs.getValue().stream()) - sumLength(rhs.getValue().stream())
        );

        return max.isPresent() ? max.get().getKey() : null;
    }

    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        return orders.stream().flatMap(m -> m.entrySet().stream())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.summingInt(Map.Entry::getValue))
                );
    }

    private static int sumLength(Stream<String> sStream) {
        return sStream.mapToInt(String::length).sum();
    }
}
