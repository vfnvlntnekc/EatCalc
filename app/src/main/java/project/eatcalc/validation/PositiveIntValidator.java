package project.eatcalc.validation;

import android.content.Context;

import project.eatcalc.R;

public class PositiveIntValidator implements Validator<Integer>{
    Context context;

    public PositiveIntValidator(Context context) {
        this.context = context;
    }

    @Override
    public boolean isValid(Integer value) {
        return value > 0;
    }

    @Override
    public String getMessage() {
        return context.getString(R.string.must_be_positive);
    }
}
