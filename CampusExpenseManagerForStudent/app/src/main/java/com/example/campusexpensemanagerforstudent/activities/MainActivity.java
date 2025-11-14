package com.example.campusexpensemanagerforstudent.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.campusexpensemanagerforstudent.R;
import com.example.campusexpensemanagerforstudent.models.Budget;
import com.example.campusexpensemanagerforstudent.models.Expense;
import com.example.campusexpensemanagerforstudent.utils.DatabaseHelper;
import com.example.campusexpensemanagerforstudent.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome, tvTotalSpent, tvBudgetRemaining, tvTopCategory;
    private CardView cardAddExpense, cardViewExpenses, cardViewBudget, cardProfile;
    private FloatingActionButton fabAddExpense;
    private BottomNavigationView bottomNav;

    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if logged in
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        userId = sessionManager.getUserId();
        dbHelper = new DatabaseHelper(this);

        initViews();
        setupBottomNavigation();
        loadDashboardData();

        fabAddExpense.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddExpenseActivity.class));
        });

        cardAddExpense.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddExpenseActivity.class));
        });

        cardViewExpenses.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ExpenseListActivity.class));
        });

        cardViewBudget.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BudgetDashboardActivity.class));
        });

        cardProfile.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.isLoggedIn()) {
            loadDashboardData();
        }
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvTotalSpent = findViewById(R.id.tv_total_spent);
        tvBudgetRemaining = findViewById(R.id.tv_budget_remaining);
        tvTopCategory = findViewById(R.id.tv_top_category);

        cardAddExpense = findViewById(R.id.card_add_expense);
        cardViewExpenses = findViewById(R.id.card_view_expenses);
        cardViewBudget = findViewById(R.id.card_view_budget);
        cardProfile = findViewById(R.id.card_profile);

        fabAddExpense = findViewById(R.id.fab_add_expense);
        bottomNav = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_expenses) {
                startActivity(new Intent(MainActivity.this, ExpenseListActivity.class));
                return true;
            } else if (id == R.id.nav_budget) {
                startActivity(new Intent(MainActivity.this, BudgetDashboardActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadDashboardData() {
        String userName = sessionManager.getUserName();
        tvWelcome.setText(getString(R.string.welcome_message, userName));

        // Get current month
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String currentMonth = monthFormat.format(calendar.getTime());

        // Calculate total spent this month
        double totalSpent = dbHelper.getTotalExpensesByUserAndMonth(userId, currentMonth);
        NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvTotalSpent.setText(currencyFormat.format(totalSpent) + " đ");

        // Calculate budget remaining
        List<Budget> budgets = dbHelper.getBudgetsByUser(userId);
        double totalBudget = 0;
        double totalSpentInBudget = 0;

        for (Budget budget : budgets) {
            // Check if budget is current (active)
            if (isDateInRange(getCurrentDate(), budget.getStartDate(), budget.getEndDate())) {
                totalBudget += budget.getAmount();
                totalSpentInBudget += budget.getSpent();
            }
        }

        double remaining = totalBudget - totalSpentInBudget;
        tvBudgetRemaining.setText(currencyFormat.format(remaining) + " đ");

        // Find top category
        String topCategory = getTopCategory();
        tvTopCategory.setText(topCategory);
    }

    private String getTopCategory() {
        List<Expense> expenses = dbHelper.getExpensesByUser(userId);
        if (expenses.isEmpty()) {
            return getString(R.string.no_expenses);
        }

        // Count expenses by category
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : expenses) {
            String category = expense.getCategoryName();
            double amount = expense.getAmount();
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
        }

        // Find category with highest total
        String topCategory = null;
        double maxAmount = 0;
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            if (entry.getValue() > maxAmount) {
                maxAmount = entry.getValue();
                topCategory = entry.getKey();
            }
        }

        return topCategory != null ? topCategory : getString(R.string.no_expenses);
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    private boolean isDateInRange(String date, String startDate, String endDate) {
        return date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
