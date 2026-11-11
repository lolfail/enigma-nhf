package validators;

public interface Validator<T> {
  public void validate(T obj);
}
