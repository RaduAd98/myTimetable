package com.example.mytimetable.teacher_main;

import static com.example.mytimetable.utils.NavigationUtils.closeKeyboard;
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

public class TeacherMainActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_MODULE = 100;
    public static final int REQUEST_EDIT_MODULE_MEETING = 101;

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private TextView navFullName,
            navEmailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);
        initializeViews();
        setTextViewNameEmail(navFullName, navEmailAddress);
        signOutDialog(navigationView.getMenu().findItem(R.id.nav_logout), TeacherMainActivity.this);
    }

    //Method for initializing the XML components
    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar_teacher);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_teacher);
        navigationView = findViewById(R.id.nav_view_teacher);
        View headerView = navigationView.getHeaderView(0);
        navFullName = headerView.findViewById(R.id.nav_bar_fullName_teacher);
        navEmailAddress = headerView.findViewById(R.id.nav_bar_email_teacher);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_add_module, R.id.nav_add_room, R.id.nav_create_group)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_teacher);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.teacher_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        closeKeyboard(this, navigationView);
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(TeacherMainActivity.this, DarkModeActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_teacher);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}