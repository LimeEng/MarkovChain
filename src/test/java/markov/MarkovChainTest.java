package markov;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

import markov.MarkovChain;
import markov.TokenSequence;
import markov.util.RandomGenerator;
import markov.util.SeededRandomGenerator;

public class MarkovChainTest {

    private static final RandomGenerator gen = new SeededRandomGenerator(42);

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

    private MarkovChain<Integer> createChain(int order) {
        return new MarkovChain<>(order);
    }
}
