package util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class WindowSpliterator<T> implements Spliterator<List<T>> {

    private final Spliterator<T> source;
    private final int windowSize;

    private final Deque<T> nextWindow;

    boolean initalized = false;

    public WindowSpliterator(Spliterator<T> source, int windowSize) {
        if (windowSize < 1) {
            throw new IllegalArgumentException("Window size may not be < 1");
        }
        if (source == null) {
            throw new NullPointerException("Spliterator may not be null");
        }
        this.source = source;
        this.windowSize = windowSize;
        this.nextWindow = new ArrayDeque<>(windowSize);
    }

    @Override
    public boolean tryAdvance(Consumer<? super List<T>> action) {
        if (hasNext()) {
            action.accept(getNextWindow());
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<List<T>> trySplit() {
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
        return source.characteristics() & ~(Spliterator.SIZED | Spliterator.ORDERED);
    }

    private void nextWindow() {
        if (nextWindow.isEmpty()) {
            return;
        }
        nextWindow.removeFirst();
        source.tryAdvance(nextWindow::offer);
    }

    private void fillInitialWindow() {
        for (int i = windowSize; i > 0; i--) {
            if (!source.tryAdvance(nextWindow::offer)) {
                return;
            }
        }
    }

    private boolean hasNext() {
        if (!initalized) {
            fillInitialWindow();
            initalized = true;
        }
        return !nextWindow.isEmpty();
    }

    private List<T> getNextWindow() {
        List<T> window = new LinkedList<>(nextWindow);
        nextWindow();
        if (nextWindow.size() != windowSize) {
            nextWindow.clear();
        }

        return window;
    }

}
