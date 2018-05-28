package util;

import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WindowedStream {

    /**
     * Returns a sliding window in the form of a Stream of lists. The window
     * size specify the size of the lists
     *
     * @param source
     *            the original stream which will be partitioned into windows
     * @param windowSize
     *            the size of each window
     * @return a stream of equally sized windows
     */
    public static <T> Stream<List<T>> windowed(Stream<T> source, int windowSize) {
        Spliterator<List<T>> spliterator = new WindowSpliterator<>(source.spliterator(), windowSize);
        return StreamSupport.stream(spliterator, false)
                .onClose(source::close);
    }

}
