package markov;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.codepoetics.protonpack.StreamUtils;

import markov.util.DefaultRandomGenerator;
import markov.util.RandomGenerator;

public class MarkovChain<T> {

    private final int order;
    private final Map<TokenSequence<T>, ProbabilityMapping<T>> matrix;

    public MarkovChain(int order) {
        if (order < 1) {
            throw new IllegalArgumentException("The order of the markov chain must be positive");
        }
        this.order = order;
        this.matrix = new HashMap<>();
    }

    public void add(Stream<T> source) {
        List<List<T>> slidingWindows = StreamUtils.windowed(source.sequential(), order)
                .collect(Collectors.toList());
        // Imagine the input text as circular. We must therefore add new lists
        // to the slidingWindows to simulate this
        for (int i = 1; i < order; i++) {
            List<T> lastList = slidingWindows.get(slidingWindows.size() - 1);
            List<T> firstList = slidingWindows.get(0);
            List<T> list = new ArrayList<>(lastList);
            list.remove(0);
            list.add(firstList.get(i - 1));
            slidingWindows.add(list);
        }
        for (int i = 0; i < slidingWindows.size(); i++) {
            List<T> window = slidingWindows.get(i);
            // System.out.println("Window: " + window);
            List<T> nextWindow = slidingWindows.get((i + 1) % slidingWindows.size());
            T followingValue = nextWindow.get(nextWindow.size() - 1);

            ProbabilityMapping<T> mapping = new ProbabilityMapping<>();
            mapping.add(followingValue);
            matrix.merge(new TokenSequence<>(window), mapping, ProbabilityMapping::merge);
        }
    }

    public Stream<T> stream() {
        return stream(new DefaultRandomGenerator());
    }

    public Stream<T> stream(RandomGenerator gen) {
        return stream(getRandomKey(gen), gen);
    }

    public Stream<T> stream(TokenSequence<T> start) {
        return stream(start, new DefaultRandomGenerator());
    }

    public Stream<T> stream(TokenSequence<T> start, RandomGenerator gen) {
        Stream<T> head = Stream.of(start.getTokens())
                .flatMap(List::stream);
        Stream<T> tail = Stream.generate(new RandomSupplier(start, gen));
        return Stream.concat(head, tail);
    }

    private TokenSequence<T> getRandomKey(RandomGenerator gen) {
        long index = gen.nextLong(matrix.size());
        Iterator<Entry<TokenSequence<T>, ProbabilityMapping<T>>> iter = matrix.entrySet()
                .iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return iter.next()
                .getKey();
    }

    public int getOrder() {
        return order;
    }

    public Map<TokenSequence<T>, ProbabilityMapping<T>> getMatrix() {
        return Collections.unmodifiableMap(matrix);
    }

    public void print() {
        matrix.entrySet()
                .stream()
                .map(MarkovChain::formatEntry)
                .forEach(System.out::println);
    }

    private static <K, V> String formatEntry(Entry<K, V> entry) {
        return entry.getKey() + " -> " + entry.getValue();
    }

    private class RandomSupplier implements Supplier<T> {

        private TokenSequence<T> sequence;
        private RandomGenerator gen;

        public RandomSupplier(TokenSequence<T> sequence, RandomGenerator gen) {
            this.sequence = sequence;
            this.gen = gen;
        }

        @Override
        public T get() {
            T next = matrix.get(sequence)
                    .getNextRandomly(gen);
            sequence = sequence.getNext(next);
            return next;
        }
    }
}
