package components;

import components.RotorCompartment;
import components.Rotor;
import utils.PairMap;
import utils.SelfMap;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class RotorCompartmentTest {
  private Rotor rotorFrom(Map<Integer, Integer> internals) {
    PairMap<Integer, Integer> wiring = new PairMap<>();
    wiring.putAll(internals);
    return new Rotor(numContactPoints, wiring);
  }

  private RotorCompartment rotors;
  private SelfMap<Integer> reflector;
  private int numContactPoints;

  @BeforeEach 
  void init() {
    reflector = new SelfMap<>(false);
    reflector.putAll(Map.of(0,1));
    this.numContactPoints = 2;
    this.rotors = new RotorCompartment(numContactPoints);
    rotors.setReflector(reflector);
  }

  @Test 
  void reflectorGet() {
    assertSame(reflector, rotors.getReflector());
  }

  // {{{ ATOMIC LIST OPERATIONS
  @Test 
  void addIncreasesSize() {
    assumeTrue(rotors.isEmpty());
    assumeTrue(0 == rotors.size());

    rotors.add(0, rotorFrom(Map.of(0,0, 1,1)));

    assertFalse(rotors.isEmpty());
    assertEquals(1, rotors.size());
  }

  @Test
  void removeDecreasesSize() {
    rotors.add(0, rotorFrom(Map.of(0,0, 1,1)));
    assumeTrue(1 == rotors.size());

    rotors.remove(0);
    assertTrue(rotors.isEmpty());
    assertEquals(0, rotors.size());
  }
  // }}}

  // {{{ CHAINING TESTS
  // these tests specificially target the base methods,
  // who get targeted for some other parameterizations of methods
  private List<Rotor> addDefaults() {
    List<Rotor> addedRotors = new ArrayList<>();

    addedRotors.add(rotorFrom(Map.of(0,0, 1,1)));
    addedRotors.add(rotorFrom(Map.of(0,0, 1,1)));
    addedRotors.add(rotorFrom(Map.of(0,0, 1,1)));
    addedRotors.add(rotorFrom(Map.of(0,0, 1,1)));
    addedRotors.add(rotorFrom(Map.of(0,0, 1,1)));
    for (Rotor rotor : addedRotors)
      rotors.add(rotor);

    return addedRotors;
  }

  // check for chaining by comparing with ArrayList array
  private void assertChained(List<Rotor> oracleList) {
    Rotor lastRotor = rotors.getFirst();
    for (int i = 1; i < oracleList.size(); i++) {
      assertSame(oracleList.get(i), lastRotor.getNextRotor());
      lastRotor = lastRotor.getNextRotor();
    }
  }

  @Test 
  void addChains() {
    List<Rotor> oracleList = addDefaults();

    oracleList.add(0, rotorFrom(Map.of(0,1, 1,0)));
    rotors.add(0, oracleList.get(0));

    oracleList.add(1, rotorFrom(Map.of(0,1, 1,0)));
    rotors.add(1, oracleList.get(1));

    assertChained(oracleList);
  }

  @Test 
  void setChains() {
    List<Rotor> oracleList = addDefaults();

    oracleList.set(0, rotorFrom(Map.of(0,1, 1,0)));
    rotors.set(0, oracleList.get(0));

    oracleList.set(1, rotorFrom(Map.of(0,1, 1,0)));
    rotors.set(1, oracleList.get(1));

    assertChained(oracleList);
  }

  @Test 
  void removeChains() {
    List<Rotor> oracleList = addDefaults();

    oracleList.remove(1);
    rotors.remove(1);

    assertChained(oracleList);
  }

  @Test 
  void retainAllChains() {
    List<Rotor> oracleList = addDefaults();

    oracleList.remove(0);
    oracleList.remove(2);
    rotors.retainAll(oracleList);

    assertChained(oracleList);
  }
  // }}}

  // {{{ RECURSIVELY DEFINED LIST IMPLEMENTATION
  @Test 
  void clear() {
    rotors.add(0, rotorFrom(Map.of(0,0, 1,1)));
    assumeTrue(!rotors.isEmpty());

    rotors.clear();
    assertTrue(rotors.isEmpty());
  }

  @Test 
  void get() {
    Rotor added = rotorFrom(Map.of(0,0, 1,1));
    rotors.add(0, added);

    assertSame(added, rotors.get(0));
  }

  @Test 
  void containsNotContains() {
    Rotor added = rotorFrom(Map.of(0,0, 1,1));

    assertFalse(rotors.contains(added));
    rotors.add(0, added);
    assertTrue(rotors.contains(added));
  }

  @Test 
  void containsAllNotContainsOne() {
    List<Rotor> addedContainer = new ArrayList<>();
    addedContainer.add(rotorFrom(Map.of(0,0, 1,1)));
    addedContainer.add(rotorFrom(Map.of(0,0, 1,1)));
    rotors.addAll(addedContainer);

    assertTrue(rotors.containsAll(addedContainer));
    addedContainer.add(rotorFrom(Map.of(0,0, 1,1)));
    assertFalse(rotors.containsAll(addedContainer));
  }

  @Test 
  void indexOf() {
    Rotor needle = rotorFrom(Map.of(0,0, 1,1));
    List<Rotor> haystack = new ArrayList<>();
    haystack.add(rotorFrom(Map.of(0,0, 1,1)));
    haystack.add(needle);
    haystack.add(needle);
    haystack.add(rotorFrom(Map.of(0,0, 1,1)));
    rotors.addAll(haystack);

    assertEquals(haystack.indexOf(needle), rotors.indexOf(needle));
    assertEquals(-1, rotors.indexOf(rotorFrom(Map.of(0,0, 1,1))));
  }

  @Test 
  void lastIndexOf() {
    Rotor needle = rotorFrom(Map.of(0,0, 1,1));
    List<Rotor> haystack = new ArrayList<>();
    haystack.add(rotorFrom(Map.of(0,0, 1,1)));
    haystack.add(needle);
    haystack.add(needle);
    haystack.add(rotorFrom(Map.of(0,0, 1,1)));
    rotors.addAll(haystack);

    assertEquals(haystack.lastIndexOf(needle), rotors.lastIndexOf(needle));
    assertEquals(-1, rotors.indexOf(rotorFrom(Map.of(0,0, 1,1))));
  }

  @Test 
  void addAll() {
    List<Rotor> source = new ArrayList<>();
    source.add(rotorFrom(Map.of(0,0, 1,1)));
    source.add(rotorFrom(Map.of(0,0, 1,1)));
    rotors.addAll(source);

    assertEquals(source.size(), rotors.size());
    for (int i = 0; i < source.size(); i++) {
      assertSame(source.get(i), rotors.get(i));
    }
  }

  @Test 
  void remove() {
    Rotor needle = rotorFrom(Map.of(0,0, 1,1));
    List<Rotor> haystack = new ArrayList<>();
    haystack.add(rotorFrom(Map.of(0,0, 1,1)));
    haystack.add(needle);
    haystack.add(rotorFrom(Map.of(0,0, 1,1)));
    rotors.addAll(haystack);

    assumeTrue(rotors.contains(needle));
    assertTrue(rotors.remove(needle));
    assertFalse(rotors.remove(rotorFrom(Map.of(0,0, 1,1))));
  }

  @Test 
  void removeAll() {
    List<Rotor> source = new ArrayList<>();
    source.add(rotorFrom(Map.of(0,0, 1,1)));
    source.add(rotorFrom(Map.of(0,0, 1,1)));
    source.add(rotorFrom(Map.of(0,0, 1,1)));
    rotors.addAll(source);

    List<Rotor> filter = new ArrayList<>();
    filter.add(source.get(0));
    filter.add(source.get(2));

    assumeTrue(source.size() == rotors.size());
    boolean modified = rotors.removeAll(filter);
    assertTrue(modified);
    assertEquals(source.size() - filter.size(), rotors.size());

    assertFalse(rotors.removeAll(new ArrayList<>()));
  }

  // TODO! iterator and other boilerplate

  // }}}

  // {{{ PASSTHROUGH & ROTATIONS
  private int passThroughOracle(int pinNum) {
    int buffer = pinNum;
    // forward pass
    for (Rotor rotor : rotors)
      buffer = rotor.forwardPass(buffer);
    // reflector
    buffer = rotors.getReflector().get(buffer);
    // reverse pass
    for (Rotor rotor : rotors.reversed())
      buffer = rotor.reversePass(buffer);
    return buffer;
  }

  static Stream<Integer> passThroughData() {
    return Stream.of(0,1);
  }

  @ParameterizedTest 
  @MethodSource("passThroughData")
  void passThrough(int input) {
    rotors.add(rotorFrom(Map.of(0,0, 1,1)));
    rotors.add(rotorFrom(Map.of(0,1, 1,0)));

    List<Integer> rotorRotations = new ArrayList<>();
    for (Rotor rotor : rotors)
      rotorRotations.add(rotor.getRotationalPosition());

    assertEquals(passThroughOracle(input), rotors.passThrough(input));
  }


  @Test
  void symmetricalEncryptionDecryption() {
    rotors.add(rotorFrom(Map.of(0,0, 1,1)));
    rotors.add(rotorFrom(Map.of(0,1, 1,0)));

    List<Integer> inputs = List.of(0,1,0,1,0,1);
    for (int input : inputs) {
      int encryptPass = rotors.passThrough(input);
      int decryptPass = rotors.passThrough(encryptPass);

      assertNotEquals(encryptPass, decryptPass);
      assertEquals(input, decryptPass);
    }
  }

  @Test 
  void rotorsCycleAndReset() {
    rotors.add(rotorFrom(Map.of(0,0, 1,1)));
    rotors.add(rotorFrom(Map.of(0,1, 1,0)));

    for (int i = 1; i < numContactPoints; i++) {
      rotors.passThrough(0);
      assertEquals(i, rotors.getFirst().getRotationalPosition());
    }

    // after a full rotation:
    rotors.passThrough(0);
    assertEquals(0, rotors.getFirst().getRotationalPosition());
    assertEquals(1, rotors.getFirst().getNextRotor().getRotationalPosition());

    rotors.reset();
    for (Rotor rotor : rotors)
      assertEquals(0, rotor.getRotationalPosition());
  }
  // }}}
}
