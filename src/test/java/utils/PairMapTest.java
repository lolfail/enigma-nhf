package utils;
import utils.PairMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class PairMapTest {
  private PairMap<Integer, String> map;

  @BeforeEach 
  void init() {
    this.map = new PairMap<>();
  }

  /// TEST ATOMIC FEATURES ///
  @Test 
  void empty() {
    assertTrue(map.isEmpty());
    assertEquals(0, map.size());
  }

  @Test
  void increasedSize() {
    map.put(1, "a");

    assertFalse(map.isEmpty());
    assertEquals(1, map.size());
  }

  @Test
  void canClear() {
    map.put(1, "a");

    map.clear();
    assertTrue(map.isEmpty());
    assertEquals(0, map.size());
  }

  /// SINGLE GETTING, PUTTING, REMOVING ///
  @Test 
  void getNotFound() {
    assertNull(map.get(1));
    assertNull(map.getReverse("a"));
  }

  @Test
  void canGetPut() {
    map.put(1, "a");

    assertEquals("a", map.get(1));
    assertEquals(1, map.getReverse("a"));
  }

  @Test
  void canGetPutReverse() {
    map.putReverse("a", 1);

    assertEquals("a", map.get(1));
    assertEquals(1, map.getReverse("a"));
  }

  @Test 
  void putOverwritesPrevious() {
    String initial = map.put(1, "a");
    assertNull(initial);
    String overwritten = map.put(1, "b");
    assertEquals(overwritten, "a");
  }

  @Test 
  void putOverwritesPreviousReverse() {
    Integer initial = map.putReverse("a", 1);
    assertNull(initial);
    Integer overwritten = map.putReverse("a", 2);
    assertEquals(overwritten, 1);
  }

  @Test 
  void getsCorrectOne() {
    Map<Integer, String> pairs = new HashMap<>();
    pairs.put(1, "a");
    pairs.put(2, "b");
    pairs.put(3, "c");
    for (Map.Entry<Integer, String> pair : pairs.entrySet()) {
      map.put(pair.getKey(), pair.getValue());
    }

    for (Map.Entry<Integer, String> pair : pairs.entrySet()) {
      Integer key = pair.getKey();
      String value = pair.getValue();
      assertEquals(value, map.get(key));
      assertEquals(key, map.getReverse(value));
    }
  }

  @Test 
  void removesPut() {
    map.put(1, "a");
    assertNotNull(map.get(1));

    assertEquals(map.remove(1), "a");
    assertTrue(map.isEmpty());
    assertNull(map.remove(1));
  }

  @Test 
  void removesPutReverse() {
    map.put(1, "a");
    assertNotNull(map.getReverse("a"));

    assertEquals(map.removeReverse("a"), 1);
    assertTrue(map.isEmpty());
    assertNull(map.removeReverse("a"));
  }

  /// CONTAINSKEY & VALUE ///
  @Test 
  void notContainsKeyValue() {
    boolean key = map.containsKey(1);
    boolean valueReverse = map.containsValueReverse(1);
    boolean value = map.containsValue("a");
    boolean keyReverse = map.containsKeyReverse("a");

    assertFalse(key);
    assertFalse(valueReverse);
    assertFalse(value);
    assertFalse(keyReverse);

    assertSame(key, valueReverse);
    assertSame(value, keyReverse);
  }

  @Test 
  void containsKeyValue() {
    map.put(1, "a");

    boolean key = map.containsKey(1);
    boolean valueReverse = map.containsValueReverse(1);
    boolean value = map.containsValue("a");
    boolean keyReverse = map.containsKeyReverse("a");

    assertTrue(key);
    assertTrue(valueReverse);
    assertTrue(value);
    assertTrue(keyReverse);

    assertSame(key, valueReverse);
    assertSame(value, keyReverse);
  }

  /// ENTRYSET, KEYSET & VALUES ///
  @Test 
  void entrySetAndReverse() {
    map.put(1, "a");
    map.put(2, "b");

    assertEquals(
      Set.of(Map.entry(1, "a"), Map.entry(2, "b")),
      map.entrySet()
    );
    assertEquals(
      Set.of(Map.entry("a", 1), Map.entry("b", 2)),
      map.entrySetReverse()
    );
  }

  @Test 
  void emptyEntrySetKeySetValues() {
    assertEquals(Set.of(), map.entrySet());
    assertEquals(Set.of(), map.entrySetReverse());

    assertEquals(Set.of(), map.keySet());
    assertEquals(Set.of(), map.valuesReverse());
    assertEquals(Set.of(), map.values());
    assertEquals(Set.of(), map.keySetReverse());
  }

  @Test 
  void keySetMatchesValuesReverse() {
    map.put(1, "a");
    map.put(2, "b");

    Set<Integer> keys = map.keySet();
    Set<Integer> valuesReverse = map.valuesReverse();
    Set<String> values = map.values();
    Set<String> keysReverse = map.keySetReverse();

    Set<Integer> expectedIntegers = Set.of(1, 2);
    Set<String> expectedStrings = Set.of("a", "b");
    assertEquals(expectedIntegers, keys);
    assertEquals(expectedIntegers, valuesReverse);
    assertEquals(expectedStrings, values);
    assertEquals(expectedStrings, keysReverse);

    assertSame(keys, valuesReverse);
    assertSame(values, keysReverse);
  }

  /// PUTALL AND PUTALLREVERSE ///
  @Test 
  void putAll() {
    Map<Integer, String> source = Map.of(1, "a", 2, "b");
    map.putAll(source);

    for (Map.Entry<Integer, String> entry : source.entrySet()) {
      assertNotNull(map.get(entry.getKey()));
      assertNotNull(map.getReverse(entry.getValue()));
    }
  }

  @Test 
  void putAllReverse() {
    Map<String, Integer> source = Map.of("a", 1, "b", 2);
    map.putAllReverse(source);

    for (Map.Entry<String, Integer> entry : source.entrySet()) {
      assertNotNull(map.get(entry.getValue()));
      assertNotNull(map.getReverse(entry.getKey()));
    }
  }

  /// LOGIC TESTS ///
  // test that both components are keys, are mapped together
  @Test 
  void bothAreKeys() {
    map.put(1, "a");
    map.put(1, "b");

    // should overwrite, keep it at 1 entry
    assertEquals(1, map.size());
    // forward direction is overwritten (automatic)
    assertEquals("b", map.get(1));
    // reverse direction points to correct value
    assertEquals(1, map.getReverse("b"));
    // reverse direction is overwritten
    assertNull(map.getReverse("a"));
  }

  @Test 
  void bothAreKeysReverse() {
    map.put(1, "a");
    map.put(2, "a");

    // should overwrite, keep it at 1 entry
    assertEquals(1, map.size());
    // reverse direction is overwritten (automatic)
    assertEquals(2, map.getReverse("a"));
    // forward direction points to correct value
    assertEquals("a", map.get(2));
    // forward direction is overwritten
    assertNull(map.get(1));
  }

  // test what happens when putAll gets a non pair mapping
  @Test 
  void putAllNonPairs() {
    Map<Integer, String> source = Map.of(1, "a", 2, "a");
    map.putAll(source);

    assertEquals(1, map.size());
  }

  @Test 
  void putAllNonPairsReverse() {
    Map<String, Integer> source = Map.of("a", 1, "b", 1);
    map.putAllReverse(source);

    assertEquals(1, map.size());
  }
}
