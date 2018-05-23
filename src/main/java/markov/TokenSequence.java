package markov;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class TokenSequence<T> {

    private final LinkedList<T> tokens;
    
    public TokenSequence(T... key) {
        this(Arrays.asList(key));
    }

    public TokenSequence(Collection<T> key) {
        tokens = new LinkedList<>(key);
    }

    private TokenSequence(Collection<T> key, T next) {
        this(key);
        tokens.removeFirst();
        tokens.addLast(next);
    }

    public TokenSequence<T> getNext(T next) {
        return new TokenSequence<>(tokens, next);
    }
    
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
