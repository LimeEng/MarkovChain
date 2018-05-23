package markov.util;

import java.util.Random;

public class SeededRandomGenerator implements RandomGenerator {

    private final Random rnd;

    public SeededRandomGenerator(long seed) {
        this.rnd = new Random(seed);
    }

    @Override
    public long nextLong(long min, long max) {
        return nextLong(rnd, max - min) + min;
    }

    @Override
    public int nextInt(int min, int max) {
        return rnd.nextInt(max - min) + min;
    }

    // https://codereview.stackexchange.com/questions/18727/extending-java-util-random-nextintint-into-nextlonglong
    private long nextLong(Random rnd, long bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        }
        if ((bound & -bound) == bound) { // bound is a power of 2
            return rnd.nextLong() & (bound - 1);
        }
        long bits;
        long val;
        do {
            bits = rnd.nextLong() & Long.MAX_VALUE;
            val = bits % bound;
        } while (bits - val + (bound - 1) < 0);
        return val;
    }
}
