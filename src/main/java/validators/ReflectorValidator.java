package validators;

import utils.SelfMap;

public class ReflectorValidator implements Validator<SelfMap<Integer>> {
  private int numContactPoints;

  public ReflectorValidator(int numContactPoints) {
    this.numContactPoints = numContactPoints;
  }

  public boolean validate(SelfMap<Integer> reflector) {
    // reflector can not map to self
    boolean identityMappings = reflector.areIdentityMappingsAllowed();
    if (identityMappings) return false;

    // do we have exactly the amount of mappings?
    boolean sufficientMappings = (reflector.size() * 2) == numContactPoints;
    if (!sufficientMappings) return false;

    // is everything mapped from [0, numContactPoints) ?
    for (int i = 0; i < numContactPoints; i++)
      if (!reflector.containsKey(i))
        return false;

    return true;
  }
}
