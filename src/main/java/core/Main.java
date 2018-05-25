package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.codepoetics.protonpack.Indexed;
import com.codepoetics.protonpack.StreamUtils;

import markov.MarkovChain;
import markov.util.RandomGenerator;
import markov.util.SeededRandomGenerator;

public class Main {

    private static final RandomGenerator gen = getGenerator(42);

    public static void main(String[] args) throws Exception {
        System.out.println("Reading...");

        Stream<String> keyStream0 = streamAndSplit("Bible.txt");
        Stream<String> keyStream1 = streamAndSplit("AliceInWonderland.txt");
        Stream<String> keyStream2 = streamAndSplit("TrumpSpeech.txt");
        Stream<String> keyStream3 = streamAndSplit("TrumpTweets.txt");

        Stream<String> textStream0 = streamAndSplit("Bible.txt");
        Stream<String> textStream1 = streamAndSplit("AliceInWonderland.txt");
        Stream<String> textStream2 = streamAndSplit("TrumpSpeech.txt");
        Stream<String> textStream3 = streamAndSplit("TrumpTweets.txt");

        // Stream<String> textStream0 = IntStream.rangeClosed(1, 9)
        // .boxed()
        // .map(String::valueOf);
        // Stream<String> textStream1 = IntStream.rangeClosed(1, 9)
        // .boxed()
        // .map(String::valueOf);
        // Stream<String> textStream2 = IntStream.rangeClosed(1, 9)
        // .boxed()
        // .map(String::valueOf);
        // Stream<String> textStream3 = IntStream.rangeClosed(1, 9)
        // .boxed()
        // .map(String::valueOf);

        MarkovChain<String> chain0 = new MarkovChain<>(2);
        MarkovChain<String> chain1 = new MarkovChain<>(2);
        MarkovChain<String> chain2 = new MarkovChain<>(2);
        MarkovChain<String> chain3 = new MarkovChain<>(2);

        chain0.add(textStream0);
        chain1.add(textStream1);
        chain2.add(textStream2);
        chain3.add(textStream3);

        MarkovChain<String> master = MarkovChain.merge(chain0, chain1, chain2, chain3);

        // Stream<String> textStream = IntStream.rangeClosed(1, 9)
        // .boxed()
        // .map(String::valueOf);

        System.out.println("Building...");
        MarkovChain<String> keyChain = new MarkovChain<>(2);
        keyChain.add(keyStream0);
        keyChain.add(keyStream1);
        keyChain.add(keyStream2);
        keyChain.add(keyStream3);
        System.out.println("Starting...");
        // chain.print();
        printStream(20, keyChain.stream(gen)
                .limit(200));
        System.out.println();
        printStream(20, master.stream(getGenerator(42))
                .limit(200));
        System.out.println("====");
        Set<?> expected = keyChain.getMatrix()
                .entrySet();
        Set<?> actual = master.getMatrix()
                .entrySet();
        for (Object entry : expected) {
            if (!actual.contains(entry)) {
                System.out.println(entry);
            }
        }
        System.out.println("===");
        for (Object entry : actual) {
            if (!expected.contains(entry)) {
                System.out.println(entry);
            }
        }

        // System.out.println("Writing to file...");
        // GraphMLConverter.convertToGraphML(chain, new File("temp.graphml"));
        // System.out.println("Done");
    }

    private static Stream<String> streamAndSplit(String... texts) {
        return Arrays.stream(String.join(" ", getTexts(texts))
                .split(" "))
                .filter(e -> !e.trim()
                        .isEmpty());
    }

    private static <T> void printStream(int linebreak, Stream<T> source) {
        StreamUtils.zipWithIndex(source)
                .forEach(e -> printIndexed(e, linebreak));
        System.out.println();
    }

    private static <T> void printIndexed(Indexed<T> indexed, int linebreak) {
        System.out.print(indexed.getValue() + " ");
        if (indexed.getIndex() % linebreak == 0 && indexed.getIndex() != 0) {
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
