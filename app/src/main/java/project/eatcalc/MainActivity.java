package project.eatcalc;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        int breakfastHour = prefs.getInt("breakfastHour", 7);
        int lunchHour = prefs.getInt("lunchHour", 13);
        int dinnerHour = prefs.getInt("dinnerHour", 18);
        boolean isThemes = prefs.getBoolean("isThemes", true);
        int themeNo = prefs.getInt("themeNo", R.style.Theme_EatCalcProject);
        Calendar rightNow = Calendar.getInstance();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
        if((currentHourIn24Format==breakfastHour || currentHourIn24Format==dinnerHour) && isThemes)
            setTheme(R.style.Theme_EatCalcProject_Yellow);
        else if(currentHourIn24Format==lunchHour && isThemes)
            setTheme(R.style.Theme_EatCalcProject_Red);
        else
            setTheme(themeNo);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1
            );
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);
    }

}