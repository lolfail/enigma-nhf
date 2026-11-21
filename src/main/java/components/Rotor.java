package components;

import utils.PairMap;

import validators.RotorWiringValidator;
import validators.Validator;

/**
 * A class that models a Rotor in the Enigma Machine.
 *
 * Passing forwards (from side a to side b) and backwards (side b to side a) is possible.
 * However it will not rotate the rotor by itself, needs that method called.
 * But when the rotation method is called, and a nextRotor is set, it will rotate the next rotor when a full rotation has been done.
 *
 * Will refer to "absolute" and "relative" positioning:
 * Interfacing between rotors is done via pin numbers.
 * But to establish a "common language" between two rotors, absolute numbering is used,
 * that is, when the rotors rotate, the pin associated with the absolute number changes.
 * Contrary to that, the relative number tracks rotations.
 * (This is because internal wiring is mapped according to relative numbers.)
 */
public class Rotor {
  // wiring enumerates contact points relative to the rotor
  // so if Rotor has not rotated, 0th is the topmost one
  // if Rotor has rotated 1 time, 0th is the one left or right to the topmost one
  private PairMap<Integer, Integer> wiring;
  private Validator<PairMap<Integer, Integer>> wiringValidator;

  private int numContactPoints;

  private int startingPosition;
  private int rotationalPosition;

  private Rotor nextRotor;

  public Rotor(int numContactPoints, PairMap<Integer, Integer> wiring) {
    this.numContactPoints = numContactPoints;

    this.startingPosition = 0;
    this.rotationalPosition = this.startingPosition;

    this.wiringValidator = new RotorWiringValidator(this.numContactPoints);

    wiringValidator.validate(wiring);
    this.wiring = wiring;
  }

  /**
   * Get the number of contact points on this rotor.
   * @return The amount of positions the rotor can take up, before "resetting".
   */
  public int getNumContactPoints() {
    return this.numContactPoints;
  }

  /**
   * Get the internal wiring of the rotor.
   * @return A {@link PairMap} that contains every mapping from one side to the other.
   */
  public PairMap<Integer, Integer> getWiring() {
    return this.wiring;
  }

  /**
   * Set the rotor's internal wiring.
   * @param The {@link PairMap} that should map from every point on one side to every point on the other side of the rotor.
   *
   * See {@link RotorWiringValidator} that is used to validate the parameter.
   */
  public void setWiring(PairMap<Integer, Integer> wiring) {
    wiringValidator.validate(wiring);
    this.wiring = wiring;
  }

  /**
   * Set the starting position of the rotor from which it rotates, until resetting.
   * @param position The position to set, [0, {@link #numContactPoints}).
   */
  public void setStartingPosition(int position) {
    if (!(0 <= position && position < numContactPoints))
      throw new IndexOutOfBoundsException("Such a starting position can not be possible!");

    this.startingPosition = position;
  }

  /**
   * Get the starting position of the rotor.
   * @return The starting position of the rotor.
   */
  public int getStartingPosition() {
    return this.startingPosition;
  }

  /**
   * Set the next rotor, that will be automatically rotated when this rotor has completed a rotation.
   * @param nextRotor The next rotor.
   */
  public void setNextRotor(Rotor nextRotor) {
    this.nextRotor = nextRotor;
  }

  /**
   * Get the previously set next rotor.
   * @return The rotor that was set previously.
   */
  public Rotor getNextRotor() {
    return this.nextRotor;
  }

  /**
   * Helper function modulusRotation(position) = position (mod {@link #numContactPoints})
   * @param position The parameter to take the mod {@link #numContactPoints} of.
   * @return The number: [0, {@link #numContactPoints})
   *
   * @implNote -1 % 4 == -1 in java so branching is needed.
   */
  private int modulusRotation(int position) {
    if (position < 0)
      return numContactPoints - (Math.abs(position) % numContactPoints);
    else
      return position % numContactPoints;
  }

  /**
   * Get the pin number relative to the rotor, tracking rotation.
   * Relative, as in setting the reference to be rotor, rotating with the rotor.
   *
   * @param pinNumAbsolute The number of the pin looking at the rotor while keeping the numbering of pins fixed (not tracking rotation).
   * @return The number of the pin on the rotor, that tracks rotation.
   */
  private int getPinRelative(int pinNumAbsolute) {
    int rawRelative = pinNumAbsolute - rotationalPosition;
    return modulusRotation(rawRelative);
  }

  /**
   * Get the pin number as if we were not tracking the rotation of the rotors.
   * See {@link #getPinRelative(int)}, this is the opposite of that.
   */
  private int getPinAbsolute(int pinNumRelative) {
    int rawAbsolute = pinNumRelative + rotationalPosition;
    return modulusRotation(rawAbsolute);
  }

  /**
   * Get what pin is associated with the provided pin.
   * @param forwardPinAbsolute The absolute pin number as an "input".
   * @return The associated "output" absolute pin number.
   */
  public int forwardPass(int forwardPinAbsolute) {
    int inputPinRelative = getPinRelative(forwardPinAbsolute);
    int outputPinRelative = wiring.get(inputPinRelative);
    int outputPinAbsolute = getPinAbsolute(outputPinRelative);
    return outputPinAbsolute;
  }

  /**
   * See {@link #forwardPass(int)}, this tackles the same thing but in reverse direction.
   */
  public int reversePass(int reversePinAbsolute) {
    int inputPinRelative = getPinRelative(reversePinAbsolute);
    int outputPinRelative = wiring.getReverse(inputPinRelative);
    int outputPinAbsolute = getPinAbsolute(outputPinRelative);
    return outputPinAbsolute;
  }

  /**
   * Rotate the current rotor, and if it has rotated completely, rotate the next one as well.
   */
  public void rotate() {
    boolean completedRotation = (rotationalPosition + 1) >= numContactPoints;
    if (completedRotation && nextRotor != null)
      nextRotor.rotate();

    rotationalPosition = modulusRotation(rotationalPosition + 1);
  }

  /**
   * Get at what position the rotor is in.
   * @return How many positions is it away from the 0-th one.
   */
  public int getRotationalPosition() {
    return this.rotationalPosition;
  }

  /**
   * Reset this rotor back to the starting position.
   * Will not reset any of the next (or previous) rotors.
   */
  public void reset() {
    rotationalPosition = startingPosition;
  }
}
