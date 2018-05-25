package markov;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

import markov.util.RandomGenerator;
import markov.util.SeededRandomGenerator;

public class MarkovChainTest {

    private final RandomGenerator gen = createGenerator(42);

    @Test
    public void testOrderZero() {
        boolean exceptionThrown = false;
        try {
            MarkovChain<Integer> chain = createChain(0);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testOrderNegative() {
        boolean exceptionThrown = false;
        try {
            MarkovChain<Integer> chain = createChain(-1);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testSingleElementInput() {
        MarkovChain<Integer> chain = createChain(2);
        chain.add(Stream.of(1));
        List<Integer> actual = chain.stream()
                .limit(10)
                .collect(Collectors.toList());
        List<Integer> expected = Stream.generate(() -> 1)
                .limit(10)
                .collect(Collectors.toList());
        assertEquals(expected, actual);
    }

    @Test
    public void testMergeWithChainsOfDifferentOrder() {
        shouldThrowIllegalArgumentException("Did not throw a IllegalArgumentException",
                () -> MarkovChain.merge(createChain(2), createChain(2), createChain(3)));
    }

    @Test
    public void testMergeWithChainsAndWeightsOfDifferentLength() {
        List<MarkovChain<Integer>> chains = new ArrayList<>(
                Arrays.asList(createChain(2), createChain(2), createChain(2), createChain(2)));
        List<Integer> weights = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        shouldThrowIllegalArgumentException("Did not throw a IllegalArgumentException",
                () -> MarkovChain.merge(chains, weights));
        weights.remove(0);
        weights.remove(0);
        shouldThrowIllegalArgumentException("Did not throw a IllegalArgumentException",
                () -> MarkovChain.merge(chains, weights));
    }

    @Test
    public void testMergeWithNullArguments() {
        List<MarkovChain<Integer>> chains = new ArrayList<>(
                Arrays.asList(createChain(2), createChain(2), createChain(2)));
        List<Integer> weights = new ArrayList<>(Arrays.asList(1, 2, 3));
        shouldThrowNullPointerException("Did not throw a NullPointerException", () -> MarkovChain.merge(null, weights));
        shouldThrowNullPointerException("Did not throw a NullPointerException", () -> MarkovChain.merge(chains, null));
        shouldThrowNullPointerException("Did not throw a NullPointerException", () -> MarkovChain.merge(null, null));
    }

    @Test
    public void testBasicMergeWithoutWeights() {

        List<Integer> list1 = randomStream(gen, 0, 10).limit(1000)
                .collect(Collectors.toList());
        List<Integer> list2 = randomStream(gen, 0, 10).limit(1000)
                .collect(Collectors.toList());
        List<Integer> list3 = randomStream(gen, 0, 10).limit(1000)
                .collect(Collectors.toList());

        MarkovChain<Integer> reference = createChain(2);
        reference.add(list1.stream());
        reference.add(list2.stream());
        reference.add(list3.stream());

        MarkovChain<Integer> chain1 = createChain(2);
        MarkovChain<Integer> chain2 = createChain(2);
        MarkovChain<Integer> chain3 = createChain(2);

        chain1.add(list1.stream());
        chain2.add(list2.stream());
        chain3.add(list3.stream());
        MarkovChain<Integer> combined = MarkovChain.merge(chain1, chain2, chain3);

        Set<?> expected = reference.getMatrix()
                .entrySet();
        Set<?> actual = combined.getMatrix()
                .entrySet();
        assertEquals("Merging Markov chains does not match an equivalent \"unmerged\" Markov chain ", expected, actual);
    }

    @Test
    public void testBasicNthChain() {
        int minOrder = 1;
        int maxOrder = 10;
        for (int i = minOrder; i <= maxOrder; i++) {
            List<Integer> key = IntStream.range(0, i)
                    .boxed()
                    .collect(Collectors.toList());
            testNthOrderChain(i, 0, 100, new TokenSequence<>(key));
        }
    }

    private void testNthOrderChain(int order, int min, int max, TokenSequence<Integer> start) {
        MarkovChain<Integer> chain = createChain(order);
        assertEquals("The order of the markov chain is not correct", order, chain.getOrder());

        int nbrOfItems = max - min;
        Stream<Integer> input = IntStream.range(min, max)
                .boxed();
        chain.add(input);
        List<Integer> actual = chain.stream(start, gen)
                .limit(nbrOfItems * 2)
                .collect(Collectors.toList());
        List<Integer> expected = IntStream.concat(IntStream.range(min, max), IntStream.range(min, max))
                .boxed()
                .collect(Collectors.toList());
        assertEquals("The generated stream does not match the expected list", expected, actual);
    }

    private Stream<Integer> randomStream(RandomGenerator gen, int min, int max) {
        return IntStream.generate(() -> gen.nextInt(min, max))
                .boxed();
    }

    private MarkovChain<Integer> createChain(int order) {
        return new MarkovChain<>(order);
    }

    private RandomGenerator createGenerator(long seed) {
        return new SeededRandomGenerator(seed);
    }

    private void shouldThrowIllegalArgumentException(String message, Supplier<?> supplier) {
        boolean exceptionThrown = false;
        try {
            supplier.get();
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(message, exceptionThrown);
    }

    private void shouldThrowNullPointerException(String message, Supplier<?> supplier) {
        boolean exceptionThrown = false;
        try {
            supplier.get();
        } catch (NullPointerException e) {
            exceptionThrown = true;
        }
        assertTrue(message, exceptionThrown);
    }
}
