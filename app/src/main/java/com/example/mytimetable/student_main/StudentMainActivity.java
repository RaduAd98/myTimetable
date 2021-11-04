package com.example.mytimetable.student_main;

import static com.example.mytimetable.utils.NavigationUtils.setTextViewNameEmail;
import static com.example.mytimetable.utils.NavigationUtils.signOutDialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mytimetable.R;
import com.example.mytimetable.theme.DarkModeActivity;
import com.google.android.material.navigation.NavigationView;

public class StudentMainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private TextView navFullName,
            navEmailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);
        initializeViews();
        setTextViewNameEmail(navFullName, navEmailAddress);
        signOutDialog(navigationView.getMenu().findItem(R.id.nav_logout), StudentMainActivity.this);
    }

    //Method for initializing the XML components
    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navFullName = headerView.findViewById(R.id.nav_bar_fullName_student);
        navEmailAddress = headerView.findViewById(R.id.nav_bar_email_student);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_about)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.student_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(StudentMainActivity.this, DarkModeActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}