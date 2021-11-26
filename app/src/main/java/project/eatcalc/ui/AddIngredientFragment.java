package project.eatcalc.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import project.eatcalc.R;
import project.eatcalc.datastorage.DatabaseHelper;
import project.eatcalc.datastorage.IngredientModel;
import project.eatcalc.validation.EmptyValidator;
import project.eatcalc.validation.NonNegativeValidator;
import project.eatcalc.validation.PositiveValidator;
import project.eatcalc.validation.Validator;
import project.eatcalc.validation.ValidatorsComposer;

public class AddIngredientFragment extends Fragment {
    private boolean isError = false;
    private TextInputEditText countEditText, nameEditText, priceEditText, caloriesEditText, proteinsEditText, fatsEditText, carbsEditText;
    private Spinner unitsSpinner, currencyEditText, ingredientType;
    private Context context;
    private DatabaseHelper databaseHelper;
    private FragmentsViewModel viewModel;
    private String updateId;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.acticity_add_ingredient, container, false);
        context = view.getContext();
        viewModel = new ViewModelProvider(requireActivity()).get(FragmentsViewModel.class);
        databaseHelper = new DatabaseHelper(context);

        countEditText    = view.findViewById(R.id.add_ingredient_count);
        unitsSpinner     = view.findViewById(R.id.add_ingredient_units);
        nameEditText     = view.findViewById(R.id.add_ingredient_name);
        priceEditText    = view.findViewById(R.id.add_ingredient_edit_price);
        currencyEditText = view.findViewById(R.id.add_ingredient_currency);
        caloriesEditText = view.findViewById(R.id.calories);
        proteinsEditText = view.findViewById(R.id.proteins);
        fatsEditText     = view.findViewById(R.id.fats);
        carbsEditText    = view.findViewById(R.id.carbs);
        ingredientType   = view.findViewById(R.id.ingredient_icon);

        updateId = viewModel.getId().getValue();
        if(updateId!=null) {
            IngredientModel toUpdate = databaseHelper.getIngredientById(updateId);
            int unit = Arrays.asList(getResources().getStringArray(R.array.units)).indexOf(toUpdate.unit_id);
            int cur = Arrays.asList(getResources().getStringArray(R.array.currency)).indexOf(toUpdate.currency_id);
            nameEditText.setText(toUpdate.name);
            countEditText.setText(String.valueOf(toUpdate.count));
            unitsSpinner.setSelection(unit);
            priceEditText.setText(String.valueOf(toUpdate.price));
            currencyEditText.setSelection(cur);
            ingredientType.setSelection(toUpdate.icon);
            caloriesEditText.setText(String.valueOf(toUpdate.calories));
            proteinsEditText.setText(String.valueOf(toUpdate.proteins));
            fatsEditText.setText(String.valueOf(toUpdate.fats));
            carbsEditText.setText(String.valueOf(toUpdate.carbohydrates));
        } else {
            caloriesEditText.setText(String.valueOf(0.0));
            proteinsEditText.setText(String.valueOf(0.0));
            fatsEditText.setText(String.valueOf(0.0));
            carbsEditText.setText(String.valueOf(0.0));
        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.setLastFrag(FragmentsViewModel.ADD_INGREDIENT);
        final Button button_submit = view.findViewById(R.id.save_new_ingredient);
        button_submit.setOnClickListener(v -> {
            isError = false;

            double count, price, calories, proteins, fats, carbs;
            // TODO: использовать массивы View
            String units = unitsSpinner.getSelectedItem().toString();
            Editable editableNameEditText = nameEditText.getText();
            String currency = currencyEditText.getSelectedItem().toString();

            String name =     editableNameEditText!=null? editableNameEditText.toString():"";

            Validator<Double> nonNegative = new NonNegativeValidator(context);
            Validator<Double> positiveValidator = new PositiveValidator(context);
            try {
                count = Double.parseDouble(countEditText.getText().toString());
            } catch (Exception e) {
                count = 0;
                isError = true;
                countEditText.setError(context.getString(R.string.input_number));
            }try {
                calories = Double.parseDouble(caloriesEditText.getText().toString());
            } catch (Exception e) {
                calories = 0;
                isError = true;
                caloriesEditText.setError(context.getString(R.string.input_number));
            }
            try {
                proteins = Double.parseDouble(proteinsEditText.getText().toString());
            } catch (Exception e) {
                proteins = 0;
                isError = true;
                proteinsEditText.setError(context.getString(R.string.input_number));
            }
            try {
                fats = Double.parseDouble(fatsEditText.getText().toString());
            } catch (Exception e) {
                fats = 0;
                isError = true;
                fatsEditText.setError(context.getString(R.string.input_number));
            }
            try {
                carbs = Double.parseDouble(carbsEditText.getText().toString());
            } catch (Exception e) {
                carbs = 0;
                isError = true;
                carbsEditText.setError(context.getString(R.string.input_number));
            }


            if(!positiveValidator.isValid(count)) {
                isError = true;
                countEditText.setError(positiveValidator.getMessage());
            }
            if(!nonNegative.isValid(calories)) {
                isError = true;
                caloriesEditText.setError(positiveValidator.getMessage());
            }
            if(!nonNegative.isValid(proteins)) {
                isError = true;
                proteinsEditText.setError(positiveValidator.getMessage());
            }
            if(!nonNegative.isValid(fats)) {
                isError = true;
                fatsEditText.setError(positiveValidator.getMessage());
            }
            if(!nonNegative.isValid(carbs)) {
                isError = true;
                carbsEditText.setError(positiveValidator.getMessage());
            }
            try {
                price = Double.parseDouble(priceEditText.getText().toString());
            } catch (Exception e) {
                price = 0;
                isError = true;
                priceEditText.setError(context.getString(R.string.input_number));
            }
            if(!nonNegative.isValid(price)) {
                isError = true;
                priceEditText.setError(nonNegative.getMessage());
            }
            ValidatorsComposer<String> validation = new ValidatorsComposer<>(new EmptyValidator(context));
            if(!validation.isValid(name)) {
                nameEditText.setError(validation.getMessage());
                isError = true;
            }
            int type = ingredientType.getSelectedItemPosition();
            if(!isError) {
                // TODO: извлечь строки в переменные
                if(viewModel.getIdIngredient().getValue()!=null){
                    databaseHelper.updateIngredient(viewModel.getIdIngredient().getValue(),1,name,price/count,units,
                            currency,calories/count,proteins/count,
                            fats/count,carbs/count, type);
                } else {
                    if(updateId==null) {
                        databaseHelper.addIngredient(1, name, price / count, units,
                                currency, calories / count, proteins / count,
                                fats / count, carbs / count, type);
                    }else{
                        databaseHelper.updateIngredient(updateId, 1, name, price / count, units,
                                currency, calories / count, proteins / count,
                                fats / count, carbs / count, type);
                    }
                }
                viewModel.setIdIngredient(null);
                getParentFragmentManager().popBackStackImmediate();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
        viewModel.setId(null);
    }
}
