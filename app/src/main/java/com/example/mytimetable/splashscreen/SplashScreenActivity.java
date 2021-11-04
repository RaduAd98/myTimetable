package com.example.mytimetable.splashscreen;

import static com.example.mytimetable.utils.NavigationUtils.checkAccountAccessLevel;
import static com.example.mytimetable.utils.NavigationUtils.getAccountID;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.mytimetable.login.LoginActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkTheme();
        super.onCreate(savedInstanceState);
        redirectUser();
    }

    //Method that redirects user to the Login Page or to the logged in Account
    private void redirectUser() {
        String currentUserID = getAccountID();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (currentUserID == null) {
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
            finish();
        } else {
            checkAccountAccessLevel(SplashScreenActivity.this, currentUserID);
        }
    }

    //Method that sets the theme according to the previous state
    private void checkTheme() {
        SharedPreferences settings = getSharedPreferences("Selection", 0);
        boolean buttonOff = settings.getBoolean("buttonOff", false);
        boolean buttonOn = settings.getBoolean("buttonOn", false);
        if (buttonOff) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (buttonOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}