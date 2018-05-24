package markov.util;

public class DefaultRandomGeneratorTest extends RandomGeneratorTest {

    @Override
    protected DefaultRandomGenerator createInstance() {
        return new DefaultRandomGenerator();
    }

}
