package validators;

/**
 * A common error class to use on validation errors.
 * extends {@link RuntimeException} because we need not mark everything with throws.
 */
public class ValidatorException extends RuntimeException {
  public ValidatorException(String message) {
    super(message);
  }
}
