package components;

import components.RotorCompartment;
import utils.PairMap;
import utils.SelfMap;

import java.util.Map;
import java.util.HashMap;

// high level obfuscation layer
// user must not talk with underlying rotor objects! 
// instead provide methods to set them through those
public class EnigmaMachine {
  private int numContactPoints;

  private RotorCompartment rotors;
  private SelfMap<Integer> plugboard;

  private Map<Integer, Integer> identityMapping() {
    Map<Integer, Integer> wiring = new HashMap<>();
    for (int i = 0; i < numContactPoints; i++)
      wiring.put(i, i);
    return wiring;
  }

  public EnigmaMachine(int numContactPoints) {
    this.numContactPoints = numContactPoints;
    this.rotors = new RotorCompartment(numContactPoints);
    this.plugboard = new SelfMap<>(true);

    // prefill with default wiring
    this.plugboard.putAll(identityMapping());
  }

  public int getNumContactPoints() {
    return numContactPoints;
  }

  public int passThrough(int input) {
    // TODO! what if input is not within allowed range?
    int rawInput = plugboard.get(input);
    int rawOutput = rotors.passThrough(rawInput);
    int output = plugboard.get(rawOutput);
    return output;
  }

  // if we need more rotors: fill with identity wiring
  // if we need less rotors: remove last n
  public void setNumRotors(int num) {
    if (num >= rotors.size()) {
      int numNeeded = num - rotors.size();
      for (int i = 0; i < numNeeded; i++) {
        // prefill with a default mapping
        PairMap<Integer, Integer> wiring = new PairMap<>();
        wiring.putAll(identityMapping());

        Rotor extension = new Rotor(numContactPoints, wiring);
        rotors.add(extension);
      }
    } else {
      int numToDelete = rotors.size() - num;
      for (int i = 0; i < numToDelete; i++)
        rotors.remove(rotors.size() - 1);
    }
  }
  public int getNumRotors() {
    return rotors.size();
  }

  public void reset() {
    rotors.reset();
  }

  public PairMap<Integer, Integer> getWiring(int rotorIndex) {
    return rotors.get(rotorIndex).getWiring();
  }
  public void setWiring(int rotorIndex, PairMap<Integer, Integer> wiring) {
    rotors.get(rotorIndex).setWiring(wiring);
  }

  public int getStartingPosition(int rotorIndex) {
    return rotors.get(rotorIndex).getStartingPosition();
  }
  public void setStartingPosition(int rotorIndex, int position) {
    rotors.get(rotorIndex).setStartingPosition(position);
  }

  public SelfMap<Integer> getReflector() {
    return rotors.getReflector();
  }
  public void setReflector(SelfMap<Integer> reflector) {
    rotors.setReflector(reflector);
  }

  public SelfMap<Integer> getPlugboard() {
    return plugboard;
  }
  public void setPlugboard(SelfMap<Integer> plugboard) {
    // TODO! this may be filled out with identity
    // validate it!
    this.plugboard = plugboard;
  }
}
