package validators;

import components.Rotor;

public class RotorValidator implements Validator<Rotor> {
  private int numContactPoints;

  public RotorValidator(int numContactPoints) {
    this.numContactPoints = numContactPoints;
  }

  public boolean validate(Rotor rotor) {
    return rotor.getNumContactPoints() == this.numContactPoints;
  }
}
