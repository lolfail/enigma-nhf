package validators;

public interface Validator<T> {
  public boolean validate(T obj);
}
