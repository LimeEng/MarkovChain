package markov.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test_utils.TestUtility;

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
        TestUtility.shouldThrowException("nextInt not throwing exception", IllegalArgumentException.class,
                () -> gen.nextInt(10, 9));
        TestUtility.shouldThrowException("nextLong not throwing exception", IllegalArgumentException.class,
                () -> gen.nextLong(10, 9));
    }

    @Test
    public void testMinEqualToMax() {
        TestUtility.shouldThrowException("nextInt not throwing exception", IllegalArgumentException.class,
                () -> gen.nextInt(10, 10));
        TestUtility.shouldThrowException("nextLong not throwing exception", IllegalArgumentException.class,
                () -> gen.nextLong(10, 10));
    }

    @Test
    public void testMaxLessThanZero() {
        TestUtility.shouldThrowException("nextInt not throwing exception", IllegalArgumentException.class,
                () -> gen.nextInt(-1));
        TestUtility.shouldThrowException("nextLong not throwing exception", IllegalArgumentException.class,
                () -> gen.nextLong(-1));
    }
}
