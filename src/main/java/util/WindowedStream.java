package util;

import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WindowedStream {

    public static <T> Stream<List<T>> windowed(Stream<T> source, int windowSize) {
        Spliterator<List<T>> spliterator = new WindowSpliterator<>(source.spliterator(), windowSize);
        return StreamSupport.stream(spliterator, false)
                .onClose(source::close);
    }

}
