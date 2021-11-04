package com.example.mytimetable.login;

import static com.example.mytimetable.utils.NavigationUtils.checkAccountAccessLevel;
import static com.example.mytimetable.utils.NavigationUtils.closeKeyboard;
import static com.example.mytimetable.utils.NavigationUtils.getAccountID;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytimetable.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private TextInputLayout emailAddress,
            passwordField;
    private TextView btnLogin,
            btnRegister,
            btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.text_login_page);
        firebaseAuth = FirebaseAuth.getInstance();
        initializeViews();
        attachBtnLoginListeners();
        attachBtnRegisterListeners();
        attachBtnResetPasswordListeners();
    }

    //Method for initializing the XML components
    private void initializeViews() {
        progressBar = findViewById(R.id.login_progress_bar);
        emailAddress = findViewById(R.id.layout_login_email);
        passwordField = findViewById(R.id.layout_login_password);
        btnLogin = findViewById(R.id.button_login);
        btnRegister = findViewById(R.id.button_register);
        btnResetPassword = findViewById(R.id.button_reset_password);
    }

    //Listener for the Login Button
    private void attachBtnLoginListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    closeKeyboard(LoginActivity.this, v);
                    progressBar.setVisibility(View.VISIBLE);
                    emailAddress.setError(null);
                    passwordField.setError(null);
                    String sEmailAddress = emailAddress.getEditText().getText().toString();
                    String sEmailPassword = passwordField.getEditText().getText().toString();

                    firebaseAuth.signInWithEmailAndPassword(sEmailAddress, sEmailPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        checkAccountAccessLevel(LoginActivity.this, getAccountID());
                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        emailAddress.setError("Invalid Account");
                                        passwordField.setError("Invalid Account");
                                    }
                                }
                            });
                } catch (Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    emailAddress.setError("Invalid Account!");
                    passwordField.setError("Invalid Account!");
                }
            }
        });
    }

    //Listener for the Register Button
    private void attachBtnRegisterListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(LoginActivity.this, v);
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    //Listener for the Reset Password Button
    private void attachBtnResetPasswordListeners() {
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(LoginActivity.this, v);
                //Creating a reset password Alert Dialog requesting the email address
                EditText email = new EditText(LoginActivity.this);
                new MaterialAlertDialogBuilder(LoginActivity.this)
                        .setTitle("Reset Password")
                        .setMessage("Your email address:")
                        .setIcon(android.R.drawable.ic_dialog_email)
                        .setView(email)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    closeKeyboard(LoginActivity.this, v);
                                    String sEmail = email.getText().toString().trim();
                                    firebaseAuth.sendPasswordResetEmail(sEmail)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(LoginActivity.this, "Reset link has been sent!", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } catch (Exception ignored) {
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
    }
}
