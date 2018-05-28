package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import markov.MarkovChain;
import markov.util.RandomGenerator;
import markov.util.SeededRandomGenerator;

public class Main {

    private static final RandomGenerator gen = getGenerator(42);

    public static void main(String[] args) throws Exception {
        System.out.println("Reading...");

        Stream<String> textStream = streamAndSplit("Bible.txt", "AliceInWonderland.txt", "TrumpSpeech.txt",
                "TrumpTweets.txt");

        System.out.println("Building...");
        MarkovChain<String> chain = new MarkovChain<>(2);
        chain.add(textStream);
        System.out.println("Starting...");
        // chain.print();
        printStream(20, chain.stream(gen)
                .limit(200));
    }

    private static Stream<String> streamAndSplit(String... texts) {
        return Arrays.stream(String.join(" ", getTexts(texts))
                .split(" "))
                .filter(e -> !e.trim()
                        .isEmpty());
    }

    private static <T> void printStream(int linebreak, Stream<T> source) {
        Stream<Long> numbers = LongStream.range(0, Long.MAX_VALUE)
                .boxed();
        Iterator<Long> iter = numbers.iterator();
        source.map(e -> new SimpleEntry<>(e, iter.next()))
                .forEach(e -> printIndexed(e, linebreak));
        System.out.println();
    }

    private static <T> void printIndexed(Entry<T, Long> indexed, int linebreak) {
        System.out.print(indexed.getKey() + " ");
        long index = indexed.getValue();
        if (index % linebreak == 0 && index != 0) {
            System.out.println();
        }
    }

    private static List<String> getTexts(String... paths) {
        return Arrays.stream(paths)
                .map(Paths::get)
                .map(Main::linesOf)
                .flatMap(Function.identity())
                .filter(Main::isNotEmpty)
                .collect(Collectors.toList());
    }

    private static Stream<String> linesOf(Path path) {
        try {
            return Files.lines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Stream.empty();
    }

    private static boolean isNotEmpty(String s) {
        return !s.isEmpty();
    }

    private static RandomGenerator getGenerator(long seed) {
        return new SeededRandomGenerator(seed);
    }
}
