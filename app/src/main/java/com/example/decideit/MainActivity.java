package com.example.decideit;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button bR;
    Button bL;
    EditText ETusername;
    EditText ETpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bR = findViewById(R.id.register);
        bL = findViewById(R.id.login);
        ETusername = findViewById(R.id.enterUsername);
        ETpassword = findViewById(R.id.enterPassword);

        bL.setOnClickListener(this);
        bR.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.login){
            Log.i("LOGIN", "Pritisnuto login dugme");
            String username = ETusername.getText().toString().trim();
            String password = ETpassword.getText().toString().trim();
            if(username.equals("student") && password.equals("student")){
                Intent iSA = new Intent(MainActivity.this, StudentViewActivity.class);
                iSA.putExtra("username", username);
                startActivity(iSA);
            }
            else if(username.equals("admin") && password.equals("admin")){
                Intent iAA = new Intent(MainActivity.this, AdminActivity.class);
                startActivity(iAA);
            }
            else{
                Toast.makeText(MainActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
            }
        }
        else if(v.getId() == R.id.register){
            Log.i("REGISTER", "Prtitisnuto REGISTER dugme");
            Intent iR = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(iR);
        }
    }
}