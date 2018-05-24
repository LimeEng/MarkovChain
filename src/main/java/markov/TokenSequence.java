package markov;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class TokenSequence<T> {

    private final LinkedList<T> tokens;

    /**
     * Constructs a new TokenSequence with the specified array.
     * 
     * @param key
     *            the collection to build the sequence with
     */
    public TokenSequence(T... key) {
        this(Arrays.asList(key));
    }

    /**
     * Constructs a new TokenSequence with the specified collection.
     * 
     * @param key
     *            the collection to build the sequence with
     */
    public TokenSequence(Collection<T> key) {
        tokens = new LinkedList<>(key);
    }

    private TokenSequence(Collection<T> key, T next) {
        this(key);
        tokens.removeFirst();
        tokens.addLast(next);
    }

    /**
     * Constructs a new instance of this TokenSequence and the specified token.
     * The first token in this sequence is dropped and the specified token is
     * appended to the tail
     * 
     * @param next
     *            the token to be appended
     * @return a new TokenSequence
     */
    public TokenSequence<T> getNext(T next) {
        return new TokenSequence<>(tokens, next);
    }

    /**
     * Returns a copy of the list of tokens. Changes in the copy will not
     * reflect in the original, and vice versa.
     * 
     * @return a list of tokens
     */
    public List<T> getTokens() {
        return new LinkedList<>(tokens);
    }

    @Override
    public int hashCode() {
        return tokens.hashCode();
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
        TokenSequence<?> other = (TokenSequence<?>) obj;
        if (tokens == null) {
            if (other.tokens != null) {
                return false;
            }
        } else if (!tokens.equals(other.tokens)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return tokens.toString();
    }
}
