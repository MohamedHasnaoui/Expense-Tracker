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
import models.UserDB;

public class activity_register extends AppCompatActivity {
    EditText email,password,nom,prenom;
    Button addUser,login;
    FloatingActionButton register,plusBtn;
    private LinearLayout addUserLayout;
    private boolean isMenuOpen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        email=findViewById(R.id.emaiRegister);
        password=findViewById(R.id.passwordRegister);
        nom=findViewById(R.id.nomRegister);
        prenom=findViewById(R.id.prenomRegister);
        addUser=findViewById(R.id.addUser);
        login=findViewById(R.id.loginPage);
        register=findViewById(R.id.addUserBtn);
        plusBtn=findViewById(R.id.plusBtn);
        addUserLayout=findViewById(R.id.addLayout);

        DbConnect dbHelper=new DbConnect(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginPage=new Intent(activity_register.this,activity_auth.class);
                startActivity(loginPage);
            }
        });
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
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity_register.this, "Vous etes dans registerActivity!", Toast.LENGTH_SHORT).show();
             }
        });
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomInput =nom.getText().toString();
                String prenomInput =prenom.getText().toString();
                String emailInput =email.getText().toString();
                String passwordInput =password.getText().toString();
                if(nomInput.isEmpty()||prenomInput.isEmpty()||emailInput.isEmpty()||passwordInput.isEmpty()){
                    Toast.makeText(activity_register.this, "Remplir tous les champs!", Toast.LENGTH_SHORT).show();

                }else{
                    UserDB newUser = new UserDB( nomInput, prenomInput, emailInput, passwordInput);
                    if(dbHelper.emailExists(newUser.getEmail())){
                        Toast.makeText(activity_register.this, "Email déjà existant!", Toast.LENGTH_SHORT).show();

                    }else{
                        dbHelper.addUser(newUser);
                        Toast.makeText(activity_register.this, "Utilisateur créé avec succès!", Toast.LENGTH_SHORT).show();
                        Intent loginPage=new Intent(activity_register.this,activity_auth.class);
                        startActivity(loginPage);
                    }
                }
            }
        });
    }
}