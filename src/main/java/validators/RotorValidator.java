package validators;

import components.Rotor;

/**
 * Validation class enforcing certain rules on a Rotor object.
 */
public class RotorValidator implements Validator<Rotor> {
  static public class RotorException extends ValidatorException {
    public RotorException(String message) { super(message); }
  }

  private int numContactPoints;

  public RotorValidator(int numContactPoints) {
    this.numContactPoints = numContactPoints;
  }

  /**
   * Validate a certain rotor.
   * @param rotor The rotor to validate.
   */
  public void validate(Rotor rotor) {
    boolean sufficientMappings = rotor.getNumContactPoints() == this.numContactPoints;
    if (!sufficientMappings)
      throw new RotorException("Rotor has differing number of pins than expected!");
  }
}
