package utils;
import utils.PairMap;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;

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
    map.put(1, "Hello World");

    assertFalse(map.isEmpty());
    assertEquals(1, map.size());
  }

  @Test
  void canClear() {
    map.put(1, "Hello World");

    map.clear();
    assertTrue(map.isEmpty());
    assertEquals(0, map.size());
  }

  /// GETTING AND PUTTING ///
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
}
