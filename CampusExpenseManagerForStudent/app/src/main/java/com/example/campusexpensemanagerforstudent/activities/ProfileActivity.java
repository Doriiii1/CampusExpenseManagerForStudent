package com.example.campusexpensemanagerforstudent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.campusexpensemanagerforstudent.R;
import com.example.campusexpensemanagerforstudent.models.User;
import com.example.campusexpensemanagerforstudent.utils.DatabaseHelper;
import com.example.campusexpensemanagerforstudent.utils.PasswordHelper;
import com.example.campusexpensemanagerforstudent.utils.SessionManager;
import com.example.campusexpensemanagerforstudent.utils.ValidationHelper;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etAddress, etPhone, etEmail;
    private TextInputEditText etOldPassword, etNewPassword, etConfirmNewPassword;
    private TextInputLayout tilName, tilAddress, tilPhone, tilEmail;
    private TextInputLayout tilOldPassword, tilNewPassword, tilConfirmNewPassword;
    private Button btnSaveProfile, btnChangePassword, btnLogout;
    private SwitchMaterial switchDarkMode;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private int userId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            finish();
            return;
        }

        userId = sessionManager.getUserId();
        dbHelper = new DatabaseHelper(this);

        initViews();
        loadUserData();
        setupDarkMode();

        btnSaveProfile.setOnClickListener(v -> handleSaveProfile());
        btnChangePassword.setOnClickListener(v -> handleChangePassword());
        btnLogout.setOnClickListener(v -> handleLogout());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sessionManager.setDarkMode(isChecked);
            applyDarkMode(isChecked);
        });
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etAddress = findViewById(R.id.et_address);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);

        tilName = findViewById(R.id.til_name);
        tilAddress = findViewById(R.id.til_address);
        tilPhone = findViewById(R.id.til_phone);
        tilEmail = findViewById(R.id.til_email);

        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmNewPassword = findViewById(R.id.et_confirm_new_password);

        tilOldPassword = findViewById(R.id.til_old_password);
        tilNewPassword = findViewById(R.id.til_new_password);
        tilConfirmNewPassword = findViewById(R.id.til_confirm_new_password);

        btnSaveProfile = findViewById(R.id.btn_save_profile);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnLogout = findViewById(R.id.btn_logout);
        switchDarkMode = findViewById(R.id.switch_dark_mode);
    }

    private void loadUserData() {
        currentUser = dbHelper.getUserById(userId);
        if (currentUser != null) {
            etEmail.setText(currentUser.getEmail());
            etName.setText(currentUser.getName());
            etAddress.setText(currentUser.getAddress());
            etPhone.setText(currentUser.getPhone());
            etEmail.setEnabled(false); // Email cannot be changed
        }
    }

    private void setupDarkMode() {
        boolean isDarkMode = sessionManager.isDarkModeEnabled();
        switchDarkMode.setChecked(isDarkMode);
    }

    private void handleSaveProfile() {
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Clear previous errors
        tilName.setError(null);
        tilAddress.setError(null);
        tilPhone.setError(null);

        // Validate
        boolean isValid = true;

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

        if (!isValid) return;

        // Update database
        int rows = dbHelper.updateUser(userId, name, address, phone);
        if (rows > 0) {
            sessionManager.updateUserName(name);
            Toast.makeText(this, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
            currentUser.setName(name);
            currentUser.setAddress(address);
            currentUser.setPhone(phone);
        } else {
            Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleChangePassword() {
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

        // Clear previous errors
        tilOldPassword.setError(null);
        tilNewPassword.setError(null);
        tilConfirmNewPassword.setError(null);

        // Validate
        boolean isValid = true;

        if (!ValidationHelper.isNotEmpty(oldPassword)) {
            tilOldPassword.setError(getString(R.string.error_empty_field));
            isValid = false;
        } else if (!PasswordHelper.verifyPassword(oldPassword, currentUser.getPasswordHash())) {
            tilOldPassword.setError("Incorrect old password");
            isValid = false;
        }

        if (!ValidationHelper.isNotEmpty(newPassword)) {
            tilNewPassword.setError(getString(R.string.error_empty_field));
            isValid = false;
        } else if (!ValidationHelper.isValidPassword(newPassword)) {
            tilNewPassword.setError(getString(R.string.error_password_short));
            isValid = false;
        }

        if (!ValidationHelper.isNotEmpty(confirmNewPassword)) {
            tilConfirmNewPassword.setError(getString(R.string.error_empty_field));
            isValid = false;
        } else if (!ValidationHelper.passwordsMatch(newPassword, confirmNewPassword)) {
            tilConfirmNewPassword.setError(getString(R.string.error_password_mismatch));
            isValid = false;
        }

        if (!isValid) return;

        // Hash new password and update
        String newPasswordHash = PasswordHelper.hashPassword(newPassword);
        if (newPasswordHash == null) {
            Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
            return;
        }

        int rows = dbHelper.updatePassword(userId, newPasswordHash);
        if (rows > 0) {
            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
            currentUser.setPasswordHash(newPasswordHash);
            etOldPassword.setText("");
            etNewPassword.setText("");
            etConfirmNewPassword.setText("");
        } else {
            Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLogout() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_logout_title))
                .setMessage(getString(R.string.confirm_logout_message))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void applyDarkMode(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        recreate(); // Recreate activity to apply theme
    }
}
