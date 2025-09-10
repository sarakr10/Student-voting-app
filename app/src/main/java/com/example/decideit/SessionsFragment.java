package com.example.decideit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
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

    int sessionCounter=0;
    String sessionName;
    String sessionStatus;


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
       //KADA SE PROSLEDI 0 GETSESSIONS DOBIJA SE LISTA SVIH SESIJA U BAZI
       ArrayList<SessionModel> sessionsFromDB = db.getSessions(0);

       adapter = new SessionAdapter(requireContext(), sessionsFromDB);

       list.setAdapter(adapter);

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
                i.putExtra("sessionName", clickedSession.getName());
                i.putExtra("sessionDate", db.getDateInString(clickedSession.getDate()));
                startActivity(i);
            }
        });
        return v;
    }

    public void onClickSubmit(View v){
        if(v.getId()==R.id.submitButton){

            sessionCounter++;
            sessionName = "Session " + sessionCounter;

            if(selectedDate < todayDate){
                sessionStatus = "PAST";
            }else{
                sessionStatus = "UPCOMING";
            }

            //glasovi za datu sesiju se inicijalizuju na 0
            VotesModel votes = new VotesModel(0, 0, 0, db.getDateInString(selectedDate), sessionName);
            db.insertVote(votes);
            
            SessionModel session = new SessionModel(sessionName,selectedDate,sessionStatus);

            db.insertSession(session);

            ArrayList<SessionModel> sessionsFromDB = db.getSessions(0);

            adapter.clear();
            adapter.setSessions(sessionsFromDB);
            adapter.notifyDataSetChanged();

        }
    }


}