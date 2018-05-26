package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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

        Stream<String> textStream = streamAndSplit("Bible.txt", "AliceInWonderland.txt", "TrumpSpeech.txt",
                "TrumpTweets.txt");

        System.out.println("Building...");
        MarkovChain<String> chain = new MarkovChain<>(2);
        chain.add(textStream);
        System.out.println("Starting...");
        // chain.print();
        printStream(20, chain.stream(gen)
                .limit(200));

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
