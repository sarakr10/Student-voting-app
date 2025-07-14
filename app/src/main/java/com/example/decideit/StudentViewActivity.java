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

public class StudentViewActivity extends AppCompatActivity implements View.OnClickListener {

    Button profileButton;
    Button calendarButton;

    ProfileFragment pFragment = new ProfileFragment();
    CalendarFragment cFragment = new CalendarFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String username = getIntent().getStringExtra("username");
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        pFragment.setArguments(bundle);

        profileButton = findViewById(R.id.profileB);
        calendarButton = findViewById(R.id.calendarB);

        profileButton.setOnClickListener(this);
        calendarButton.setOnClickListener(this);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentLayout, pFragment).commit();

    }

    @Override
    public void onClick(View v) {
        int green = ContextCompat.getColor(this, R.color.green);
        int charcoal = ContextCompat.getColor(this, R.color.charcoal);
        if(v.getId() == R.id.calendarB){
            Log.i("CALENDAR","pritisnuto je CALENDAR dugme");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, cFragment).addToBackStack(null).commit();
            calendarButton.setBackgroundColor(green);
            profileButton.setBackgroundColor(charcoal);
        } else if (v.getId() == R.id.profileB) {
            Log.i("PROFILE", "pritisnuto je PROFILE dugme");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, pFragment).addToBackStack(null).commit();
            calendarButton.setBackgroundColor(charcoal);
            profileButton.setBackgroundColor(green);

        }
    }
}