package components;

import components.RotorCompartment;
import utils.PairMap;
import utils.SelfMap;

// high level obfuscation layer
// user must not talk with underlying rotor objects! 
// instead provide methods to set them through those
public class EnigmaMachine {
  private int numContactPoints;

  private RotorCompartment rotors;
  private SelfMap<Integer> plugboard;

  public EnigmaMachine(int numContactPoints) {
    this.rotors = new RotorCompartment(numContactPoints);
    this.plugboard = new SelfMap<>(true);
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

  private PairMap<Integer, Integer> defaultWiring() {
    PairMap<Integer, Integer> wiring = new PairMap<>();
    for (int i = 0; i < numContactPoints; i++)
      wiring.put(i, i);
    return wiring;
  }

  // if we need more rotors: fill with identity wiring
  // if we need less rotors: remove last n
  public void setNumRotors(int num) {
    if (num >= rotors.size()) {
      int numNeeded = num - rotors.size();
      for (int i = 0; i < numNeeded; i++) {
        Rotor extension = new Rotor(numContactPoints, defaultWiring());
        rotors.add(extension);
      }
    } else {
      int numToDelete = rotors.size() - num;
      for (int i = 0; i < numToDelete; i++)
        rotors.remove(rotors.size() - i);
    }
  }
  public int getNumRotors() {
    return rotors.size();
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
    this.plugboard = plugboard;
  }
}
