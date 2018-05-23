package markov;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class TokenSequenceTest {

    @Test
    public void testVarargsConstructor() {
        TokenSequence<Integer> seq = new TokenSequence<>(1, 2, 3, 4);
        assertEquals("The tokens differ from the varargs constructor", Arrays.asList(1, 2, 3, 4), seq.getTokens());
    }

    @Test
    public void testCollectionConstructor() {
        TokenSequence<Integer> seq = new TokenSequence<>(1, 2, 3, 4);
        List<Integer> expected = Arrays.asList(1, 2, 3, 4);
        assertEquals("The tokens differ from the varargs constructor", expected, seq.getTokens());
    }

    @Test
    public void testGetNext() {
        TokenSequence<Integer> seq = new TokenSequence<>(1, 2, 3);
        for (int i = 4; i < 100; i++) {
            TokenSequence<Integer> next = seq.getNext(i);
            assertFalse(seq.equals(next));
            assertFalse(seq.hashCode() == next.hashCode());
            assertNotEquals(seq.getTokens(), next.getTokens());
        }
    }

    @Test
    public void testGetTokensUnmodifiable() {
        TokenSequence<Integer> seq = new TokenSequence<>(1, 2, 3);
        List<Integer> modified = seq.getTokens();
        modified.remove(0);
        assertNotEquals("Modifying the token list outside the object modifies the internal representation", modified,
                seq.getTokens());
    }

    @Test
    public void testVariousSimpleEquals() {
        TokenSequence<Integer> seq1 = new TokenSequence<>(1, 2, 3);
        TokenSequence<Integer> seq2 = new TokenSequence<>(1, 2, 3);
        TokenSequence<Integer> seq3 = new TokenSequence<>(2, 3, 4);
        assertTrue(seq1.equals(seq1));
        assertTrue(seq1.equals(seq2));
        assertFalse(seq1.equals(seq3));
        assertFalse(seq1.equals(null));
        assertFalse(seq1.equals("Hello World!"));
    }

}
