package com.example.decideit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONException;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SessionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SessionsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    DBHelper db;
    ListView list;
    TextView emptyText;
    SessionAdapter adapter;
    Button submit;
    CalendarView calendar;

    long selectedDate;
    long todayDate;
    String sessionName;
    String sessionStatus;
    private  HttpHelper httpHelper;
    private static final String SERVER_URL = "http://10.0.2.2:8080/api/sessions";
    private static final String POST_URL = "http://10.0.2.2:8080/api/session";
    boolean a;

    public SessionsFragment() {
        // Required empty public constructor
    }

    public static SessionsFragment newInstance(String param1, String param2) {
        SessionsFragment fragment = new SessionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_sessions, container, false);

        list = v.findViewById(R.id.sessionList);
        emptyText = v.findViewById(R.id.emptyListText);
        list.setEmptyView(emptyText);

        submit = v.findViewById(R.id.submitButton);
        calendar = v.findViewById(R.id.sessionCalendar);

        submit.setOnClickListener(this::onClickSubmit);

        db = new DBHelper(requireContext());
        db.clearAllSessions();
        httpHelper = new HttpHelper();
        adapter = new SessionAdapter(requireContext(), new ArrayList<>());
        adapter.clear();
        list.setAdapter(adapter);


        fetchSessionsFromServer();

        //datum selektovan kada se fragment prvi put ucita tj danasnji datum
        todayDate = calendar.getDate();
        selectedDate = calendar.getDate();

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Activity a = getActivity();
                if(a!=null){
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month, dayOfMonth, 0, 0, 0);
                    selectedDate = cal.getTimeInMillis();
                }
            }
        });

        //KLIK NA DATUM PRELAZI SE NA RESULTS ACTIVITY
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SessionModel clickedSession = (SessionModel) parent.getItemAtPosition(position);
                Intent i = new Intent(view.getContext(), ResultsActivity.class);
                i.putExtra("sessionID", clickedSession.getId());
                startActivity(i);
            }
        });
        return v;
    }

    private void fetchSessionsFromServer() {
        Log.i("FETCH_SESSIONS", "Starting fetchSessionsFromServer()");
        new Thread(() -> {
            try {
                JSONArray jsonArray = httpHelper.getJSONArrayFromURL(SERVER_URL);
                if (jsonArray != null) {
                    db.clearAllSessions();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Log.i("FETCH_SESSIONS", "Parsing session object: " + obj.toString());
                        String name = obj.getString("sessionName");
                        String dateStr="";
                        long dateMillis = 0;
                        try {
                            dateStr = obj.getString("date");
                            dateMillis = Instant.parse(dateStr).toEpochMilli();
                        } catch (JSONException e) {
                            try {
                                dateMillis = obj.getLong("date"); // timestamp u milisekundama
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        }

                        String description = obj.getString("description");
                        String endOfVotingTimeStr = obj.getString("endOfVotingTime");
                        long endOfVotingTime = java.time.Instant.parse(endOfVotingTimeStr).toEpochMilli();
                        String id = obj.optString("_id", null);

                        dateMillis = Instant.parse(dateStr).toEpochMilli();
                        SessionModel session = new SessionModel(name, dateMillis, description);
                        session.setId(id);

                        db.insertSession(session);

                        Log.i("FETCH_SESSIONS", "Inserted session into DB: " + session.getName());
                    }
                }
                requireActivity().runOnUiThread(() -> {
                    refreshAdapter();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void refreshAdapter() {
        ArrayList<SessionModel> sessionsFromDB = db.getSessions(0);
        adapter.clear();
        adapter.setSessions(sessionsFromDB);
        adapter.notifyDataSetChanged();
        Log.i("REFRESH_ADAPTER", "Adapter refreshed with " + sessionsFromDB.size() + " sessions");
    }
    public void onClickSubmit(View v){
        if(v.getId()==R.id.submitButton){
            adapter.clear();

            LayoutInflater inflater = LayoutInflater.from(requireContext());
            View dialogView = inflater.inflate(R.layout.submit_dialog, null);

            EditText nameInput = dialogView.findViewById(R.id.sessionNameInput);
            EditText descInput = dialogView.findViewById(R.id.sessionDescInput);

            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("New Session")
                    .setView(dialogView)
                    .setPositiveButton("OK", (dialog, which) -> {
                        String sessionName = nameInput.getText().toString().trim();
                        String sessionDesc = descInput.getText().toString().trim();

                        SessionModel session = new SessionModel(sessionName, selectedDate, sessionDesc);
                        // Kreiraj JSON za server
                        JSONObject json = new JSONObject();
                        try {
                            json.put("date", selectedDate);
                            json.put("sessionName", sessionName);
                            json.put("description", sessionDesc);
                            json.put("endOfVotingTime", session.getEndOfVotingTime());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Pošalji na server u posebnom thread-u
                        new Thread(() -> {
                            try {
                                boolean success = httpHelper.postJSONObjectFromURL(POST_URL, json);
                                Log.i("SUBMIT", "POST request result: " + success);

                                if (success) {
                                    // Sačekaj kratko da se server ažurira
                                    Thread.sleep(500);

                                    // Ponovo učitaj sve sesije sa servera
                                    fetchSessionsFromServer();
                                } else {
                                    Log.e("SUBMIT", "Failed to post session to server");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("SUBMIT", "Error posting session: " + e.getMessage());
                            }
                        }).start();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

}