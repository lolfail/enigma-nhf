package validators;

import utils.SelfMap;

public class ReflectorValidator implements Validator<SelfMap<Integer>> {
  static public class ReflectorException extends ValidatorException {
    public ReflectorException(String message) { super(message); }
  }

  private int numContactPoints;

  public ReflectorValidator(int numContactPoints) {
    this.numContactPoints = numContactPoints;
  }

  public void validate(SelfMap<Integer> reflector) {
    // reflector can not map to self
    boolean identityMappings = reflector.areIdentityMappingsAllowed();
    if (identityMappings)
      throw new ReflectorException("Reflector's points can not be wired to itself!");

    // do we have exactly the amount of mappings?
    boolean sufficientMappings = (reflector.size() * 2) == numContactPoints;
    if (!sufficientMappings)
      throw new ReflectorException("Improper number of mappings!");

    // is everything mapped from [0, numContactPoints) ?
    for (int i = 0; i < numContactPoints; i++)
      if (!reflector.containsKey(i))
        throw new ReflectorException("Some pins were not mapped!");
  }
}
