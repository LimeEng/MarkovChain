package markov.util;

public interface RandomGenerator {

    long nextLong(long min, long max);

    int nextInt(int min, int max);

    default long nextLong(long max) {
        return nextLong(0, max);
    }

    default int nextInt(int max) {
        return nextInt(0, max);
    }

}
