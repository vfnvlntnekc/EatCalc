package project.eatcalc.validation;

public interface Validator<T> {

    boolean isValid(T value); // TODO: invert method

    String getMessage();

}