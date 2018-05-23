package markov.util;

import java.util.concurrent.ThreadLocalRandom;

public class DefaultRandomGenerator implements RandomGenerator {

    @Override
    public long nextLong(long min, long max) {
        return ThreadLocalRandom.current()
                .nextLong(min, max);
    }

    @Override
    public int nextInt(int min, int max) {
        return ThreadLocalRandom.current()
                .nextInt(min, max);
    }

}
