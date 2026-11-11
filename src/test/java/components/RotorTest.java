package components;

import components.Rotor;
import utils.PairMap;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class RotorTest {
  private Rotor rotor;
  private PairMap<Integer, Integer> map;
  private int numContactPoints;

  @BeforeEach 
  void init() {
    map = new PairMap<>();
    map.putAll(Map.of(0,0, 1,1));
    this.numContactPoints = 2;
    this.rotor = new Rotor(numContactPoints, map);
  }

  // {{{ ATOMIC TESTS, GETTERS SETTERS
  @Test 
  void createdContactPoints() {
    assertEquals(numContactPoints, rotor.getNumContactPoints());
  }

  @Test 
  void contactPointGetSet() {
    rotor.setStartingPosition(1);
    assertEquals(1, rotor.getStartingPosition());
  }

  @Test 
  void nextRotorGetSet() {
    assumeTrue(rotor.getNextRotor() == null);

    PairMap<Integer, Integer> nextWiring = new PairMap<>();
    nextWiring.putAll(Map.of(0,1, 1,0));
    Rotor nextRotor = new Rotor(2, nextWiring);
    rotor.setNextRotor(nextRotor);

    assertNotNull(rotor.getNextRotor());
    assertSame(nextRotor, rotor.getNextRotor());
  }

  @Test 
  void rotatesSelfAndNext() {
    assumeTrue(0 == rotor.getRotationalPosition());

    PairMap<Integer, Integer> nextWiring = new PairMap<>();
    nextWiring.putAll(Map.of(0,1, 1,0));
    Rotor nextRotor = new Rotor(2, nextWiring);
    rotor.setNextRotor(nextRotor);
    assumeTrue(0 == nextRotor.getRotationalPosition());

    for (int i = 1; i < numContactPoints; i++) {
      rotor.rotate();
      assertEquals(i, rotor.getRotationalPosition());
    }

    // after a full rotation:
    rotor.rotate();
    assertEquals(0, rotor.getRotationalPosition());
    assertEquals(1, rotor.getNextRotor().getRotationalPosition());
  }

  @Test 
  void reset() {
    assumeTrue(0 == rotor.getRotationalPosition());
    rotor.rotate();
    assumeTrue(1 == rotor.getRotationalPosition());

    rotor.reset();
    assertEquals(0, rotor.getRotationalPosition());
  }
  // }}}

  // {{{ FORWARD/REVERSE PASSES
  static Stream<Integer> passThroughData() {
    return Stream.of(0,1,2);
  }

  int modulus(int n) {
    if (n < 0)
      return numContactPoints - (Math.abs(n) % numContactPoints);
    else
      return n % numContactPoints;
  }

  int forwardPassOracle(int input) {
    int rotation = rotor.getRotationalPosition();
    int pinRelativeToRotor = modulus(input - rotation);
    return modulus(map.get(pinRelativeToRotor) + rotation);
  }
  int reversePassOracle(int input) {
    int rotation = rotor.getRotationalPosition();
    int pinRelativeToRotor = modulus(input - rotation);
    return modulus(map.getReverse(pinRelativeToRotor) + rotation);
  }

  @ParameterizedTest 
  @MethodSource("passThroughData")
  void forwardPass(int entryValue) {
    for (int i = 0; i < numContactPoints + 1; i++) {
      rotor.rotate();
      assertEquals(forwardPassOracle(entryValue), rotor.forwardPass(entryValue));
    }
  }
  @ParameterizedTest 
  @MethodSource("passThroughData")
  void reversePass(int entryValue) {
    for (int i = 0; i < numContactPoints + 1; i++) {
      rotor.rotate();
      assertEquals(reversePassOracle(entryValue), rotor.reversePass(entryValue));
    }
  }
  // }}}
}
