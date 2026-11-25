package validators;

/**
 * A simple interface to have validators of {@link EnigmaMachine}.
 */
public interface Validator<T> {
  /**
   * Validate the parameter, throw on issues.
   * @param obj The object to validate.
   */
  public void validate(T obj);
}
