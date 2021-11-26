package project.eatcalc.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import project.eatcalc.R;
import project.eatcalc.validation.PositiveIntValidator;
import project.eatcalc.validation.Validator;

public class SettingsFragment extends Fragment {

    private TextInputEditText breakHour, lunchHour, dinnerHour;
    private SwitchMaterial isThemes;
    private Button save;
    private Spinner themeSpinner;
    private Context context;
    private boolean isValid = false;
    private boolean isError = false;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);
        context = view.getContext();
        breakHour = view.findViewById(R.id.breakfast_hour);
        lunchHour = view.findViewById(R.id.lunch_hour);
        dinnerHour = view.findViewById(R.id.dinner_hour);
        isThemes = view.findViewById(R.id.switch1);
        save = view.findViewById(R.id.save_settings);
        themeSpinner = view.findViewById(R.id.defaultThemeSpinner);

        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        int breakfastHourInt = prefs.getInt("breakfastHour", 7);
        int lunchHourInt = prefs.getInt("lunchHour", 13);
        int dinnerHourInt = prefs.getInt("dinnerHour", 18);
        boolean isThemesBool = prefs.getBoolean("isThemes", true);

        breakHour.setText(String.valueOf(breakfastHourInt));
        lunchHour.setText(String.valueOf(lunchHourInt));
        dinnerHour.setText(String.valueOf(dinnerHourInt));
        isThemes.setChecked(isThemesBool);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        save.setOnClickListener(v -> {
            isError = false;

            int breakfast, lunch, dinner;

            Validator<Integer> positiveValidator = new PositiveIntValidator(context);
            try {
                breakfast = Integer.parseInt(breakHour.getText().toString());
            } catch (Exception e) {
                breakfast = 7;
                isError = true;
                breakHour.setError(context.getString(R.string.input_number));
            }

            try {
                lunch = Integer.parseInt(lunchHour.getText().toString());
            } catch (Exception e) {
                lunch = 13;
                isError = true;
                lunchHour.setError(context.getString(R.string.input_number));
            }

            try {
                dinner = Integer.parseInt(dinnerHour.getText().toString());
            } catch (Exception e) {
                dinner = 18;
                isError = true;
                dinnerHour.setError(context.getString(R.string.input_number));
            }

            if (!positiveValidator.isValid(breakfast) && (breakfast < 24)) {
                isError = true;
                breakHour.setError(positiveValidator.getMessage());
            }
            if (!positiveValidator.isValid(lunch) && (lunch < 24)) {
                isError = true;
                lunchHour.setError(positiveValidator.getMessage());
            }
            if (!positiveValidator.isValid(dinner)) {
                isError = true;
                dinnerHour.setError(positiveValidator.getMessage());
            }

            if (!isError) {
                // TODO: извлечь строки в переменные
                boolean isChecked = isThemes.isChecked();
                isValid = true;
                SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("breakfastHour", breakfast).apply();
                editor.putInt("lunchHour", lunch).apply();
                editor.putInt("dinnerHour", dinner).apply();
                editor.putBoolean("isThemes", isChecked).apply();
                int themePos = themeSpinner.getSelectedItemPosition();
                int themeNo = 0;
                switch (themePos)
                {
                    case 0: themeNo = R.style.Theme_EatCalcProject;
                            break;
                    case 1:
                    case 3: themeNo=R.style.Theme_EatCalcProject_Yellow;
                            break;
                    case 2: themeNo=R.style.Theme_EatCalcProject_Red;
                            break;
                }
                editor.putInt("themeNo", themeNo).apply();
                Toast toast = Toast.makeText(context,
                        R.string.saved,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }
}
