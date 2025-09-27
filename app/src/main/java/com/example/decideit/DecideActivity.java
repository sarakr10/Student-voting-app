package com.example.decideit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.Instant;
import java.util.Locale;

public class DecideActivity extends AppCompatActivity implements View.OnClickListener {

    Button by;
    Button bn;
    Button ba;
    DBHelper db;
    HttpHelper httpHelper;
    String name;
    String status;
    long date;
    long endOfVotingTime;
    String sessionID;
    String description;
    Integer yes, no, abstain;

    TextView sessionNameTV;
    TextView endOfVotingTimeTV;
    TextView sessionDescriptionTV;
    TextView sessionDateTV;

    Button selectedButton = null;
    String SERVER_URL;
    VotesModel votes;
    private static final String POST_URL = "http://10.0.2.2:8080/api/results/vote";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_decide);

        db = new DBHelper(this);
        httpHelper = new HttpHelper();

        SessionModel session= (SessionModel) getIntent().getSerializableExtra("session");
        if(session!=null){
            sessionID = session.getId();
            description = session.getDescription();
            name = session.getName();
            date = session.getDate();
            status = session.getStatus();
            SERVER_URL = "http://10.0.2.2:8080/api/votes?sessionID="+sessionID;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fetchVotesFromServer();

        by = findViewById(R.id.yes);
        bn = findViewById(R.id.no);
        ba = findViewById(R.id.abstrain);
        sessionDateTV = findViewById(R.id.sessionDate);
        sessionDescriptionTV = findViewById(R.id.sessionDescription);
        sessionNameTV = findViewById(R.id.sessionName);
        sessionNameTV.setText(name);
        endOfVotingTimeTV = findViewById(R.id.endOfVotingTime);

        String dateString = db.getDateInString(date);
        sessionDateTV.setText(dateString);
        sessionDescriptionTV.setText(description);

        by.setOnClickListener(this);
        bn.setOnClickListener(this);
        ba.setOnClickListener(this);

        endOfVotingTime = db.getEndOfVotingTime(sessionID, db.getDateInString(date));
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

        if(selectedButton!=null){
            selectedButton.setBackgroundColor(charcoal);
        }
        selectedButton = (Button) v;
        selectedButton.setBackgroundColor(green);

        if (db == null) {
            Log.e("DECIDE_ACTIVITY", "DB helper is null!");
            return;
        }

        votes = db.getVotes(sessionID);

        if (votes == null) {
            Log.e("DECIDE_ACTIVITY", "Votes object is null for sessionID: " + sessionID);
            Toast.makeText(this, "Error: Could not load voting data", Toast.LENGTH_SHORT).show();
            return;
        }

        String voteType = "";
        if(v.getId()==R.id.yes){
            voteType = "yes";
            Toast.makeText(this, "Your YES vote has been recorded", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.no) {
            voteType = "no";
            Toast.makeText(this, "Your NO vote has been recorded", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.abstrain) {
            voteType = "abstain";
            Toast.makeText(this, "Your ABSTAIN vote has been recorded", Toast.LENGTH_SHORT).show();
        }

        JSONObject json = new JSONObject();
        try {
            json.put("sessionId", sessionID);
            json.put("vote", voteType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            try {
                boolean success = httpHelper.postJSONObjectFromURL(POST_URL, json);
                Log.i("SUBMIT", "POST request result: " + success);

                if (success) {
                    Thread.sleep(500);
                    //fetchVotesFromServer();
                } else {
                    Log.e("SUBMIT", "Failed to post votes to server");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SUBMIT", "Error posting votes: " + e.getMessage());
            }
        }).start();
       fetchVotesFromServer();
    }

    private void fetchVotesFromServer() {
        Log.i("FETCH_VOTES", "Starting fetchVotesFromServer()");
        new Thread(() -> {
            try {
                if (db == null) {
                    Log.e("FETCH_VOTES", "DB helper is null in fetchVotesFromServer");
                    return;
                }

                JSONArray jsonArray = httpHelper.getJSONArrayFromURL(SERVER_URL);
                if (jsonArray != null) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Log.i("FETCH_VOTES", "Parsing session object: " + obj.toString());
                        String sessionID = obj.getString("sessionId");
                        String sessionName = obj.getString("sessionName");
                        long dateMillis = 0;
                        String dateStr = obj.optString("sessionDate", null);
                        if (dateStr != null) {
                            try {
                                dateMillis = Instant.parse(dateStr).toEpochMilli();
                                Log.i("FETCH_VOTES", "Parsed date: " + dateStr + " -> " + dateMillis);
                            } catch (Exception e) {
                                Log.e("FETCH_VOTES", "Failed to parse ISO date: " + dateStr, e);
                            }
                        } else {
                            // fallback ako je broj u milisekundama
                            dateMillis = obj.optLong("sessionDate", System.currentTimeMillis());
                            Log.i("FETCH_VOTES", "Using timestamp: " + dateMillis);
                        }

                        Integer numberYes = obj.getInt("yes");
                        Integer numberNo = obj.getInt("no");
                        Integer numberAbstain = obj.getInt("abstain");
                        String updatedAt = obj.getString("updatedAt");

                        db.updateVotesFromServer(sessionID, numberYes, numberNo, numberAbstain, updatedAt);

                        Log.i("FETCH_VOTES", "Inserted votes into DB from SESSION: " +sessionName);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("FETCH_VOTES", "Error in fetchVotesFromServer: " + e.getMessage());
            }
        }).start();
    }
}