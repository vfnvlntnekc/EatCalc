package project.eatcalc.validation;

import android.content.Context;

import project.eatcalc.R;

public class NonNegativeValidator implements Validator<Double> {

    Context context;

    public NonNegativeValidator(Context context) {
        this.context = context;
    }

    @Override
    public boolean isValid(Double value) {
        return value >= 0;
    }

    @Override
    public String getMessage() {
        return context.getString(R.string.must_be_non_negative);
    }
}
