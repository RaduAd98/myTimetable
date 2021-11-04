package com.example.mytimetable.login;

import static com.example.mytimetable.utils.NavigationUtils.checkAccountAccessLevel;
import static com.example.mytimetable.utils.NavigationUtils.closeKeyboard;
import static com.example.mytimetable.utils.NavigationUtils.getAccountID;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytimetable.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private RadioGroup radioGroup;
    private RadioButton radioButtonTeacher;
    private Button btnRegister;
    private ProgressBar progressBar;
    private View activityContainer;
    private TextInputLayout fullName,
            emailAddress,
            passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle(R.string.text_register_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initializeViews();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        attachBtnRegisterListeners();
    }

    //Method for initializing the XML components
    private void initializeViews() {
        fullName = findViewById(R.id.layout_register_fullName);
        emailAddress = findViewById(R.id.layout_register_email);
        passwordField = findViewById(R.id.layout_register_password);
        btnRegister = findViewById(R.id.button_register);
        progressBar = findViewById(R.id.register_progress_bar);
        radioGroup = findViewById(R.id.radio_group_buttons);
        radioButtonTeacher = findViewById(R.id.radio_button_teacher);
        activityContainer = findViewById(R.id.activity_container);
    }

    //Listener for the Register Button
    private void attachBtnRegisterListeners() {
        activityContainer.setOnFocusChangeListener(this);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sFullName = fullName.getEditText().getText().toString();
                String sEmailAddress = emailAddress.getEditText().getText().toString().trim();
                String sPasswordField = passwordField.getEditText().getText().toString().trim();
                if (sFullName.isEmpty()) {
                    fullName.setError("Full Name is required!");
                } else {
                    fullName.setError(null);
                }
                if (sEmailAddress.isEmpty()) {
                    emailAddress.setError("Email Address is required!");
                } else {
                    emailAddress.setError(null);
                }
                if (sPasswordField.isEmpty()) {
                    passwordField.setError("Password is required!");
                } else {
                    passwordField.setError(null);
                }
                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Account type is required!", Toast.LENGTH_SHORT).show();
                }
                if (!sFullName.isEmpty() && !sEmailAddress.isEmpty() &&
                        !sPasswordField.isEmpty() && radioGroup.getCheckedRadioButtonId() != -1) {
                    closeKeyboard(RegisterActivity.this, v);
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth
                            .createUserWithEmailAndPassword(sEmailAddress, sPasswordField)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //Collect the user ID (UID) and create a Cloud Firestore collection named "Accounts"
                                        DocumentReference documentReference =
                                                firebaseFirestore
                                                        .collection("Accounts")
                                                        .document(getAccountID());
                                        //Collect the data to be stored in the collection and set it up
                                        Map<String, Object> account = new HashMap<>();
                                        account.put("fullName", sFullName);
                                        account.put("email", sEmailAddress);
                                        if (radioButtonTeacher.isChecked()) {
                                            account.put("isAdmin", true);
                                        } else {
                                            account.put("isAdmin", false);
                                            account.put("hasGroup", false);
                                        }
                                        documentReference.set(account);
                                        checkAccountAccessLevel(RegisterActivity.this, getAccountID());
                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        fullName.setError("Full Name is required!");
                                        emailAddress.setError("Email Address is required!");
                                        passwordField.setError("Password is required!");
                                        Toast.makeText(RegisterActivity.this, "Already registered, invalid email or weak password!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        closeKeyboard(RegisterActivity.this);
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            closeKeyboard(RegisterActivity.this, view);
        }
    }
}
