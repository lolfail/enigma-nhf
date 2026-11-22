package components;

import components.RotorCompartment;
import utils.PairMap;
import utils.SelfMap;
import validators.PlugboardValidator;
import validators.Validator;

import java.util.Map;
import java.util.HashMap;

/**
 * The Enigma Machine that users are encouraged to use.
 * Not only is this the "first" class that uses the plugboard,
 * but also provides a high level way of interfacing with the components.
 * This is both for simplified methods (not having overly long getter chains),
 * but also for additional optional validation of arguments (even though most objects will handle those at the lowest level).
 *
 * See {@link RotorCompartment} for the Rotor Stack.
 */
public class EnigmaMachine {
  private int numContactPoints;

  private RotorCompartment rotors;
  private SelfMap<Integer> plugboard;

  private Validator<SelfMap<Integer>> plugboardValidator;

  /**
   * Get a map who maps number from [0, numContactPoints) (in i to i) fashion.
   * @return The map.
   */
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
    this.plugboardValidator = new PlugboardValidator(this.numContactPoints);

    // prefill with default wiring
    this.plugboard.putAll(identityMapping());
  }

  /**
   * Take the parameter, and shallow copy internals of it to self.
   * Because of this, the parameter is expected never to be used again.
   *
   * @param other The parameter to use as a reference to copy.
   */
  public void shallowCopyInternals(EnigmaMachine other) {
    this.numContactPoints = other.numContactPoints;
    this.plugboard = other.plugboard;
    this.rotors = other.rotors;
    this.plugboardValidator = other.plugboardValidator;
  }

  /**
   * Get the number of pins on every rotor.
   * @return The number of contact points.
   */
  public int getNumContactPoints() {
    return numContactPoints;
  }

  /**
   * "Pass a signal through the Enigma Machine".
   * @param input The input pin's number on the plugboard.
   * @return The output pin's number.
   *
   * In practice, just as with {@link RotorCompartment} it is the pin's number we're passing,
   * and it yields the pin that it is associated with as it goes through the machine.
   * However this time the plugboard also acts on that pin.
   */
  public int passThrough(int input) {
    int rawInput = plugboard.get(input);
    int rawOutput = rotors.passThrough(rawInput);
    int output = plugboard.get(rawOutput);
    return output;
  }

  // if we need more rotors: fill with identity wiring
  // if we need less rotors: remove last n
  /**
   * Set the number of rotors in the Enigma Machine.
   * @param num The number to set to.
   * 
   * Note that this will prefill the rotors with an identity mapping.
   * If we need less rotors, it will start deleting from the end.
   */
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

  /**
   * Get the number of rotors in the machine currently.
   * @return The number of rotors.
   */
  public int getNumRotors() {
    return rotors.size();
  }

  /**
   * Reset the rotors back to their starting position.
   */
  public void reset() {
    rotors.reset();
  }

  /**
   * Get the wiring of a rotor.
   * @param rotorIndex The index of the rotor to get wiring of.
   * @return The wiring/mapping of the rotor.
   */
  public PairMap<Integer, Integer> getWiring(int rotorIndex) {
    return rotors.get(rotorIndex).getWiring();
  }

  /**
   * Set the wiring of a rotor by it's index.
   * @param rotorIndex The index of the rotor.
   * @param The wiring to set it to.
   */
  public void setWiring(int rotorIndex, PairMap<Integer, Integer> wiring) {
    rotors.get(rotorIndex).setWiring(wiring);
  }

  /**
   * Get the starting position of a rotor.
   * @param rotorIndex Index of the rotor to get the starting position of.
   * @return The starting position.
   */
  public int getStartingPosition(int rotorIndex) {
    return rotors.get(rotorIndex).getStartingPosition();
  }

  /**
   * Set the starting position of a rotor.
   * @param rotorIndex The rotor's index whose position to set.
   * @param position The starting position to set.
   */
  public void setStartingPosition(int rotorIndex, int position) {
    rotors.get(rotorIndex).setStartingPosition(position);
  }

  /**
   * Get the reflector of the Rotor Stack.
   * @return The mapping representing the reflector.
   */
  public SelfMap<Integer> getReflector() {
    return rotors.getReflector();
  }

  /**
   * Set the reflector of the Rotor Stack.
   * @param reflector The mapping representing the reflector.
   */
  public void setReflector(SelfMap<Integer> reflector) {
    rotors.setReflector(reflector);
  }

  /**
   * Get the plugboard of the machine.
   * @return The mapping representing the plugboard.
   */
  public SelfMap<Integer> getPlugboard() {
    return plugboard;
  }

  /**
   * Set the plugboard of the machine.
   * @param plugboard The mapping to use as a source.
   *
   * This method will validate the mapping, see {@link PlugboardValidator}.
   * It will also fill it up with identity mappings if some "plugs" are not mapped.
   */
  public void setPlugboard(SelfMap<Integer> plugboard) {
    for (int i = 0; i < numContactPoints; i++) {
      if (!plugboard.containsKey(i))
        plugboard.put(i, i);
    }

    plugboardValidator.validate(plugboard);
    this.plugboard = plugboard;
  }
}
