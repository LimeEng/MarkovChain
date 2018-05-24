package markov.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SeededRandomGeneratorTest extends RandomGeneratorTest {

    @Override
    protected SeededRandomGenerator createInstance() {
        return createInstance(42);
    }

    protected SeededRandomGenerator createInstance(long seed) {
        return new SeededRandomGenerator(seed);
    }

    @Test
    public void testGetSeed() {
        for (int i = 0; i < 100; i++) {
            SeededRandomGenerator gen = createInstance(i);
            assertEquals("getSeed does not return the seed that initialized the object", i, gen.getSeed());
        }
    }
}
