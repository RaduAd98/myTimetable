package com.example.mytimetable.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytimetable.login.LoginActivity;
import com.example.mytimetable.login.RegisterActivity;
import com.example.mytimetable.student_main.StudentMainActivity;
import com.example.mytimetable.teacher_main.TeacherMainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class NavigationUtils {

    //Method that redirects the user according to the application state
    public static void checkAccountAccessLevel(Context context, String accountID) {
        FirebaseFirestore.getInstance()
                .collection("Accounts")
                .document(accountID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Boolean result = documentSnapshot.getBoolean("isAdmin");
                        String firstName = documentSnapshot.getString("fullName");
                        if (firstName.contains(" ")) {
                            firstName = firstName.substring(0, firstName.indexOf(" "));
                        }
                        if (result != null && result) {
                            if (context instanceof RegisterActivity) {
                                Toast.makeText(context, String.format("Teacher %s account created!", firstName), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, String.format("Teacher %s successfully logged in!", firstName), Toast.LENGTH_SHORT).show();
                            }

                            Intent intent = new Intent(context, TeacherMainActivity.class);
                            context.startActivity(intent);

                            ((Activity) context).finish();

                        } else if (result != null) {
                            if (context instanceof RegisterActivity) {
                                Toast.makeText(context, String.format("Student %s account created!", firstName), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, String.format("Student %s successfully logged in!", firstName), Toast.LENGTH_SHORT).show();
                            }

                            Intent intent = new Intent(context, StudentMainActivity.class);
                            context.startActivity(intent);

                            ((Activity) context).finish();
                        } else {
                            if (!(context instanceof LoginActivity)) {
                                context.startActivity(new Intent(context, LoginActivity.class));
                            }
                            Toast.makeText(context, "The account has been deleted by the University.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Method that shows the Sign Out Dialog to the user
    public static void signOutDialog(MenuItem item, Context context) {
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Logout")
                        .setMessage("Are you sure?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(context, LoginActivity.class);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                return true;
            }

        });
    }

    //Method that provides the UID
    public static String getAccountID() {
        return FirebaseAuth.getInstance().getUid();
    }

    //Method for initializing the Spinners that contain data stored in the Firestore database
    public static void initializeSpinner(Context context, Spinner spinner, List<String> items) {
        ArrayAdapter<String> spinnerAdapter
                = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }

    //Method that checks if the Teacher is in the Edit Module class to set spinners with old data
    public static void initializeSpinner(Context context, Spinner spinner, List<String> items,
                                         boolean isEditMode, String item) {

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(spinnerAdapter);
        if (isEditMode) {
            spinner.post(new Runnable() {
                public void run() {
                    spinner.setSelection(items.indexOf(item), true);
                }
            });
        }
    }

    //Method for initializing the Spinners that contain data stored in the Values file
    public static void initializeSpinner(Context context, Spinner spinner, int arrayResourceId) {
        ArrayAdapter<CharSequence> spinnerAdapter
                = ArrayAdapter.createFromResource(context, arrayResourceId, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }

    //Method for closing the keyboard when certain events occur
    public static void closeKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //Method for closing the keyboard when the view cannot be provided
    public static void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //Listener that closes the keyboard when a Spinner is touched
    public static void attachSpinnerCloseKeyboardListeners(Spinner spinner, Context context, View view) {
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboard(context, view);
                return false;
            }
        });
    }

    //Method that sets the navigation bar TextViews to the account name and email address
    public static void setTextViewNameEmail(TextView fullName, TextView emailAddress) {
        FirebaseFirestore.getInstance()
                .collection("Accounts")
                .document(getAccountID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        fullName.setText(documentSnapshot.getString("fullName"));
                        emailAddress.setText(documentSnapshot.getString("email"));
                    }
                });
    }
}