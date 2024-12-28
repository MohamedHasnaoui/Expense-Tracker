package com.hasnaoui.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import databaseHandlers.DbConnect;
import models.Expense;
import models.ExpenseAdapter;

public class Home extends AppCompatActivity implements ExpenseAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private List<Expense> expenseList;
    int userId;
    private DbConnect db = new DbConnect(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle extras = getIntent().getExtras();
        userId = extras.getInt("userId");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Executors.newSingleThreadExecutor().execute(() -> {
            expenseList = db.getExpensesByUserId(userId);
            runOnUiThread(() -> {
                adapter = new ExpenseAdapter(this, expenseList, this);
                recyclerView.setAdapter(adapter);
                displayChart();
            });
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent =new Intent(this,MainActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    private void displayChart() {
        Map<Expense.Category, Double> categoryTotals = new HashMap<>();
        for (Expense expense : expenseList) {
            categoryTotals.put(expense.getCategory(), categoryTotals.getOrDefault(expense.getCategory(), 0.0) + expense.getAmount());
        }

        double maxTotal = categoryTotals.values().stream().max(Double::compare).orElse(1.0);

        updateBarHeight(R.id.bar_leisure, categoryTotals.getOrDefault(Expense.Category.LEISURE, 0.0), maxTotal);
        updateBarHeight(R.id.bar_food, categoryTotals.getOrDefault(Expense.Category.FOOD, 0.0), maxTotal);
        updateBarHeight(R.id.bar_work, categoryTotals.getOrDefault(Expense.Category.WORK, 0.0), maxTotal);
        updateBarHeight(R.id.bar_other, categoryTotals.getOrDefault(Expense.Category.OTHER, 0.0), maxTotal);
    }

    private void updateBarHeight(int barId, double total, double maxTotal) {
        View bar = findViewById(barId);
        if (bar != null) {
            int maxHeight = 200;
            int height = (int) ((total / maxTotal) * maxHeight);
            bar.getLayoutParams().height = height;
            bar.requestLayout();
        }
    }


    // Handle Delete Button Click
    @Override
    public void onDeleteClick(Expense expense) {
        expenseList.remove(expense);
        db.deleteExpense(expense.getExpenseId());
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Deleted: " + expense.getTitle(), Toast.LENGTH_SHORT).show();

        displayChart();
    }
}