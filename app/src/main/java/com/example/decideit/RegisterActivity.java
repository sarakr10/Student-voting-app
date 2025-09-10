package com.example.decideit;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText ETusername = findViewById(R.id.enterUsername);
        EditText ETname = findViewById(R.id.enterName);
        EditText ETindex = findViewById(R.id.enterIndex);
        EditText ETpassword = findViewById(R.id.enterPassword);

        Button register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ETusername.getText().toString().trim();
                String password = ETpassword.getText().toString().trim();
                String nameSurname = ETname.getText().toString().trim();
                String index = ETindex.getText().toString().trim();

                String name, surname;
                String[] parts = nameSurname.split(" ");
                name = parts[0];
                surname = parts[1];

                boolean role;
                if(index.equals("admin")){
                    role = true;
                }else{
                    role=false;
                }

                if(username.isEmpty() || nameSurname.isEmpty() || password.isEmpty() || index.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please fill al the fields", Toast.LENGTH_SHORT).show();
                }
                else if(v.getId()==R.id.register){
                    Log.i("REGISTER", "pritisnuto REGISTER dugme");
                    UserModel user = new UserModel(name, surname, username, password, role, index);
                    DBHelper dbHelper = new DBHelper(RegisterActivity.this);
                    if(dbHelper.findUsername(username)){
                        Toast.makeText(RegisterActivity.this, "Username is already taken", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        dbHelper.insertUser(user);
                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(i);
                    }

                }
            }
        });
    }
}