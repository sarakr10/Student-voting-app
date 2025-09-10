package com.example.decideit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResultsActivity extends AppCompatActivity {

    VotesModel votes;
    TextView yesTV;
    TextView noTV;
    TextView abstainTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_results);
        Intent i = getIntent();
        String sessionName = i.getStringExtra("sessionName");
        String sessionDate = i.getStringExtra("sessionDate");

        yesTV = findViewById(R.id.yesTextView);
        noTV = findViewById(R.id.noTextView);
        abstainTV = findViewById(R.id.abstainTextView);

        DBHelper db = new DBHelper(this);
        votes = db.getVotes(sessionName, sessionDate);

        if(votes!=null){
            yesTV.setText(String.valueOf(votes.getYesVotes()));
            noTV.setText(String.valueOf(votes.getNoVotes()));
            abstainTV.setText(String.valueOf(votes.getAbstrainVotes()));
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.resultsActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}