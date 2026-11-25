package validators;

import utils.SelfMap;

/**
 * Validation class to enforce certain rules on a Plugboard object.
 */
public class PlugboardValidator implements Validator<SelfMap<Integer>> {
  static public class PlugboardException extends ValidatorException {
    public PlugboardException(String message) { super(message); }
  }

  private int numContactPoints;

  public PlugboardValidator(int numContactPoints) {
    this.numContactPoints = numContactPoints;
  }

  /**
   * Validate a plugboard's mappings.
   * @param plugboard The mapping to validate.
   */
  public void validate(SelfMap<Integer> plugboard) {
    // reflector can not map to self
    boolean identityMappings = plugboard.areIdentityMappingsAllowed();
    if (!identityMappings)
      throw new PlugboardException("Plugboard's points must be allowed to map to self.");

    // is everything mapped from [0, numContactPoints) ?
    for (int i = 0; i < numContactPoints; i++)
      if (!plugboard.containsKey(i))
        throw new PlugboardException("Some pins were not mapped!");
  }
}
