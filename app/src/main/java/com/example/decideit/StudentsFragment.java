package com.example.decideit;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ListView list;
    TextView emptyText;

    StudentAdapter adapter;

    public StudentsFragment() {
        // Required empty public constructor
    }

    public static StudentsFragment newInstance(String param1, String param2) {
        StudentsFragment fragment = new StudentsFragment();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_students, container, false);
        list = v.findViewById(R.id.studentList);
        emptyText = v.findViewById(R.id.emptyListText);
        list.setEmptyView(emptyText);

        DBHelper db = new DBHelper(requireContext());
        adapter = new StudentAdapter(requireContext(), db);
        List<StudentModel> students = db.getAllStudents(requireContext());
        Log.i("LISTA STUDENATA","Broj studenata u listi "+ students.size());
        for(StudentModel s : students){
            adapter.addElement(s);
        }

        list.setAdapter(adapter);

        return v;
    }
}