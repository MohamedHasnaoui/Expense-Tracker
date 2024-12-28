package com.hasnaoui.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import databaseHandlers.DbConnect;

public class activity_auth extends AppCompatActivity {

    EditText email ,password;
    Button login;
    FloatingActionButton register,plusBtn;
    private LinearLayout addUserLayout;
    private boolean isMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        email=findViewById(R.id.emailInput);
        password =findViewById(R.id.passwordInput);

        login=findViewById(R.id.login);
        register=findViewById(R.id.addUserBtn);
        plusBtn=findViewById(R.id.plusBtn);
        addUserLayout=findViewById(R.id.addLayout);

        DbConnect dbHelper=new DbConnect(this);

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMenuOpen) {
                    addUserLayout.setVisibility(View.GONE);
                } else {
                    addUserLayout.setVisibility(View.VISIBLE);
                }
                isMenuOpen = !isMenuOpen;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailInput=email.getText().toString();
                String passwordInput=password.getText().toString();
                if(emailInput.isEmpty() || passwordInput.isEmpty()){
                    Toast.makeText(activity_auth.this, "Remplir tous les champs! ", Toast.LENGTH_SHORT).show();

                }else{
                    String[] nomPrenom = dbHelper.authenticate(emailInput,passwordInput);
                    int userId = dbHelper.getUserIdByEmail(emailInput);

                    if(nomPrenom!=null && userId != -1){
                        Toast.makeText(activity_auth.this, "Bienvenue "+nomPrenom[0]+" "+nomPrenom[1]+" "+userId, Toast.LENGTH_SHORT).show();
                        Intent intent =new Intent(activity_auth.this,Home.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);

                    }else{
                        Toast.makeText(activity_auth.this, "Email ou Password incorrect! ", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivity =new Intent(activity_auth.this,activity_register.class);
                startActivity(registerActivity);
            }
        });
    }
}