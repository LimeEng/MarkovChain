package markov.util;

import static org.junit.Assert.assertTrue;

import java.util.function.Supplier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class RandomGeneratorTest {

    protected RandomGenerator gen;

    protected abstract RandomGenerator createInstance();

    @Before
    public void setUp() {
        gen = createInstance();
    }

    @After
    public void tearDown() {
        gen = null;
    }

    @Test
    public void testMinLargerThanMax() {
        shouldThrowIllegalArgumentException("nextInt not throwing exception", () -> gen.nextInt(10, 9));
        shouldThrowIllegalArgumentException("nextLong not throwing exception", () -> gen.nextLong(10, 9));
    }

    @Test
    public void testMinEqualToMax() {
        shouldThrowIllegalArgumentException("nextInt not throwing exception", () -> gen.nextInt(10, 10));
        shouldThrowIllegalArgumentException("nextLong not throwing exception", () -> gen.nextLong(10, 10));
    }

    @Test
    public void testMaxLessThanZero() {
        shouldThrowIllegalArgumentException("nextInt not throwing exception", () -> gen.nextInt(-1));
        shouldThrowIllegalArgumentException("nextLong not throwing exception", () -> gen.nextLong(-1));
    }

    private void shouldThrowIllegalArgumentException(String message, Supplier<Number> supplier) {
        boolean exceptionThrown = false;
        try {
            supplier.get();
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(message, exceptionThrown);
    }
}
