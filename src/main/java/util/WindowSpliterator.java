package util;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class WindowSpliterator<T> implements Spliterator<List<T>> {

    private final Spliterator<T> source;
    private final int windowSize;

    public WindowSpliterator(Spliterator<T> source, int windowSize) {
        if (windowSize < 1) {
            throw new IllegalArgumentException("Window size may not be < 1");
        }
        if (source == null) {
            throw new NullPointerException("Spliterator may not be null");
        }
        this.source = source;
        this.windowSize = windowSize;
    }

    @Override
    public boolean tryAdvance(Consumer<? super List<T>> action) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Spliterator<List<T>> trySplit() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long estimateSize() {
        long size = source.estimateSize();
        if (size == 0) {
            return 0;
        } else if (size <= windowSize) {
            return 1;
        }
        return size - windowSize;
    }

    @Override
    public int characteristics() {
        return Spliterator.SIZED | Spliterator.ORDERED;
    }

}
