package markov;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import markov.util.DefaultRandomGenerator;
import test_utils.TestUtility;

public class ProbabilityMappingTest {

    @Test
    public void testAdd() {
        ProbabilityMapping<Integer> map = new ProbabilityMapping<>();

        for (int i = 0; i < 10; i++) {
            map.add(i);
            assertEquals(i + 1, map.getTotalValues());
        }
        long total = map.getMapping()
                .values()
                .stream()
                .mapToLong(e -> e)
                .sum();
        assertEquals(total, map.getTotalValues());
    }

    @Test
    public void testAddWithQuantity() {
        ProbabilityMapping<Integer> map = new ProbabilityMapping<>();

        for (int i = 0; i < 10; i++) {
            map.add(i, 10);
            assertEquals((i + 1) * 10, map.getTotalValues());
        }
        long total = map.getMapping()
                .values()
                .stream()
                .mapToLong(e -> e)
                .sum();
        assertEquals(total, map.getTotalValues());
    }

    @Test
    public void testAddWithQuantityZero() {
        ProbabilityMapping<Integer> map = new ProbabilityMapping<>();
        map.add(2, 0);
        assertEquals(0, map.getTotalValues());
        assertTrue(map.getMapping()
                .isEmpty());
    }

    @Test
    public void testAddWithQuantityNegative() {
        boolean exceptionThrown = false;
        try {
            ProbabilityMapping<Integer> map = new ProbabilityMapping<>();
            map.add(2, -1);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testGetNextRandomlyFromEmptyMapping() {
        boolean exceptionThrown = false;
        try {
            ProbabilityMapping<Integer> map = new ProbabilityMapping<>();
            map.getNextRandomly(new DefaultRandomGenerator());
        } catch (IllegalStateException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testMerge() {
        ProbabilityMapping<Integer> map1 = new ProbabilityMapping<>();
        ProbabilityMapping<Integer> map2 = new ProbabilityMapping<>();

        map1.add(1, 3);
        map1.add(2, 4);
        map1.add(3, 5);

        map2.add(2, 4);
        map2.add(3, 5);
        map2.add(4, 4);

        ProbabilityMapping<Integer> merged = map1.merge(map2);
        assertFalse(map1.equals(merged));
        assertFalse(map2.equals(merged));
        assertFalse(map1.hashCode() == merged.hashCode());
        assertFalse(map2.hashCode() == merged.hashCode());

        assertEquals(12, map1.getTotalValues());
        assertEquals(13, map2.getTotalValues());

        map1.add(45, 45);
        map1.add(45, 45);

        assertEquals(25, merged.getTotalValues());

    }

    @Test
    public void testGetMappingUnmodifiable() {
        ProbabilityMapping<Integer> map = new ProbabilityMapping<>();
        map.add(1, 1);
        map.add(2, 2);
        map.add(3, 3);
        Map<Integer, Long> modified = map.getMapping();
        modified.remove(1);
        assertNotEquals("Modifying the mapping outside the object modifies the internal representation", modified,
                map.getMapping());
    }

    @Test
    public void testVariousSimpleEquals() {
        ProbabilityMapping<Integer> map1 = new ProbabilityMapping<>();
        ProbabilityMapping<Integer> map2 = new ProbabilityMapping<>();
        ProbabilityMapping<Integer> map3 = new ProbabilityMapping<>();
        map3.add(3, 3);
        assertTrue(map1.equals(map1));
        assertTrue(map1.equals(map2));
        assertFalse(map1.equals(map3));
        assertFalse(map1.equals(null));
        assertFalse(map1.equals("Hello World!"));
    }

    @Test
    public void testSetItemWithNegativeQuantity() {
        ProbabilityMapping<Integer> map = new ProbabilityMapping<>();
        TestUtility.shouldThrowException("Did not throw IllegalArgumentException", IllegalArgumentException.class,
                () -> map.set(4, -1));
    }

    @Test
    public void testSetItemWithZeroQuantity() {
        ProbabilityMapping<Integer> map = new ProbabilityMapping<>();
        map.set(2, 0);
        assertEquals("Total values not correct", map.getTotalValues(), 0);
        assertEquals("Value associated with key not correct", map.get(2), null);
    }

    @Test
    public void testSetItemWithItemAlreadyPresent() {
        ProbabilityMapping<Integer> map = new ProbabilityMapping<>();
        map.add(3, 5);
        map.set(3, 8);
        assertEquals("Total values not correct", map.getTotalValues(), 8);
        assertEquals("Value associated with key not correct", map.get(3), Long.valueOf(8));
    }

    @Test
    public void testSetItemWithNoPreviousItemPresent() {
        ProbabilityMapping<Integer> map = new ProbabilityMapping<>();
        map.set(3, 8);
        assertEquals("Total values not correct", map.getTotalValues(), 8);
        assertEquals("Value associated with key not correct", map.get(3), Long.valueOf(8));
    }

    @Test
    public void testGetWithEmptyMapping() {
        ProbabilityMapping<Integer> map = new ProbabilityMapping<>();
        Long value = map.get(2);
        assertTrue(value == null);
    }

    @Test
    public void testGetWithManyMappings() {
        ProbabilityMapping<Integer> map = new ProbabilityMapping<>();
        map.add(3, 4);
        map.add(4, 5);
        map.add(5, 6);
        Long value = map.get(3);
        assertTrue(value == 4);
    }

    @Test
    public void testGetWithInvalidItem() {
        ProbabilityMapping<Integer> map = new ProbabilityMapping<>();
        map.add(3, 4);
        map.add(4, 5);
        map.add(5, 6);
        Long value = map.get(2);
        assertTrue(value == null);
    }

    @Test
    public void testDefaultGetEmptyMapping() {
        ProbabilityMapping<Integer> map = new ProbabilityMapping<>();
        Long value = map.getOrDefault(2, 100L);
        assertTrue(value == 100);
    }

    @Test
    public void testDefaultGetManyMappings() {
        ProbabilityMapping<Integer> map = new ProbabilityMapping<>();
        map.add(3, 4);
        map.add(4, 5);
        map.add(5, 6);
        Long value = map.getOrDefault(3, 100L);
        assertTrue(value == 4);
    }

    @Test
    public void testDefaultInvalidItem() {
        ProbabilityMapping<Integer> map = new ProbabilityMapping<>();
        map.add(3, 4);
        map.add(4, 5);
        map.add(5, 6);
        Long value = map.getOrDefault(2, 100L);
        assertTrue(value == 100);
    }
}
