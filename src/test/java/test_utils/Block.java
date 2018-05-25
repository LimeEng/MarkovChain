package test_utils;

/**
 * Utility interface for representing a block of code to be executed, while also
 * throwing exceptions
 */
@FunctionalInterface
public interface Block {

    void run() throws Exception;

}
