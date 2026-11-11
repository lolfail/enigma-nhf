package validators;

import components.Rotor;

public class RotorValidator implements Validator<Rotor> {
  static public class RotorException extends ValidatorException {
    public RotorException(String message) { super(message); }
  }

  private int numContactPoints;

  public RotorValidator(int numContactPoints) {
    this.numContactPoints = numContactPoints;
  }

  public void validate(Rotor rotor) {
    boolean sufficientMappings = rotor.getNumContactPoints() == this.numContactPoints;
    if (!sufficientMappings)
      throw new RotorException("Rotor has differing number of pins than expected!");
  }
}
