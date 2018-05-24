package markov;

import java.util.ArrayList;
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

    /**
     * Constructs a new Markov chain of the specified order.
     * 
     * @param order
     *            the order of the Markov chain
     * @throws IllegalArgumentException
     *             if the specified order < 1
     */
    public MarkovChain(int order) {
        if (order < 1) {
            throw new IllegalArgumentException("The order of the markov chain must be positive");
        }
        this.order = order;
        this.matrix = new HashMap<>();
    }

    /**
     * Builds a transition matrix based on the specified source. Each element
     * is considered a token. The stream is also considered to be
     * <em>circular</em>, which means that the last element is considered to
     * precede the first element. This property guarantees that an infinite
     * stream can be created. Note that the stream must be held in memory when
     * building the matrix.
     * 
     * @param source
     *            the source of the input data.
     */
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

    /**
     * Returns an infinite stream representing a random walk through the
     * transition matrix. The stream starts with a random element.
     * 
     * @return an infinite stream
     */
    public Stream<T> stream() {
        return stream(new DefaultRandomGenerator());
    }

    /**
     * Returns an infinite stream representing a random walk through the
     * transition matrix, using the specified random generator. The stream
     * starts with a random element.
     * 
     * @param gen
     *            the random generator to use
     * @return an infinite stream
     */
    public Stream<T> stream(RandomGenerator gen) {
        return stream(getRandomKey(gen), gen);
    }

    /**
     * Returns an infinite stream representing a random walk through the
     * transition matrix. The stream starts with the specified TokenSequence.
     * 
     * @param start
     *            the starting TokenSequence
     * @return an infinite stream
     */
    public Stream<T> stream(TokenSequence<T> start) {
        return stream(start, new DefaultRandomGenerator());
    }

    /**
     * Returns an infinite stream representing a random walk through the
     * transition matrix, using the specified random generator. The stream
     * starts with the specified TokenSequence.
     * 
     * @param start
     *            the starting TokenSequence
     * @param gen
     *            the random generator to use
     * @return an infinite stream
     */
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

    /**
     * Returns the order of the Markov chain.
     * 
     * @return the order of the Markov chain.
     */
    public int getOrder() {
        return order;
    }

    /**
     * Returns a copy of the internal representation. Changes in the copy will
     * not reflect in the original, and vice versa.
     * 
     * @return a copy of the internal representation
     */
    public Map<TokenSequence<T>, ProbabilityMapping<T>> getMatrix() {
        return matrix.entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
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
