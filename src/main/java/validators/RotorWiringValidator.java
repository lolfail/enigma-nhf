package validators;

import utils.PairMap;

public class RotorWiringValidator implements Validator<PairMap<Integer, Integer>> {
  static public class RotorWiringException extends ValidatorException {
    public RotorWiringException(String message) { super(message); }
  }

  private int numContactPoints;

  public RotorWiringValidator(int numContactPoints) {
    this.numContactPoints = numContactPoints;
  }

  public void validate(PairMap<Integer, Integer> mapping) {
    // do we have exactly the amount of mappings?
    boolean sufficientMappings = mapping.size() == numContactPoints;
    if (!sufficientMappings)
      throw new RotorWiringException("Improper number of mappings!");

    // is everything mapped from [0, numContactPoints) ?
    for (int i = 0; i < numContactPoints; i++)
      if (!mapping.containsKey(i) || !mapping.containsKeyReverse(i))
        throw new RotorWiringException("Some pins were not mapped!");
  }
}
