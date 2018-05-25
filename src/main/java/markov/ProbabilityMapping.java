package markov;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import markov.util.RandomGenerator;

public class ProbabilityMapping<T> {
    private final Map<T, Long> counter;
    private long totalValues;

    /**
     * Constructs a new, empty, ProbabilityMapping
     */
    public ProbabilityMapping() {
        this.counter = new LinkedHashMap<>();
        this.totalValues = 0;
    }

    public void set(T item, long quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Cannot add a negative amount of items");
        }
        Long previous = counter.put(item, quantity);
        if (previous != null) {
            totalValues -= previous;
        }
        totalValues += quantity;
    }

    public long get(T item) {
        return counter.get(item);
    }

    public long getOrDefault(T item, Long otherwise) {
        return counter.getOrDefault(item, otherwise);
    }

    /**
     * Adds the specified item one time to this mapping.
     * 
     * @param item
     *            the item to add to this mapping
     */
    public void add(T item) {
        add(item, 1);
    }

    /**
     * Adds the specified item <em>n</em> times to this mapping. A specified
     * quantity of 0 does nothing.
     * 
     * @param item
     *            the item to add to this mapping
     * @param quantity
     *            how many copies of the specified item should be added
     * @throws IllegalArgumentException
     *             if quantity < 0
     */
    public void add(T item, long quantity) {
        if (quantity == 0) {
            return;
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Cannot add a negative amount of items");
        }
        counter.merge(item, quantity, Long::sum);
        totalValues += quantity;
    }

    /**
     * Merges this and the specified mapping and returns a new one. The merging
     * does not affect the two mappings used to create the third mapping.
     * 
     * @param mapping
     *            the mapping which should be merged with this
     * @return the newly constructed ProbabilityMapping
     */
    public ProbabilityMapping<T> merge(ProbabilityMapping<T> mapping) {
        Map<T, Long> map = merge(counter, mapping.counter, Long::sum);
        ProbabilityMapping<T> newMapping = new ProbabilityMapping<>();
        for (Entry<T, Long> entry : map.entrySet()) {
            newMapping.add(entry.getKey(), entry.getValue());
        }
        return newMapping;
    }

    private <K, V> Map<K, V> merge(Map<K, V> a, Map<K, V> b, BiFunction<? super V, ? super V, ? extends V> remapping) {
        return Stream.of(a, b)
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, remapping::apply));
    }

    /**
     * An element is randomly picked from this mapping, given the specified
     * random generator. The random selection is weighted, elements with a
     * higher quantity are more likely to be selected.
     * 
     * @param gen
     *            the random generator used for picking a random element
     * @throws IllegalStateException
     *             if the map is empty
     * @return a randomly chosen element
     */
    public T getNextRandomly(RandomGenerator gen) {
        if (totalValues == 0) {
            throw new IllegalStateException("Values must be added to the map before one can be chosen");
        }
        return getNextByIndex(gen.nextLong(totalValues));
    }

    private T getNextByIndex(long index) { // 0 based (of course)
        if (index >= totalValues || index < 0) {
            throw new IndexOutOfBoundsException("Out of bounds: Index = " + index + ", size = " + totalValues);
        }
        for (Entry<T, Long> entry : counter.entrySet()) {
            index -= entry.getValue();
            if (index < 0) {
                return entry.getKey();
            }
        }
        throw new RuntimeException("This should not have happened. I'm sorry");
        // Should never happen. Bounds checked earlier
    }

    /**
     * Returns a copy of the internal representation. Changes in the copy will
     * not reflect in the original, and vice versa.
     * 
     * @return a copy of the internal representation
     */
    public Map<T, Long> getMapping() {
        return counter.entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    /**
     * Returns the total count of all items added.
     * 
     * @return the total count of all items added
     */
    public long getTotalValues() {
        return totalValues;
    }

    @Override
    public int hashCode() {
        return counter.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ProbabilityMapping<?> other = (ProbabilityMapping<?>) obj;
        if (counter == null) {
            if (other.counter != null) {
                return false;
            }
        } else if (!counter.equals(other.counter)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return counter.entrySet()
                .stream()
                .map(ProbabilityMapping::formatEntry)
                .collect(Collectors.joining(" ", "[", "]"));
    }

    private static <K, V> String formatEntry(Entry<K, V> entry) {
        return "{" + entry.getKey() + " - " + entry.getValue() + "}";
    }
}
