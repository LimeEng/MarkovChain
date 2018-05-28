package util;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;

import test_utils.TestUtility;

public class WindowedStreamTest {

    @Test
    public void testWithNullStream() {
        TestUtility.shouldThrowException("Null stream not throwing exception", NullPointerException.class,
                () -> WindowedStream.windowed(null, -1));
    }

    @Test
    public void testWindowSizeNegative() {
        TestUtility.shouldThrowException("Negative window size not throwing exception", IllegalArgumentException.class,
                () -> WindowedStream.windowed(IntStream.range(1, 10)
                        .boxed(), -1));
    }

    @Test
    public void testWindowSizeZero() {
        TestUtility.shouldThrowException("A window size of zero not throwing exception", IllegalArgumentException.class,
                () -> WindowedStream.windowed(IntStream.range(1, 10)
                        .boxed(), 0));
    }

    @Test
    public void testWindowSizeNth() {
        for (int i = 1; i <= 20; i++) {
            List<Integer> source = IntStream.range(0, 1000)
                    .boxed()
                    .collect(Collectors.toList());
            testWindowSizeNth(source, i);
        }
    }

    private void testWindowSizeNth(List<Integer> source, int size) {
        List<List<Integer>> windows = WindowedStream.windowed(source.stream(), size)
                .collect(Collectors.toList());
        for (List<Integer> window : windows) {
            System.out.println(size + ": " + window.size());
            assertEquals("Size of window not matching parameter", size, window.size());
        }
        long elements = windows.stream()
                .flatMap(List::stream)
                .count();
        assertEquals("Number of items in stream not matching", size * source.size(), elements);
    }
}
