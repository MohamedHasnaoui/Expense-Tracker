package databaseHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

import models.Expense;
import models.UserDB;

public class DbConnect extends SQLiteOpenHelper {

    private static  String dbname="Users";
    private static  int dbVersion=1;



    public DbConnect(@Nullable Context context) {
        super(context, dbname, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String queryUsers = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nom TEXT NOT NULL, " +
                "prenom TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL)";
        sqLiteDatabase.execSQL(queryUsers);

        String queryExpense = "CREATE TABLE expenses (" +
                "expenseId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "date TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "userId INTEGER NOT NULL, " +
                "FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE)";
        sqLiteDatabase.execSQL(queryExpense);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldV, int newV) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS expenses");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS users");
        onCreate(sqLiteDatabase);
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void addUser(UserDB user){


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("nom",user.getNom());
        values.put("prenom",user.getPrenom());
        values.put("email",user.getEmail());
        values.put("password",user.getPassword());
        db.insert("users",null,values);


    }
    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE email = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            cursor.close();
            return userId;
        } else {
            cursor.close();
            return -1;
        }
    }


    public String[] authenticate(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        String[] args=new String[]{email,password};
        Cursor cursor = db.rawQuery(query, args);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int columnNom = cursor.getColumnIndex("nom");
            int columnPrenom = cursor.getColumnIndex("prenom");
            if (columnNom != -1 && columnPrenom!=-1) {
                String nom = cursor.getString(columnNom);
                String prenom = cursor.getString(columnPrenom);
                cursor.close();
                return new String[]{nom, prenom};
            } else {
                return null;
            }

        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public void deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("users", "email = ?", new String[]{email});


    }


    //--------------------CRUD POUR EXPENSE-----------------//
    public void addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", expense.getTitle());
        values.put("amount", expense.getAmount());
        values.put("date", expense.getDate().toString());
        values.put("category", expense.getCategory().name());
        values.put("userId", expense.getUserId());
        db.insert("expenses", null, values);
    }

    public List<Expense> getExpensesByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Expense> expenses = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM expenses WHERE userId = ?", new String[]{String.valueOf(userId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int expenseIdIndex = cursor.getColumnIndex("expenseId");
                    int titleIndex = cursor.getColumnIndex("title");
                    int amountIndex = cursor.getColumnIndex("amount");
                    int dateIndex = cursor.getColumnIndex("date");
                    int categoryIndex = cursor.getColumnIndex("category");

                    if (expenseIdIndex != -1 && titleIndex != -1 && amountIndex != -1 && dateIndex != -1 && categoryIndex != -1) {
                        int expenseId = cursor.getInt(expenseIdIndex);
                        String title = cursor.getString(titleIndex);
                        double amount = cursor.getDouble(amountIndex);
                        String dateStr = cursor.getString(dateIndex);
                        Expense.Category category = Expense.Category.valueOf(cursor.getString(categoryIndex));

                        Date date = Date.valueOf(dateStr);

                        Expense expense = new Expense(expenseId, title, amount, date, category, userId);
                        expenses.add(expense);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return expenses;
    }
    public void updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", expense.getTitle());
        values.put("amount", expense.getAmount());
        values.put("date", expense.getDate().toString());
        values.put("category", expense.getCategory().name());
        db.update("expenses", values, "expenseId = ?", new String[]{String.valueOf(expense.getExpenseId())});
    }
    public void deleteExpense(int expenseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("expenses", "expenseId = ?", new String[]{String.valueOf(expenseId)});
    }

}
