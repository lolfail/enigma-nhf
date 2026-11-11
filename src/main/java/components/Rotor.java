package components;

import utils.PairMap;

import validators.RotorWiringValidator;

public class Rotor {
  // wiring enumerates contact points relative to the rotor
  // so if Rotor has not rotated, 0th is the topmost one
  // if Rotor has rotated 1 time, 0th is the one left or right to the topmost one
  private PairMap<Integer, Integer> wiring;
  private int numContactPoints;

  private int startingPosition;
  private int rotationalPosition;

  private Rotor nextRotor;

  public Rotor(int numContactPoints, PairMap<Integer, Integer> wiring) {
    this.numContactPoints = numContactPoints;

    this.startingPosition = 0;
    this.rotationalPosition = this.startingPosition;

    (new RotorWiringValidator(this.numContactPoints))
      .validate(wiring);
    this.wiring = wiring;
  }

  public int getNumContactPoints() {
    return this.numContactPoints;
  }

  public void setStartingPosition(int position) {
    // TODO! position not within limits?

    this.startingPosition = position;
  }
  public int getStartingPosition() {
    return this.startingPosition;
  }

  public void setNextRotor(Rotor nextRotor) {
    this.nextRotor = nextRotor;
  }
  public Rotor getNextRotor() {
    return this.nextRotor;
  }

  // WARNING! -1 % 4 == -1
  private int modulusRotation(int position) {
    if (position < 0)
      return numContactPoints - (Math.abs(position) % numContactPoints);
    else
      return position % numContactPoints;
  }

  private int getPinRelative(int pinNumAbsolute) {
    int rawRelative = pinNumAbsolute - rotationalPosition;
    return modulusRotation(rawRelative);
  }
  private int getPinAbsolute(int pinNumRelative) {
    int rawAbsolute = pinNumRelative + rotationalPosition;
    return modulusRotation(rawAbsolute);
  }

  public int forwardPass(int forwardPinAbsolute) {
    int inputPinRelative = getPinRelative(forwardPinAbsolute);
    int outputPinRelative = wiring.get(inputPinRelative);
    int outputPinAbsolute = getPinAbsolute(outputPinRelative);
    return outputPinAbsolute;
  }
  public int reversePass(int reversePinAbsolute) {
    int inputPinRelative = getPinRelative(reversePinAbsolute);
    int outputPinRelative = wiring.getReverse(inputPinRelative);
    int outputPinAbsolute = getPinAbsolute(outputPinRelative);
    return outputPinAbsolute;
  }

  public void rotate() {
    boolean completedRotation = (rotationalPosition + 1) >= numContactPoints;
    if (completedRotation && nextRotor != null)
      nextRotor.rotate();

    rotationalPosition = modulusRotation(rotationalPosition + 1);
  }

  public int getRotationalPosition() {
    return this.rotationalPosition;
  }

  public void reset() {
    rotationalPosition = startingPosition;
  }
}
