package utils;
import utils.SelfMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class SelfMapTest {
  private SelfMap<Integer> map;

  @BeforeEach 
  void init() {
    this.map = new SelfMap<>();
  }

  /// TEST ATOMIC FEATURES ///
  @Test 
  void empty() {
    assertTrue(map.isEmpty());
    assertEquals(0, map.size());
  }

  @Test 
  void increasedSize() {
    map.put(1, 2);

    assertFalse(map.isEmpty());
    assertEquals(1, map.size());
  }

  @Test 
  void canClear() {
    map.put(1, 2);

    map.clear();
    assertTrue(map.isEmpty());
    assertEquals(0, map.size());
  }

  /// GETTING, PUTTING, REMOVING ///
  @Test 
  void getNotFound() {
    assertNull(map.get(1));
  }

  @Test
  void canGetPut() {
    map.put(1, 2);

    assertEquals(2, map.get(1));
    assertEquals(1, map.get(2));
  }

  @Test 
  void removesNone() {
    Integer removed = map.remove(1);
    assertNull(removed);
  }

  @Test 
  void removesFirst() {
    map.put(1, 2);
    Integer removed = map.remove(1);

    assertTrue(map.isEmpty());
    assertEquals(2, removed);
  }

  @Test 
  void removesSecond() {
    map.put(1, 2);
    Integer removed = map.remove(2);

    assertTrue(map.isEmpty());
    assertEquals(1, removed);
  }

  @Test 
  void putAll() {
    Map<Integer, Integer> source = Map.of(1,2, 3,4, 5,6);
    Set<Integer> allvalues = new HashSet<>();
    allvalues.addAll(source.keySet());
    allvalues.addAll(source.values());
    map.putAll(source);

    assertEquals(source.size(), map.size());
    assertEquals(allvalues, map.values());
  }

  /// CONTAINSKEY & VALUE, KEYSET & VALUES ///
  @Test 
  void containsKeyValue() {
    map.put(1, 2);
    assertTrue(map.containsKey(1));
    assertTrue(map.containsKey(2));
    assertTrue(map.containsValue(1));
    assertTrue(map.containsValue(2));
  }

  @Test 
  void notContainsKeyValue() {
    assertFalse(map.containsKey(1));
    assertFalse(map.containsKey(2));
    assertFalse(map.containsValue(1));
    assertFalse(map.containsValue(2));
  }

  @Test 
  void keySetValues() {
    map.put(1, 2);
    map.put(3, 4);

    Set<Integer> allKeyValues = Set.of(1,2,3,4);
    assertEquals(allKeyValues, map.keySet());
    assertEquals(allKeyValues, map.values());
  }

  /// IDENTITY MAPPING SPECIFIC ///
  @Test
  void getIdentity() {
    map = new SelfMap<>(false);
    assertFalse(map.areIdentityMappingsAllowed());

    map = new SelfMap<>(true);
    assertTrue(map.areIdentityMappingsAllowed());
  }

  @Test 
  void identityFailsPutIfIdentityDisabled() {
    map = new SelfMap<>(false);
    map.put(1, 1);

    assertNull(map.get(1));
    assertTrue(map.isEmpty());
  }

  @Test 
  void identitySuccessfulPutIfIdentityEnabled() {
    map = new SelfMap<>(true);
    map.put(1, 1);

    assertEquals(1, map.get(1));
    assertFalse(map.isEmpty());
  }

  @Test 
  void identityRemoves() {
    map = new SelfMap<>(true);
    map.put(1, 1);
    Integer removed = map.remove(1);

    assertTrue(map.isEmpty());
    assertEquals(1, removed);
  }

  @Test 
  void identityOverwrites() {
    map = new SelfMap<>(true);
    map.put(1, 2);
    map.put(1, 1);

    assertNull(map.get(2));
    assertEquals(1, map.get(1));
  }

  @Test 
  void identityOverwritten() {
    map = new SelfMap<>(true);
    map.put(1, 1);
    map.put(1, 2);

    assertEquals(1, map.get(2));
    assertEquals(2, map.get(1));
  }
}
