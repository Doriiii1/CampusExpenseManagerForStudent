package com.example.campusexpensemanagerforstudent.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.campusexpensemanagerforstudent.models.Budget;
import com.example.campusexpensemanagerforstudent.models.Category;
import com.example.campusexpensemanagerforstudent.models.Expense;
import com.example.campusexpensemanagerforstudent.models.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "campus_expense.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_EXPENSES = "expenses";
    private static final String TABLE_BUDGETS = "budgets";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password_hash TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "address TEXT, " +
                "phone TEXT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createUsersTable);

        // Create Categories table
        String createCategoriesTable = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT UNIQUE NOT NULL, " +
                "icon TEXT)";
        db.execSQL(createCategoriesTable);

        // Create Expenses table
        String createExpensesTable = "CREATE TABLE " + TABLE_EXPENSES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "category_id INTEGER NOT NULL, " +
                "amount REAL NOT NULL, " +
                "description TEXT, " +
                "date TEXT NOT NULL, " +
                "time TEXT NOT NULL, " +
                "receipt_photo TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(id), " +
                "FOREIGN KEY(category_id) REFERENCES " + TABLE_CATEGORIES + "(id))";
        db.execSQL(createExpensesTable);

        // Create Budgets table
        String createBudgetsTable = "CREATE TABLE " + TABLE_BUDGETS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "category_id INTEGER NOT NULL, " +
                "amount REAL NOT NULL, " +
                "period TEXT NOT NULL, " +
                "start_date TEXT NOT NULL, " +
                "end_date TEXT NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(id), " +
                "FOREIGN KEY(category_id) REFERENCES " + TABLE_CATEGORIES + "(id))";
        db.execSQL(createBudgetsTable);

        // Pre-populate categories
        populateCategories(db);
    }

    private void populateCategories(SQLiteDatabase db) {
        String[] categories = {
                "Food & Dining", "Transportation", "Education", "Entertainment",
                "Health", "Shopping", "Utilities", "Accommodation", "Books & Supplies", "Other"
        };

        for (String category : categories) {
            ContentValues values = new ContentValues();
            values.put("name", category);
            values.put("icon", "ic_category");
            db.insert(TABLE_CATEGORIES, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // USER CRUD OPERATIONS

    public long insertUser(String email, String passwordHash, String name, String address, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password_hash", passwordHash);
        values.put("name", name);
        values.put("address", address);
        values.put("phone", phone);
        return db.insert(TABLE_USERS, null, values);
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, "email = ?", new String[]{email},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.setPasswordHash(cursor.getString(cursor.getColumnIndexOrThrow("password_hash")));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
            cursor.close();
        }
        return user;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, "id = ?", new String[]{String.valueOf(id)},
                null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            user.setPasswordHash(cursor.getString(cursor.getColumnIndexOrThrow("password_hash")));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
            cursor.close();
        }
        return user;
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{"id"}, "email = ?",
                new String[]{email}, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        return exists;
    }

    public int updateUser(int userId, String name, String address, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("address", address);
        values.put("phone", phone);
        return db.update(TABLE_USERS, values, "id = ?", new String[]{String.valueOf(userId)});
    }

    public int updatePassword(int userId, String newPasswordHash) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password_hash", newPasswordHash);
        return db.update(TABLE_USERS, values, "id = ?", new String[]{String.valueOf(userId)});
    }

    // CATEGORY OPERATIONS

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, "name ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                category.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                category.setIcon(cursor.getString(cursor.getColumnIndexOrThrow("icon")));
                categories.add(category);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return categories;
    }

    public Category getCategoryById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null, "id = ?", new String[]{String.valueOf(id)},
                null, null, null);

        Category category = null;
        if (cursor != null && cursor.moveToFirst()) {
            category = new Category();
            category.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            category.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            category.setIcon(cursor.getString(cursor.getColumnIndexOrThrow("icon")));
            cursor.close();
        }
        return category;
    }

    // EXPENSE CRUD OPERATIONS

    public long insertExpense(int userId, int categoryId, double amount, String description,
                              String date, String time, String receiptPhoto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("category_id", categoryId);
        values.put("amount", amount);
        values.put("description", description);
        values.put("date", date);
        values.put("time", time);
        values.put("receipt_photo", receiptPhoto);
        return db.insert(TABLE_EXPENSES, null, values);
    }

    public List<Expense> getExpensesByUser(int userId) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT e.*, c.name as category_name FROM " + TABLE_EXPENSES + " e " +
                "INNER JOIN " + TABLE_CATEGORIES + " c ON e.category_id = c.id " +
                "WHERE e.user_id = ? ORDER BY e.date DESC, e.time DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                expense.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                expense.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                expense.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                expense.setTime(cursor.getString(cursor.getColumnIndexOrThrow("time")));
                expense.setReceiptPhoto(cursor.getString(cursor.getColumnIndexOrThrow("receipt_photo")));
                expense.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow("category_name")));
                expenses.add(expense);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return expenses;
    }

    public Expense getExpenseById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.*, c.name as category_name FROM " + TABLE_EXPENSES + " e " +
                "INNER JOIN " + TABLE_CATEGORIES + " c ON e.category_id = c.id " +
                "WHERE e.id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        Expense expense = null;
        if (cursor != null && cursor.moveToFirst()) {
            expense = new Expense();
            expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            expense.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
            expense.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
            expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
            expense.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
            expense.setTime(cursor.getString(cursor.getColumnIndexOrThrow("time")));
            expense.setReceiptPhoto(cursor.getString(cursor.getColumnIndexOrThrow("receipt_photo")));
            expense.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow("category_name")));
            cursor.close();
        }
        return expense;
    }

    public int updateExpense(int expenseId, int categoryId, double amount, String description,
                             String date, String time, String receiptPhoto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_id", categoryId);
        values.put("amount", amount);
        values.put("description", description);
        values.put("date", date);
        values.put("time", time);
        values.put("receipt_photo", receiptPhoto);
        return db.update(TABLE_EXPENSES, values, "id = ?", new String[]{String.valueOf(expenseId)});
    }

    public int deleteExpense(int expenseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EXPENSES, "id = ?", new String[]{String.valueOf(expenseId)});
    }

    public double getTotalExpensesByUserAndMonth(int userId, String yearMonth) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(amount) as total FROM " + TABLE_EXPENSES +
                " WHERE user_id = ? AND date LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), yearMonth + "%"});

        double total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
            cursor.close();
        }
        return total;
    }

    // BUDGET CRUD OPERATIONS

    public long insertBudget(int userId, int categoryId, double amount, String period,
                             String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("category_id", categoryId);
        values.put("amount", amount);
        values.put("period", period);
        values.put("start_date", startDate);
        values.put("end_date", endDate);
        return db.insert(TABLE_BUDGETS, null, values);
    }

    public List<Budget> getBudgetsByUser(int userId) {
        List<Budget> budgets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT b.*, c.name as category_name FROM " + TABLE_BUDGETS + " b " +
                "INNER JOIN " + TABLE_CATEGORIES + " c ON b.category_id = c.id " +
                "WHERE b.user_id = ? ORDER BY b.start_date DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Budget budget = new Budget();
                budget.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                budget.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                budget.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                budget.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                budget.setPeriod(cursor.getString(cursor.getColumnIndexOrThrow("period")));
                budget.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow("start_date")));
                budget.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow("end_date")));
                budget.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow("category_name")));

                // Calculate spent amount
                double spent = getSpentInBudgetPeriod(userId, budget.getCategoryId(),
                        budget.getStartDate(), budget.getEndDate());
                budget.setSpent(spent);

                budgets.add(budget);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return budgets;
    }

    private double getSpentInBudgetPeriod(int userId, int categoryId, String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(amount) as total FROM " + TABLE_EXPENSES +
                " WHERE user_id = ? AND category_id = ? AND date BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId),
                String.valueOf(categoryId), startDate, endDate});

        double total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
            cursor.close();
        }
        return total;
    }

    public int deleteBudget(int budgetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BUDGETS, "id = ?", new String[]{String.valueOf(budgetId)});
    }

    // REPORTING QUERIES

    public List<Expense> getExpensesByDateRange(int userId, String startDate, String endDate) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT e.*, c.name as category_name FROM " + TABLE_EXPENSES + " e " +
                "INNER JOIN " + TABLE_CATEGORIES + " c ON e.category_id = c.id " +
                "WHERE e.user_id = ? AND e.date BETWEEN ? AND ? " +
                "ORDER BY e.date DESC, e.time DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), startDate, endDate});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                expense.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                expense.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("amount")));
                expense.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                expense.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                expense.setTime(cursor.getString(cursor.getColumnIndexOrThrow("time")));
                expense.setReceiptPhoto(cursor.getString(cursor.getColumnIndexOrThrow("receipt_photo")));
                expense.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow("category_name")));
                expenses.add(expense);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return expenses;
    }
}
