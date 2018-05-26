package test_utils;

import static org.junit.Assert.assertTrue;

public class TestUtility {

    public static void shouldThrowException(String message, Class<? extends Exception> ex, Block block) {
        boolean exceptionThrown = false;
        try {
            block.run();
        } catch (Exception e) {
            if (e.getClass().equals(ex)) {
                exceptionThrown = true;
            }
        }
        assertTrue(message, exceptionThrown);
    }

}
