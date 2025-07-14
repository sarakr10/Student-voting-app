package com.example.decideit;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener{

    Button buttonStudents;
    Button buttonSessions;

    StudentsFragment stFragment = new StudentsFragment();
    SessionsFragment seFragment = new SessionsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonStudents = findViewById(R.id.studentsB);
        buttonSessions = findViewById(R.id.sessionsB);

        buttonStudents.setOnClickListener(this);
        buttonSessions.setOnClickListener(this);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentLayoutAdmin, stFragment).commit();
    }

    @Override
    public void onClick(View v) {
        int green = ContextCompat.getColor(this, R.color.green);
        int charcoal = ContextCompat.getColor(this, R.color.charcoal);
        if(v.getId() == R.id.sessionsB){
            Log.i("STUDENTS","pritisnuto je STUDENTS dugme");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayoutAdmin, seFragment).addToBackStack(null).commit();
            buttonSessions.setBackgroundColor(green);
            buttonStudents.setBackgroundColor(charcoal);
        } else if (v.getId() == R.id.studentsB) {
            Log.i("SESSIONS", "pritisnuto je SESSIONS dugme");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayoutAdmin, stFragment).addToBackStack(null).commit();
            buttonSessions.setBackgroundColor(charcoal);
            buttonStudents.setBackgroundColor(green);

        }
    }
}