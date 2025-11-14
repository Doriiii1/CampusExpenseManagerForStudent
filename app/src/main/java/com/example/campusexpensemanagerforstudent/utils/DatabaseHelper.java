package com.example.campusexpensemanagerforstudent.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.campusexpensemanagerforstudent.models.Budget;
import com.example.campusexpensemanagerforstudent.models.Category;
import com.example.campusexpensemanagerforstudent.models.Currency;
import com.example.campusexpensemanagerforstudent.models.Expense;
import com.example.campusexpensemanagerforstudent.models.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "campus_expense.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_EXPENSES = "expenses";
    private static final String TABLE_BUDGETS = "budgets";
    private static final String TABLE_CURRENCIES = "currencies";

    // Common Columns
    private static final String COL_ID = "id";
    private static final String COL_CREATED_AT = "created_at";

    // User Table Columns
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASSWORD = "password_hash";
    private static final String COL_USER_NAME = "name";
    private static final String COL_USER_ADDRESS = "address";
    private static final String COL_USER_PHONE = "phone";
    private static final String COL_USER_AVATAR = "avatar_path";

    // Category Table Columns
    private static final String COL_CATEGORY_NAME = "name";
    private static final String COL_CATEGORY_ICON = "icon_name";

    // Expense Table Columns
    private static final String COL_EXPENSE_USER_ID = "user_id";
    private static final String COL_EXPENSE_CATEGORY_ID = "category_id";
    private static final String COL_EXPENSE_AMOUNT = "amount";
    private static final String COL_EXPENSE_DESCRIPTION = "description";
    private static final String COL_EXPENSE_DATE = "date";
    private static final String COL_EXPENSE_RECEIPT = "receipt_path";
    private static final String COL_EXPENSE_CURRENCY_ID = "currency_id";

    // Budget Table Columns
    private static final String COL_BUDGET_USER_ID = "user_id";
    private static final String COL_BUDGET_CATEGORY_ID = "category_id";
    private static final String COL_BUDGET_AMOUNT = "amount";
    private static final String COL_BUDGET_PERIOD = "period";
    private static final String COL_BUDGET_START_DATE = "start_date";
    private static final String COL_BUDGET_END_DATE = "end_date";

    // Currency Table Columns
    private static final String COL_CURRENCY_CODE = "code";
    private static final String COL_CURRENCY_SYMBOL = "symbol";
    private static final String COL_CURRENCY_RATE = "exchange_rate";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_USER_PASSWORD + " TEXT NOT NULL, " +
                COL_USER_NAME + " TEXT NOT NULL, " +
                COL_USER_ADDRESS + " TEXT, " +
                COL_USER_PHONE + " TEXT, " +
                COL_USER_AVATAR + " TEXT, " +
                COL_CREATED_AT + " INTEGER NOT NULL)";
        db.execSQL(createUsersTable);

        // Create Categories Table
        String createCategoriesTable = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CATEGORY_NAME + " TEXT NOT NULL, " +
                COL_CATEGORY_ICON + " TEXT)";
        db.execSQL(createCategoriesTable);

        // Create Currencies Table
        String createCurrenciesTable = "CREATE TABLE " + TABLE_CURRENCIES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CURRENCY_CODE + " TEXT NOT NULL, " +
                COL_CURRENCY_SYMBOL + " TEXT NOT NULL, " +
                COL_CURRENCY_RATE + " REAL NOT NULL)";
        db.execSQL(createCurrenciesTable);

        // Create Expenses Table
        String createExpensesTable = "CREATE TABLE " + TABLE_EXPENSES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EXPENSE_USER_ID + " INTEGER NOT NULL, " +
                COL_EXPENSE_CATEGORY_ID + " INTEGER NOT NULL, " +
                COL_EXPENSE_AMOUNT + " REAL NOT NULL, " +
                COL_EXPENSE_DESCRIPTION + " TEXT, " +
                COL_EXPENSE_DATE + " INTEGER NOT NULL, " +
                COL_EXPENSE_RECEIPT + " TEXT, " +
                COL_EXPENSE_CURRENCY_ID + " INTEGER DEFAULT 1, " +
                COL_CREATED_AT + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COL_EXPENSE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + "), " +
                "FOREIGN KEY(" + COL_EXPENSE_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COL_ID + "), " +
                "FOREIGN KEY(" + COL_EXPENSE_CURRENCY_ID + ") REFERENCES " + TABLE_CURRENCIES + "(" + COL_ID + "))";
        db.execSQL(createExpensesTable);

        // Create Budgets Table
        String createBudgetsTable = "CREATE TABLE " + TABLE_BUDGETS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BUDGET_USER_ID + " INTEGER NOT NULL, " +
                COL_BUDGET_CATEGORY_ID + " INTEGER NOT NULL, " +
                COL_BUDGET_AMOUNT + " REAL NOT NULL, " +
                COL_BUDGET_PERIOD + " TEXT NOT NULL, " +
                COL_BUDGET_START_DATE + " INTEGER NOT NULL, " +
                COL_BUDGET_END_DATE + " INTEGER NOT NULL, " +
                COL_CREATED_AT + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COL_BUDGET_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + "), " +
                "FOREIGN KEY(" + COL_BUDGET_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COL_ID + "))";
        db.execSQL(createBudgetsTable);

        // Create indexes for better query performance
        db.execSQL("CREATE INDEX idx_expenses_user ON " + TABLE_EXPENSES + "(" + COL_EXPENSE_USER_ID + ")");
        db.execSQL("CREATE INDEX idx_expenses_date ON " + TABLE_EXPENSES + "(" + COL_EXPENSE_DATE + ")");
        db.execSQL("CREATE INDEX idx_budgets_user ON " + TABLE_BUDGETS + "(" + COL_BUDGET_USER_ID + ")");

        // Pre-populate Categories
        insertDefaultCategories(db);

        // Pre-populate Currencies
        insertDefaultCurrencies(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENCIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void insertDefaultCategories(SQLiteDatabase db) {
        String[] categories = {
                "Food & Dining", "Transportation", "Education", "Entertainment",
                "Shopping", "Health", "Utilities", "Accommodation",
                "Personal Care", "Other"
        };

        for (String category : categories) {
            ContentValues values = new ContentValues();
            values.put(COL_CATEGORY_NAME, category);
            values.put(COL_CATEGORY_ICON, "ic_category");
            db.insert(TABLE_CATEGORIES, null, values);
        }
    }

    private void insertDefaultCurrencies(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COL_CURRENCY_CODE, "VND");
        values.put(COL_CURRENCY_SYMBOL, "₫");
        values.put(COL_CURRENCY_RATE, 1.0);
        db.insert(TABLE_CURRENCIES, null, values);
    }

    // ==================== USER CRUD OPERATIONS ====================

    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_EMAIL, user.getEmail());
        values.put(COL_USER_PASSWORD, user.getPasswordHash());
        values.put(COL_USER_NAME, user.getName());
        values.put(COL_USER_ADDRESS, user.getAddress());
        values.put(COL_USER_PHONE, user.getPhone());
        values.put(COL_CREATED_AT, System.currentTimeMillis());
        return db.insert(TABLE_USERS, null, values);
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COL_USER_EMAIL + "=?",
                new String[]{email}, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)));
            user.setPasswordHash(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)));
            user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ADDRESS)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE)));
            user.setAvatarPath(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_AVATAR)));
            user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT)));
            cursor.close();
        }
        return user;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COL_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)));
            user.setPasswordHash(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)));
            user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ADDRESS)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE)));
            user.setAvatarPath(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_AVATAR)));
            user.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT)));
            cursor.close();
        }
        return user;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, user.getName());
        values.put(COL_USER_ADDRESS, user.getAddress());
        values.put(COL_USER_PHONE, user.getPhone());
        if (user.getAvatarPath() != null) {
            values.put(COL_USER_AVATAR, user.getAvatarPath());
        }
        return db.update(TABLE_USERS, values, COL_ID + "=?",
                new String[]{String.valueOf(user.getId())});
    }

    public int updateUserPassword(int userId, String newPasswordHash) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_PASSWORD, newPasswordHash);
        return db.update(TABLE_USERS, values, COL_ID + "=?",
                new String[]{String.valueOf(userId)});
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_ID},
                COL_USER_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        return exists;
    }

    // ==================== CATEGORY CRUD OPERATIONS ====================

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, COL_CATEGORY_NAME);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                category.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY_NAME)));
                category.setIconName(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY_ICON)));
                categories.add(category);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return categories;
    }

    public Category getCategoryById(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null, COL_ID + "=?",
                new String[]{String.valueOf(categoryId)}, null, null, null);

        Category category = null;
        if (cursor != null && cursor.moveToFirst()) {
            category = new Category();
            category.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            category.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY_NAME)));
            category.setIconName(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY_ICON)));
            cursor.close();
        }
        return category;
    }

    // ==================== EXPENSE CRUD OPERATIONS ====================

    public long insertExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EXPENSE_USER_ID, expense.getUserId());
        values.put(COL_EXPENSE_CATEGORY_ID, expense.getCategoryId());
        values.put(COL_EXPENSE_AMOUNT, expense.getAmount());
        values.put(COL_EXPENSE_DESCRIPTION, expense.getDescription());
        values.put(COL_EXPENSE_DATE, expense.getDate());
        values.put(COL_EXPENSE_RECEIPT, expense.getReceiptPath());
        values.put(COL_EXPENSE_CURRENCY_ID, expense.getCurrencyId());
        values.put(COL_CREATED_AT, System.currentTimeMillis());
        return db.insert(TABLE_EXPENSES, null, values);
    }

    public List<Expense> getExpensesByUser(int userId) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT e.*, c." + COL_CATEGORY_NAME + " FROM " + TABLE_EXPENSES + " e " +
                "INNER JOIN " + TABLE_CATEGORIES + " c ON e." + COL_EXPENSE_CATEGORY_ID + " = c." + COL_ID +
                " WHERE e." + COL_EXPENSE_USER_ID + " = ? ORDER BY e." + COL_EXPENSE_DATE + " DESC";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                expense.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_USER_ID)));
                expense.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_CATEGORY_ID)));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_EXPENSE_AMOUNT)));
                expense.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPENSE_DESCRIPTION)));
                expense.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(COL_EXPENSE_DATE)));
                expense.setReceiptPath(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPENSE_RECEIPT)));
                expense.setCurrencyId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_CURRENCY_ID)));
                expense.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT)));
                expense.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY_NAME)));
                expenses.add(expense);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return expenses;
    }

    public Expense getExpenseById(int expenseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.*, c." + COL_CATEGORY_NAME + " FROM " + TABLE_EXPENSES + " e " +
                "INNER JOIN " + TABLE_CATEGORIES + " c ON e." + COL_EXPENSE_CATEGORY_ID + " = c." + COL_ID +
                " WHERE e." + COL_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(expenseId)});

        Expense expense = null;
        if (cursor != null && cursor.moveToFirst()) {
            expense = new Expense();
            expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            expense.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_USER_ID)));
            expense.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_CATEGORY_ID)));
            expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_EXPENSE_AMOUNT)));
            expense.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPENSE_DESCRIPTION)));
            expense.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(COL_EXPENSE_DATE)));
            expense.setReceiptPath(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPENSE_RECEIPT)));
            expense.setCurrencyId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_CURRENCY_ID)));
            expense.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT)));
            expense.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY_NAME)));
            cursor.close();
        }
        return expense;
    }

    public int updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EXPENSE_CATEGORY_ID, expense.getCategoryId());
        values.put(COL_EXPENSE_AMOUNT, expense.getAmount());
        values.put(COL_EXPENSE_DESCRIPTION, expense.getDescription());
        values.put(COL_EXPENSE_DATE, expense.getDate());
        if (expense.getReceiptPath() != null) {
            values.put(COL_EXPENSE_RECEIPT, expense.getReceiptPath());
        }
        return db.update(TABLE_EXPENSES, values, COL_ID + "=?",
                new String[]{String.valueOf(expense.getId())});
    }

    public int deleteExpense(int expenseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EXPENSES, COL_ID + "=?",
                new String[]{String.valueOf(expenseId)});
    }

    public List<Expense> searchExpenses(int userId, String query) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String sqlQuery = "SELECT e.*, c." + COL_CATEGORY_NAME + " FROM " + TABLE_EXPENSES + " e " +
                "INNER JOIN " + TABLE_CATEGORIES + " c ON e." + COL_EXPENSE_CATEGORY_ID + " = c." + COL_ID +
                " WHERE e." + COL_EXPENSE_USER_ID + " = ? AND e." + COL_EXPENSE_DESCRIPTION + " LIKE ? " +
                "ORDER BY e." + COL_EXPENSE_DATE + " DESC";
        
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{String.valueOf(userId), "%" + query + "%"});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                expense.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_USER_ID)));
                expense.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_CATEGORY_ID)));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_EXPENSE_AMOUNT)));
                expense.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPENSE_DESCRIPTION)));
                expense.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(COL_EXPENSE_DATE)));
                expense.setReceiptPath(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPENSE_RECEIPT)));
                expense.setCurrencyId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXPENSE_CURRENCY_ID)));
                expense.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT)));
                expense.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY_NAME)));
                expenses.add(expense);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return expenses;
    }

    public double getTotalExpensesByUser(int userId, long startDate, long endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COL_EXPENSE_AMOUNT + ") FROM " + TABLE_EXPENSES +
                " WHERE " + COL_EXPENSE_USER_ID + " = ? AND " + COL_EXPENSE_DATE + " BETWEEN ? AND ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId),
                String.valueOf(startDate),
                String.valueOf(endDate)
        });

        double total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getDouble(0);
            cursor.close();
        }
        return total;
    }

    public double getTotalExpensesByCategory(int userId, int categoryId, long startDate, long endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COL_EXPENSE_AMOUNT + ") FROM " + TABLE_EXPENSES +
                " WHERE " + COL_EXPENSE_USER_ID + " = ? AND " + COL_EXPENSE_CATEGORY_ID + " = ? " +
                "AND " + COL_EXPENSE_DATE + " BETWEEN ? AND ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId),
                String.valueOf(categoryId),
                String.valueOf(startDate),
                String.valueOf(endDate)
        });

        double total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getDouble(0);
            cursor.close();
        }
        return total;
    }

    // ==================== BUDGET CRUD OPERATIONS ====================

    public long insertBudget(Budget budget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BUDGET_USER_ID, budget.getUserId());
        values.put(COL_BUDGET_CATEGORY_ID, budget.getCategoryId());
        values.put(COL_BUDGET_AMOUNT, budget.getAmount());
        values.put(COL_BUDGET_PERIOD, budget.getPeriod());
        values.put(COL_BUDGET_START_DATE, budget.getStartDate());
        values.put(COL_BUDGET_END_DATE, budget.getEndDate());
        values.put(COL_CREATED_AT, System.currentTimeMillis());
        return db.insert(TABLE_BUDGETS, null, values);
    }

    public List<Budget> getBudgetsByUser(int userId) {
        List<Budget> budgets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT b.*, c." + COL_CATEGORY_NAME + " FROM " + TABLE_BUDGETS + " b " +
                "INNER JOIN " + TABLE_CATEGORIES + " c ON b." + COL_BUDGET_CATEGORY_ID + " = c." + COL_ID +
                " WHERE b." + COL_BUDGET_USER_ID + " = ? ORDER BY b." + COL_CREATED_AT + " DESC";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Budget budget = new Budget();
                budget.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                budget.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_BUDGET_USER_ID)));
                budget.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_BUDGET_CATEGORY_ID)));
                budget.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BUDGET_AMOUNT)));
                budget.setPeriod(cursor.getString(cursor.getColumnIndexOrThrow(COL_BUDGET_PERIOD)));
                budget.setStartDate(cursor.getLong(cursor.getColumnIndexOrThrow(COL_BUDGET_START_DATE)));
                budget.setEndDate(cursor.getLong(cursor.getColumnIndexOrThrow(COL_BUDGET_END_DATE)));
                budget.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT)));
                budget.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY_NAME)));
                
                // Calculate spent amount
                double spent = getTotalExpensesByCategory(userId, budget.getCategoryId(),
                        budget.getStartDate(), budget.getEndDate());
                budget.setSpent(spent);
                budget.setRemaining(budget.getAmount() - spent);
                
                budgets.add(budget);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return budgets;
    }

    public Budget getBudgetById(int budgetId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.*, c." + COL_CATEGORY_NAME + " FROM " + TABLE_BUDGETS + " b " +
                "INNER JOIN " + TABLE_CATEGORIES + " c ON b." + COL_BUDGET_CATEGORY_ID + " = c." + COL_ID +
                " WHERE b." + COL_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(budgetId)});

        Budget budget = null;
        if (cursor != null && cursor.moveToFirst()) {
            budget = new Budget();
            budget.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            budget.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_BUDGET_USER_ID)));
            budget.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_BUDGET_CATEGORY_ID)));
            budget.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BUDGET_AMOUNT)));
            budget.setPeriod(cursor.getString(cursor.getColumnIndexOrThrow(COL_BUDGET_PERIOD)));
            budget.setStartDate(cursor.getLong(cursor.getColumnIndexOrThrow(COL_BUDGET_START_DATE)));
            budget.setEndDate(cursor.getLong(cursor.getColumnIndexOrThrow(COL_BUDGET_END_DATE)));
            budget.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT)));
            budget.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY_NAME)));
            
            // Calculate spent amount
            double spent = getTotalExpensesByCategory(budget.getUserId(), budget.getCategoryId(),
                    budget.getStartDate(), budget.getEndDate());
            budget.setSpent(spent);
            budget.setRemaining(budget.getAmount() - spent);
            
            cursor.close();
        }
        return budget;
    }

    public int updateBudget(Budget budget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BUDGET_AMOUNT, budget.getAmount());
        values.put(COL_BUDGET_PERIOD, budget.getPeriod());
        values.put(COL_BUDGET_START_DATE, budget.getStartDate());
        values.put(COL_BUDGET_END_DATE, budget.getEndDate());
        return db.update(TABLE_BUDGETS, values, COL_ID + "=?",
                new String[]{String.valueOf(budget.getId())});
    }

    public int deleteBudget(int budgetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BUDGETS, COL_ID + "=?",
                new String[]{String.valueOf(budgetId)});
    }

    // ==================== CURRENCY OPERATIONS ====================

    public List<Currency> getAllCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CURRENCIES, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Currency currency = new Currency();
                currency.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                currency.setCode(cursor.getString(cursor.getColumnIndexOrThrow(COL_CURRENCY_CODE)));
                currency.setSymbol(cursor.getString(cursor.getColumnIndexOrThrow(COL_CURRENCY_SYMBOL)));
                currency.setExchangeRate(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_CURRENCY_RATE)));
                currencies.add(currency);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return currencies;
    }
}
