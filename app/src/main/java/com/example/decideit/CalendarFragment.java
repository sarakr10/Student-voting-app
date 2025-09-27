package com.example.decideit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
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

        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        CalendarView calendarV = v.findViewById(R.id.calendar);

        Calendar minDate = Calendar.getInstance();
        minDate.set(2024, Calendar.DECEMBER, 15);
        calendarV.setMinDate(minDate.getTimeInMillis());
        

        calendarV.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                
                Activity a = getActivity();
                if(a!=null){
                    Calendar cal = Calendar.getInstance();
                    cal.set(year,month, dayOfMonth, 0, 0, 0);
                    long selectedDate = cal.getTimeInMillis();

                    DBHelper db = new DBHelper(requireContext());
                    Log.i("ODABRAN DATUM", ":"+selectedDate);
                    ArrayList<SessionModel> sessions = db.getSessions(selectedDate);
                    
                    if(sessions.isEmpty()){
                        Toast.makeText(a, "No sessions for selected date", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent i = new Intent(a, DecideActivity.class);
                        i.putExtra("session", (Serializable) sessions.get(0)); //prosledjuje prvu sesiju koja odgovara tom datumu
                        a.startActivity(i);
                    }
                }
            }
        });

        return v;
    }
}