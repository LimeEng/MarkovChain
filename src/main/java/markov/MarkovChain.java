package markov;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import markov.util.DefaultRandomGenerator;
import markov.util.RandomGenerator;
import util.WindowedStream;

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
        this(order, new HashMap<>());
    }

    private MarkovChain(int order, Map<TokenSequence<T>, ProbabilityMapping<T>> matrix) {
        if (order < 1) {
            throw new IllegalArgumentException("The order of the markov chain must be positive");
        }
        this.order = order;
        this.matrix = matrix;
    }

    /**
     * Builds a transition matrix based on the specified source. Each element is
     * considered a token. The stream is also considered to be
     * <em>circular</em>, which means that the last element is considered to
     * precede the first element. This property guarantees that an infinite
     * stream can be created. Note that the stream must be held in memory when
     * building the matrix.
     * 
     * @param source
     *            the source of the input data.
     */
    public void add(Stream<T> source) {
        List<List<T>> slidingWindows = WindowedStream.windowed(source.sequential(), order)
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
            List<T> nextWindow = slidingWindows.get((i + 1) % slidingWindows.size());
            T followingValue = nextWindow.get(nextWindow.size() - 1);

            ProbabilityMapping<T> mapping = new ProbabilityMapping<>();
            mapping.add(followingValue);
            matrix.merge(new TokenSequence<>(window), mapping, ProbabilityMapping::merge);
        }
    }

    /**
     * Merges the specified Markov chains and returns a new Markov chains. All
     * chains are equally weighted
     * 
     * @param chains
     *            the Markov chains to be merged
     * @return a new Markov chains consisting of the specified chains
     * @throws NullPointerException
     *             if any input argument is null
     * @throws IllegalArgumentException
     *             if all Markov chains does not have the same order
     */
    @SafeVarargs
    public static <T> MarkovChain<T> merge(MarkovChain<T>... chains) {
        return merge(Arrays.asList(chains));
    }

    /**
     * Merges the specified Markov chains and returns a new Markov chains. All
     * chains are equally weighted
     * 
     * @param chains
     *            the Markov chains to be merged
     * @return a new Markov chains consisting of the specified chains
     * @throws NullPointerException
     *             if any input argument is null
     * @throws IllegalArgumentException
     *             if all Markov chains does not have the same order
     */
    public static <T> MarkovChain<T> merge(Collection<MarkovChain<T>> chains) {
        return merge(chains, Collections.nCopies(chains.size(), 1));
    }

    /**
     * 
     * Merges the specified Markov chains and returns a new Markov chains. The
     * weights specifiy how much relative emphasis to place on each chain as the
     * new one is built.
     * 
     * @param chains
     *            the Markov chains to be merged
     * @param weights
     *            the relative emphasis to place on each chain as they are
     *            merged into a new chain
     * @return a new Markov chains consisting of the specified chains, weighted
     *         as specified
     * @throws NullPointerException
     *             if any input argument is null
     * @throws IllegalArgumentException
     *             if the length of the input arguments do not match
     * @throws IllegalArgumentException
     *             if all Markov chains does not have the same order
     */
    public static <T> MarkovChain<T> merge(Collection<MarkovChain<T>> chains, List<Integer> weights) {
        if (weights == null || chains == null) {
            throw new NullPointerException("Null items not allowed");
        }
        if (chains.size() != weights.size()) {
            throw new IllegalArgumentException("The length of the input arguments must match");
        }
        long uniqueOrders = chains.stream()
                .mapToInt(MarkovChain::getOrder)
                .distinct()
                .count();
        if (uniqueOrders != 1) {
            throw new IllegalArgumentException("All Markov chains must be of the same order");
        }
        int order = chains.stream()
                .mapToInt(MarkovChain::getOrder)
                .findAny()
                .getAsInt();

        List<Map<TokenSequence<T>, ProbabilityMapping<T>>> models = chains.stream()
                .map(MarkovChain::getMatrix)
                .collect(Collectors.toList());

        Map<TokenSequence<T>, ProbabilityMapping<T>> mergedMap = new HashMap<>();

        for (int i = 0; i < models.size(); i++) {
            Map<TokenSequence<T>, ProbabilityMapping<T>> model = models.get(i);
            int weight = weights.get(i);
            for (Entry<TokenSequence<T>, ProbabilityMapping<T>> entry : model.entrySet()) {
                TokenSequence<T> sequence = entry.getKey();
                ProbabilityMapping<T> mapping = entry.getValue();
                ProbabilityMapping<T> current = mergedMap.getOrDefault(sequence, new ProbabilityMapping<>());
                for (Entry<T, Long> e : mapping.getMapping()
                        .entrySet()) {
                    T nextToken = e.getKey();
                    long nextTokenValue = e.getValue();
                    long previous = current.getMapping()
                            .getOrDefault(nextToken, 0L);
                    current.set(nextToken, previous + (nextTokenValue * weight));
                }
                mergedMap.put(sequence, current);
            }
        }

        MarkovChain<T> mergedChain = new MarkovChain<>(order, mergedMap);
        return mergedChain;
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

    /**
     * Returns the next element, given the specified starting sequence.
     * 
     * @param start
     *            the starting TokenSequence
     * @return the next element
     */
    public T getNextRandomly(TokenSequence<T> start) {
        return getNextRandomly(start, new DefaultRandomGenerator());
    }

    /**
     * Returns the next element, given the specified starting sequence and
     * random generator.
     * 
     * @param start
     *            the starting TokenSequence
     * @param gen
     *            the random generator to use
     * @return the next element
     */
    public T getNextRandomly(TokenSequence<T> start, RandomGenerator gen) {
        return matrix.get(start)
                .getNextRandomly(gen);
    }

    private TokenSequence<T> getRandomKey(RandomGenerator gen) {
        int index = gen.nextInt(matrix.size());
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
            T next = getNextRandomly(sequence, gen);
            sequence = sequence.getNext(next);
            return next;
        }
    }
}
