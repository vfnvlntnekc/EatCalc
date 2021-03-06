package project.eatcalc.validation;

import java.util.Arrays;
import java.util.List;

public class ValidatorsComposer<T> implements Validator<T> {

    private final List<Validator<T>> validators;
    private String message;

    public ValidatorsComposer(Validator<T>... validators) {
        this.validators = Arrays.asList(validators);
    }

    @Override
    public boolean isValid(T value) {
        for(Validator<T> validator : validators) {
            if(!validator.isValid(value)) {
                this.message = validator.getMessage();
                return false;
            }
        }
        return true;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
