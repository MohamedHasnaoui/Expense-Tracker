package models;

import android.content.Context;

import com.hasnaoui.myapplication.R;

import java.sql.Date;

public class Expense {
    private int expenseId;
    private String title;
    private double amount;
    private Date date;
    private Category category;
    private int userId;
    public enum Category {
        LEISURE,
        FOOD,
        WORK,
        OTHER;

        public String getDisplayName(Context context) {
            switch (this) {
                case LEISURE:
                    return context.getString(R.string.category_leisure);
                case FOOD:
                    return context.getString(R.string.category_food);
                case WORK:
                    return context.getString(R.string.category_work);
                case OTHER:
                    return context.getString(R.string.category_other);
                default:
                    return "";
            }
        }
    }
    public Expense(String title, double amount, Date date, Category category, int userId) {
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.userId = userId;
    }

    public Expense(int expenseId, String title, double amount, Date date, Category category, int userId) {
        this.expenseId = expenseId;
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.userId = userId;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}