package validators;

import utils.PairMap;

public class RotorWiringValidator implements Validator<PairMap<Integer, Integer>> {
  private int numContactPoints;

  public RotorWiringValidator(int numContactPoints) {
    this.numContactPoints = numContactPoints;
  }

  public boolean validate(PairMap<Integer, Integer> mapping) {
    // do we have exactly the amount of mappings?
    boolean sufficientMappings = mapping.size() == numContactPoints;
    if (!sufficientMappings) return false;

    // is everything mapped from [0, numContactPoints) ?
    for (int i = 0; i < numContactPoints; i++)
      if (!mapping.containsKey(i) || !mapping.containsKeyReverse(i))
        return false;

    return true;
  }
}
