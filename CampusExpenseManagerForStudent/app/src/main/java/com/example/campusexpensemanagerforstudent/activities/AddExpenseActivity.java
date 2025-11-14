package com.example.campusexpensemanagerforstudent.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanagerforstudent.R;
import com.example.campusexpensemanagerforstudent.models.Category;
import com.example.campusexpensemanagerforstudent.utils.DatabaseHelper;
import com.example.campusexpensemanagerforstudent.utils.SessionManager;
import com.example.campusexpensemanagerforstudent.utils.ValidationHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    private TextInputEditText etAmount, etDescription, etDate, etTime;
    private TextInputLayout tilAmount, tilDescription, tilDate, tilTime;
    private Spinner spinnerCategory;
    private Button btnSave, btnCancel;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private List<Category> categories;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();

        initViews();
        loadCategories();
        setDefaultDateTime();

        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());
        btnSave.setOnClickListener(v -> handleSaveExpense());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etAmount = findViewById(R.id.et_amount);
        etDescription = findViewById(R.id.et_description);
        etDate = findViewById(R.id.et_date);
        etTime = findViewById(R.id.et_time);

        tilAmount = findViewById(R.id.til_amount);
        tilDescription = findViewById(R.id.til_description);
        tilDate = findViewById(R.id.til_date);
        tilTime = findViewById(R.id.til_time);

        spinnerCategory = findViewById(R.id.spinner_category);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void loadCategories() {
        categories = dbHelper.getAllCategories();
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setDefaultDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        etDate.setText(dateFormat.format(calendar.getTime()));
        etTime.setText(timeFormat.format(calendar.getTime()));
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    etDate.setText(dateFormat.format(calendar.getTime()));
                }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    calendar.set(Calendar.MINUTE, selectedMinute);

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    etTime.setText(timeFormat.format(calendar.getTime()));
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void handleSaveExpense() {
        String amountStr = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        int categoryPosition = spinnerCategory.getSelectedItemPosition();

        // Clear errors
        tilAmount.setError(null);
        tilDescription.setError(null);

        // Validate
        boolean isValid = true;

        if (!ValidationHelper.isValidAmount(amountStr)) {
            tilAmount.setError(getString(R.string.error_invalid_amount));
            isValid = false;
        }

        if (!ValidationHelper.isNotEmpty(description)) {
            tilDescription.setError(getString(R.string.error_empty_field));
            isValid = false;
        }

        if (categoryPosition < 0) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (!isValid) return;

        double amount = Double.parseDouble(amountStr);
        int categoryId = categories.get(categoryPosition).getId();
        int userId = sessionManager.getUserId();

        long expenseId = dbHelper.insertExpense(userId, categoryId, amount, description,
                date, time, null);

        if (expenseId > 0) {
            Toast.makeText(this, getString(R.string.expense_added), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
        }
    }
}
