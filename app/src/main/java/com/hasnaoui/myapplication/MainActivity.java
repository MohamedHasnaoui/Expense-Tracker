package com.hasnaoui.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import databaseHandlers.DbConnect;
import models.Expense;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextAmount;
    private TextView textViewCharCount, textViewDate;
    private Spinner spinnerCategory;
    private Button buttonCancel, buttonSave;
    int userId ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DbConnect db = new DbConnect(this);
        Bundle extras = getIntent().getExtras();
        userId = extras.getInt("userId");

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextAmount = findViewById(R.id.editTextAmount);
        textViewCharCount = findViewById(R.id.textViewCharCount);
        textViewDate = findViewById(R.id.textViewDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSave = findViewById(R.id.buttonSave);

        textViewDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_calendar_month_24, 0);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setSelection(0);

        editTextTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentLength = s.toString().trim().length();
                textViewCharCount.setText(currentLength + "/50");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editTextTitle.setFilters(new android.text.InputFilter[] {
                new android.text.InputFilter.LengthFilter(50)
        });

        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this,
                        new DatePickerDialog.OnDateSetListener(){
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar selectedCalendar = Calendar.getInstance();
                                selectedCalendar.set(year, month, dayOfMonth);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                String selectedDate = sdf.format(selectedCalendar.getTime());
                                textViewDate.setText(selectedDate);
                            }
                        },
                        currentYear, currentMonth, currentDay
                );
                datePickerDialog.show();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,Home.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString().trim();
                String amountStr = editTextAmount.getText().toString().trim();
                String dateStr = textViewDate.getText().toString().trim();
                String categoryStr = spinnerCategory.getSelectedItem().toString().trim();

                if (title.isEmpty() || amountStr.isEmpty() || dateStr.equals("No date selected") || categoryStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Montant invalide.", Toast.LENGTH_SHORT).show();
                    return;
                }

                java.util.Date utilDate = null;
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    utilDate = dateFormat.parse(dateStr);
                } catch (ParseException e) {
                    Toast.makeText(MainActivity.this, "Format de date incorrect.", Toast.LENGTH_SHORT).show();
                    return;
                }
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

                Expense.Category category;
                try {
                    category = Expense.Category.valueOf(categoryStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    Toast.makeText(MainActivity.this, "Catégorie invalide.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Expense expense = new Expense(title, amount, sqlDate, category, userId);

                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        db.addExpense(expense);
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Expense saved successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(MainActivity.this,Home.class);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Error saving expense: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });
    }
}