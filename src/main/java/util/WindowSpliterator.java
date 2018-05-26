package util;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class WindowSpliterator<T> implements Spliterator<List<T>> {

    private final Spliterator<T> source;
    private final int windowSize;

    public WindowSpliterator(Spliterator<T> source, int windowSize) {
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
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int characteristics() {
        // TODO Auto-generated method stub
        return 0;
    }

}
