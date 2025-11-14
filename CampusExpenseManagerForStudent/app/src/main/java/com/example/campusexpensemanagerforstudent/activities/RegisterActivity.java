package com.example.campusexpensemanagerforstudent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanagerforstudent.R;
import com.example.campusexpensemanagerforstudent.utils.DatabaseHelper;
import com.example.campusexpensemanagerforstudent.utils.PasswordHelper;
import com.example.campusexpensemanagerforstudent.utils.ValidationHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etConfirmPassword, etName, etAddress, etPhone;
    private TextInputLayout tilEmail, tilPassword, tilConfirmPassword, tilName, tilAddress, tilPhone;
    private Button btnRegister, btnBackToLogin;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        dbHelper = new DatabaseHelper(this);

        // Add text watchers for real-time validation
        setupTextWatchers();

        btnRegister.setOnClickListener(v -> handleRegister());
        btnBackToLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etName = findViewById(R.id.et_name);
        etAddress = findViewById(R.id.et_address);
        etPhone = findViewById(R.id.et_phone);

        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        tilName = findViewById(R.id.til_name);
        tilAddress = findViewById(R.id.til_address);
        tilPhone = findViewById(R.id.til_phone);

        btnRegister = findViewById(R.id.btn_register);
        btnBackToLogin = findViewById(R.id.btn_back_to_login);
    }

    private void setupTextWatchers() {
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = etPassword.getText().toString();
                String confirmPassword = s.toString();

                if (!confirmPassword.isEmpty()) {
                    if (password.equals(confirmPassword)) {
                        tilConfirmPassword.setError(null);
                        tilConfirmPassword.setEndIconDrawable(R.drawable.ic_check);
                    } else {
                        tilConfirmPassword.setError(getString(R.string.error_password_mismatch));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void handleRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Clear previous errors
        clearErrors();

        // Validate all fields
        boolean isValid = true;

        if (!ValidationHelper.isNotEmpty(email)) {
            tilEmail.setError(getString(R.string.error_empty_field));
            isValid = false;
        } else if (!ValidationHelper.isValidEmail(email)) {
            tilEmail.setError(getString(R.string.error_invalid_email));
            isValid = false;
        } else if (dbHelper.emailExists(email)) {
            tilEmail.setError(getString(R.string.error_email_exists));
            isValid = false;
        }

        if (!ValidationHelper.isNotEmpty(password)) {
            tilPassword.setError(getString(R.string.error_empty_field));
            isValid = false;
        } else if (!ValidationHelper.isValidPassword(password)) {
            tilPassword.setError(getString(R.string.error_password_short));
            isValid = false;
        }

        if (!ValidationHelper.isNotEmpty(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.error_empty_field));
            isValid = false;
        } else if (!ValidationHelper.passwordsMatch(password, confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.error_password_mismatch));
            isValid = false;
        }

        if (!ValidationHelper.isNotEmpty(name)) {
            tilName.setError(getString(R.string.error_empty_field));
            isValid = false;
        }

        if (!ValidationHelper.isNotEmpty(address)) {
            tilAddress.setError(getString(R.string.error_empty_field));
            isValid = false;
        }

        if (!ValidationHelper.isNotEmpty(phone)) {
            tilPhone.setError(getString(R.string.error_empty_field));
            isValid = false;
        } else if (!ValidationHelper.isValidPhone(phone)) {
            tilPhone.setError(getString(R.string.error_invalid_phone));
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Hash password
        String passwordHash = PasswordHelper.hashPassword(password);
        if (passwordHash == null) {
            Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert user
        long userId = dbHelper.insertUser(email, passwordHash, name, address, phone);

        if (userId > 0) {
            Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearErrors() {
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        tilName.setError(null);
        tilAddress.setError(null);
        tilPhone.setError(null);
    }
}
