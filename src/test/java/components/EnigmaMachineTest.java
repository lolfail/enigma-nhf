package components;

import components.EnigmaMachine;
import utils.PairMap;
import utils.SelfMap;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class EnigmaMachineTest {
  private EnigmaMachine enigmaMachine;
  private int numContactPoints;

  @BeforeEach 
  void init() {
    this.numContactPoints = 2;
    this.enigmaMachine = new EnigmaMachine(this.numContactPoints);
  }

  // {{{ ATOMIC TESTS
  @Test 
  void emptyGetNumRotors() {
    assertEquals(0, enigmaMachine.getNumRotors());
  }

  @Test 
  void getNumContactPoints() {
    assertEquals(numContactPoints, enigmaMachine.getNumContactPoints());
  }
  // }}}

  // {{{ REFLECTOR & PLUGBOARD GET SET
  @Test 
  void reflectorSetGet() {
    SelfMap<Integer> reflector = new SelfMap<>(false, Map.of(0,1));
    enigmaMachine.setReflector(reflector);
    assertSame(reflector, enigmaMachine.getReflector());
  }

  @Test 
  void plugboardGetSetFull() {
    SelfMap<Integer> plugboard = new SelfMap<>(true, Map.of(0,1));
    enigmaMachine.setPlugboard(plugboard);
    assertSame(plugboard, enigmaMachine.getPlugboard());
  }

  @Test 
  void plugboardGetSetFill() {
    SelfMap<Integer> plugboard = new SelfMap<>(true);
    enigmaMachine.setPlugboard(plugboard);
    assertEquals(Map.of(0,0, 1,1).entrySet(),
      enigmaMachine.getPlugboard().entrySet());
  }
  // }}}

  // {{{ SETNUMROTORS, WIRING & STARTINGPOSITION GET SET
  @Test 
  void setNumRotorsChangesNum() {
    assumeTrue(0 == enigmaMachine.getNumRotors());
    enigmaMachine.setNumRotors(1);
    assertEquals(1, enigmaMachine.getNumRotors());
  }

  @Test 
  void setNumRotorsFillsDefaults() {
    enigmaMachine.setNumRotors(1);
    PairMap<Integer, Integer> wiring = new PairMap<>(Map.of(0,0, 1,1));
    assertEquals(wiring.entrySet(), enigmaMachine.getWiring(0).entrySet());
  }
  
  @Test 
  void setNumRotorsDeletesLast() {
    enigmaMachine.setNumRotors(2);
    PairMap<Integer, Integer> crossWiring = new PairMap<>(Map.of(0,1, 1,0));
    enigmaMachine.setWiring(0, crossWiring);

    assumeTrue(Map.of(0,0, 1,1).entrySet().equals(enigmaMachine.getWiring(1).entrySet()));
    enigmaMachine.setNumRotors(1);
    assertEquals(crossWiring.entrySet(), enigmaMachine.getWiring(0).entrySet());
  }

  @Test 
  void wiringGetSet() {
    enigmaMachine.setNumRotors(1);
    assumeTrue(Map.of(0,0, 1,1).entrySet().equals(enigmaMachine.getWiring(0).entrySet()));

    PairMap<Integer, Integer> crossWiring = new PairMap<>(Map.of(0,1, 1,0));
    enigmaMachine.setWiring(0, crossWiring);
    assertEquals(crossWiring.entrySet(), enigmaMachine.getWiring(0).entrySet());
  }

  @Test 
  void startingPositionGetSet() {
    enigmaMachine.setNumRotors(1);

    assumeTrue(0 == enigmaMachine.getStartingPosition(0));
    enigmaMachine.setStartingPosition(0, 1);
    assertEquals(1, enigmaMachine.getStartingPosition(0));
  }
  // }}}

  @Test 
  void passThroughGivesSameOnReset() {
    enigmaMachine.setNumRotors(1);
    enigmaMachine.setReflector(new SelfMap<Integer>(false, Map.of(0,1)));
    enigmaMachine.setPlugboard(new SelfMap<Integer>(true));

    int[] in = {0, 1, 1, 0};
    int[] encrypted = {-1, -1, -1, -1};
    for (int i = 0; i < in.length; i++)
      encrypted[i] = enigmaMachine.passThrough(in[i]);

    enigmaMachine.reset();

    int[] decrypted = {-1, -1, -1, -1};
    for (int i = 0; i < encrypted.length; i++)
      decrypted[i] = enigmaMachine.passThrough(encrypted[i]);

    assertArrayEquals(in, decrypted);
  }
}
