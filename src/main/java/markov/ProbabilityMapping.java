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

    public ProbabilityMapping() {
        this.counter = new LinkedHashMap<>();
        this.totalValues = 0;
    }

    public void add(T item) {
        add(item, 1);
    }

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

    public T getNextRandomly(RandomGenerator gen) {
        if (totalValues == 0) {
            throw new IllegalStateException("Values must be added to the map before one can be chosen");
        }
        return getNextByIndex(gen.nextLong(totalValues));
    }

    private T getNextByIndex(long index) { // 0 based (of course)
        if (index >= totalValues) {
            throw new IllegalArgumentException("That many values does not exist (" + index + ")");
        }
        for (T key : counter.keySet()) {
            index -= counter.get(key);
            if (index < 0) {
                return key;
            }
        }
        throw new RuntimeException("This should not have happened. I'm sorry");
        // Should never happen. Bounds checked earlier
    }

    public Map<T, Long> getMapping() {
        return counter.entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

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
