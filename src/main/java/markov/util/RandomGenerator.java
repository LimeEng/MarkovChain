package markov.util;

public interface RandomGenerator {

    /**
     * Returns a pseudorandom long value between the specified origin
     * (inclusive) and the specified bound (exclusive).
     *
     * @param origin
     *            the least value returned
     * @param bound
     *            the upper bound (exclusive)
     * @return a pseudorandom long value between the origin (inclusive) and the
     *         bound (exclusive)
     * @throws IllegalArgumentException
     *             if origin is greater than or equal to bound
     */
    long nextLong(long min, long max);

    /**
     * Returns a pseudorandom int value between the specified origin (inclusive)
     * and the specified bound (exclusive).
     *
     * @param origin
     *            the least value returned
     * @param bound
     *            the upper bound (exclusive)
     * @return a pseudorandom int value between the origin (inclusive) and the
     *         bound (exclusive)
     * @throws IllegalArgumentException
     *             if origin is greater than or equal to bound
     */
    int nextInt(int min, int max);

    /**
     * Returns a pseudorandom long value between 0 (inclusive) and the specified
     * bound (exclusive).
     *
     * @param max
     *            the upper bound (exclusive)
     * @return a pseudorandom long value between 0 (inclusive) and the bound
     *         (exclusive)
     * @throws IllegalArgumentException
     *             if origin is greater than or equal to bound
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
     * @return a pseudorandom int value between 0 (inclusive) and the bound
     *         (exclusive)
     * @throws IllegalArgumentException
     *             if origin is greater than or equal to bound
     */
    default int nextInt(int max) {
        return nextInt(0, max);
    }

}
