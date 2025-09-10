package com.example.decideit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class DecideActivity extends AppCompatActivity implements View.OnClickListener {

    Button by;
    Button bn;
    Button ba;
    String name;
    String status;
    long date;
    long endOfVotingTime;
    String timeLeftToVote;

    TextView sessionNameTV;
    TextView endOfVotingTimeTV;

    Button selectedButton = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_decide);
        SessionModel session= (SessionModel) getIntent().getSerializableExtra("session");
        if(session!=null){
            name = session.getName();
            date = session.getDate();
            status = session.getStatus();
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        DBHelper dbHelper = new DBHelper(this);

        by = findViewById(R.id.yes);
        bn = findViewById(R.id.no);
        ba = findViewById(R.id.abstrain);
        sessionNameTV = findViewById(R.id.sessionName);
        sessionNameTV.setText(name);
        endOfVotingTimeTV = findViewById(R.id.endOfVotingTime);

        by.setOnClickListener(this);
        bn.setOnClickListener(this);
        ba.setOnClickListener(this);
        endOfVotingTime = dbHelper.getEndOfVotingTime(name, dbHelper.getDateInString(date));
        long currentTime = System.currentTimeMillis();
        Log.i("TIME-------------", "Current time: " + currentTime +" ----- End of voting time: " + endOfVotingTime);
        long remainingTime = endOfVotingTime - currentTime;
        if(remainingTime>0){
            long seconds = remainingTime / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            hours = hours % 24;
            minutes = minutes % 60;
            seconds = seconds % 60;

            String stringRemainingTime = String.format(Locale.getDefault(),  "%d days, %d hours, %d minutes", days, hours, minutes);
            endOfVotingTimeTV.setText(stringRemainingTime);
        }else{
            endOfVotingTimeTV.setText("Voting has ended");
        }

    }

    @Override
    public void onClick(View v) {
        int green = ContextCompat.getColor(this, R.color.green);
        int charcoal = ContextCompat.getColor(this, R.color.charcoal);
        //resetuje prethodno dugme
        if(selectedButton!=null){
            selectedButton.setBackgroundColor(charcoal);
        }
        selectedButton = (Button) v;
        selectedButton.setBackgroundColor(green);

        DBHelper db = new DBHelper(this);

        if(v.getId()==R.id.yes){
            db.updateVotes(name, date, "YES");
            Toast.makeText(this, "Your YES vote has been recorded", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.no) {
            db.updateVotes(name, date, "NO");
            Toast.makeText(this, "Your NO vote has been recorded", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.abstrain) {
            db.updateVotes(name, date, "ABSTAIN");
            Toast.makeText(this, "Your ABSTAIN vote has been recorded", Toast.LENGTH_SHORT).show();
        }
        }
    }
