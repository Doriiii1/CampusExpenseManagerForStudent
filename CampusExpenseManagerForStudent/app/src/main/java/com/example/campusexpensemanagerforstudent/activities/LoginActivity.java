package com.example.campusexpensemanagerforstudent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanagerforstudent.R;
import com.example.campusexpensemanagerforstudent.models.User;
import com.example.campusexpensemanagerforstudent.utils.DatabaseHelper;
import com.example.campusexpensemanagerforstudent.utils.PasswordHelper;
import com.example.campusexpensemanagerforstudent.utils.SessionManager;
import com.example.campusexpensemanagerforstudent.utils.ValidationHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private TextInputLayout tilEmail, tilPassword;
    private Button btnLogin, btnGoToRegister;
    private CheckBox cbRememberMe;
    private ImageButton btnTogglePassword;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if already logged in
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        initViews();
        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> handleLogin());
        btnGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoToRegister = findViewById(R.id.btn_go_to_register);
        cbRememberMe = findViewById(R.id.cb_remember_me);
        btnTogglePassword = findViewById(R.id.btn_toggle_password);
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Clear previous errors
        tilEmail.setError(null);
        tilPassword.setError(null);

        // Validate inputs
        if (!ValidationHelper.isNotEmpty(email)) {
            tilEmail.setError(getString(R.string.error_empty_field));
            return;
        }

        if (!ValidationHelper.isValidEmail(email)) {
            tilEmail.setError(getString(R.string.error_invalid_email));
            return;
        }

        if (!ValidationHelper.isNotEmpty(password)) {
            tilPassword.setError(getString(R.string.error_empty_field));
            return;
        }

        // Check if account is locked
        if (sessionManager.isAccountLocked()) {
            long remainingTime = sessionManager.getRemainingLockTime();
            long minutes = remainingTime / (60 * 1000);
            Toast.makeText(this, getString(R.string.error_account_locked) + " (" + minutes + " minutes)",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Verify credentials
        User user = dbHelper.getUserByEmail(email);
        if (user == null || !PasswordHelper.verifyPassword(password, user.getPasswordHash())) {
            sessionManager.incrementFailedAttempts();
            int attempts = sessionManager.getFailedAttempts();

            if (attempts >= 3) {
                Toast.makeText(this, getString(R.string.error_account_locked), Toast.LENGTH_LONG).show();
            } else {
                tilPassword.setError(getString(R.string.error_login_failed));
                Toast.makeText(this, "Attempt " + attempts + " of 3", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Login successful
        sessionManager.resetFailedAttempts();
        sessionManager.createLoginSession(user.getId(), user.getEmail(), user.getName(),
                cbRememberMe.isChecked());

        Toast.makeText(this, "Welcome, " + user.getName() + "!", Toast.LENGTH_SHORT).show();
        navigateToMain();
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye_off);
        } else {
            etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_eye);
        }
        etPassword.setSelection(etPassword.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
