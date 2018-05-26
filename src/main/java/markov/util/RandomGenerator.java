package markov.util;

public interface RandomGenerator {

    /**
     * Returns a pseudorandom long value between the specified min (inclusive)
     * and the specified bound (exclusive).
     *
     * @param min
     *            the least value returned
     * @param max
     *            the upper bound (exclusive)
     * @return a pseudorandom long value between the min (inclusive) and max
     *         (exclusive)
     * @throws IllegalArgumentException
     *             if min is greater than or equal to max
     */
    long nextLong(long min, long max);

    /**
     * Returns a pseudorandom int value between the specified min (inclusive)
     * and the specified bound (exclusive).
     *
     * @param min
     *            the least value returned
     * @param max
     *            the upper bound (exclusive)
     * @return a pseudorandom int value between the min (inclusive) and max
     *         (exclusive)
     * @throws IllegalArgumentException
     *             if min is greater than or equal to max
     */
    int nextInt(int min, int max);

    /**
     * Returns a pseudorandom long value between 0 (inclusive) and the specified
     * bound (exclusive).
     *
     * @param max
     *            the upper bound (exclusive)
     * @return a pseudorandom long value between 0 (inclusive) and max
     *         (exclusive)
     * @throws IllegalArgumentException
     *             if 0 is greater than or equal to max
     */
    default long nextLong(long max) {
        return nextLong(0, max);
    }

    /**
     * Returns a pseudorandom int value between 0 (inclusive) and the specified
     * bound (exclusive).
     *
     * @param max
     *            the upper bound (exclusive)
     * @return a pseudorandom int value between 0 (inclusive) and max
     *         (exclusive)
     * @throws IllegalArgumentException
     *             if 0 is greater than or equal to max
     */
    default int nextInt(int max) {
        return nextInt(0, max);
    }

}
