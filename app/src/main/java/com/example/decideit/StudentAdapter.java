package com.example.decideit;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends BaseAdapter {

    private Context context;
    private DBHelper dbHelper;
    private ArrayList<StudentModel> students;

    public StudentAdapter(Context context, DBHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
        students = new ArrayList<StudentModel>();
    }

    public void addElement(StudentModel element){
        students.add(element);
    }

    public void removeElement(StudentModel element){
        students.remove(element);
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public Object getItem(int position) {
        if(position>=0) {
            return students.get(position);
        }else{
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row, null);
        }

        StudentModel student = (StudentModel) getItem(position);

        ImageView image = convertView.findViewById(R.id.listImage);
        TextView name = convertView.findViewById(R.id.listName);
        TextView index = convertView.findViewById(R.id.listIndex);
        CheckBox check = convertView.findViewById(R.id.listCheck);

        image.setImageDrawable(student.getImage());
        name.setText(student.getName());
        index.setText(student.getIndex());

        check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                new AlertDialog.Builder(context)
                        .setTitle("Delete item")
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("YES", ((dialog, which) -> {
                            String deletedIndex = student.getIndex();
                            dbHelper.removeUser(deletedIndex);
                            students.remove(position);
                            check.setChecked(false);
                            notifyDataSetChanged();
                        }))
                        .setNegativeButton("NO", ((dialog, which) -> {
                            check.setChecked(false);
                            dialog.dismiss();
                        }))
                        .show();
            }
        });

        return convertView;
    }
}
