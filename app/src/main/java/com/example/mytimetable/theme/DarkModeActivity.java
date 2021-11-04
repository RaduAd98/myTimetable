package com.example.mytimetable.theme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.mytimetable.R;

public class DarkModeActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButtonOff,
            radioButtonOn,
            radioButtonSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dark_mode);

        initializeViews();

        setTitle("Dark Mode");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkSelection();
        changeTheme();

    }

    //Method that offers an Action Bar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //Method for initializing the XML components
    private void initializeViews() {
        radioGroup = findViewById(R.id.radio_group_dark_mode);
        radioButtonOff = findViewById(R.id.radio_button_dark_mode_off);
        radioButtonOn = findViewById(R.id.radio_button_dark_mode_on);
        radioButtonSystem = findViewById(R.id.radio_button_dark_mode_system);
    }

    //Listener for Radio Group that changes theme according to the selection
    private void changeTheme() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences settings = getSharedPreferences("Selection", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();

                switch (checkedId) {

                    case R.id.radio_button_dark_mode_off:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        editor.putBoolean("buttonOff", radioButtonOff.isChecked());
                        break;

                    case R.id.radio_button_dark_mode_on:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        editor.putBoolean("buttonOn", radioButtonOn.isChecked());
                        break;

                    default:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        break;

                }
                editor.apply();
            }
        });
    }

    //Method that checks the specific Radio Button according to the previous state
    private void checkSelection() {
        SharedPreferences settings = getSharedPreferences("Selection", 0);
        boolean buttonOff = settings.getBoolean("buttonOff", false);
        boolean buttonOn = settings.getBoolean("buttonOn", false);
        if (buttonOff) {
            radioButtonOff.setChecked(true);
        } else if (buttonOn) {
            radioButtonOn.setChecked(true);
        } else {
            radioButtonSystem.setChecked(true);
        }
    }
}
