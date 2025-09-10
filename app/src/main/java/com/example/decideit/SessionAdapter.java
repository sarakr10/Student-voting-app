package com.example.decideit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SessionAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SessionModel> sessions;
    public SessionAdapter(Context context, ArrayList<SessionModel> sessionsFromDB){
        this.context = context;
        this.sessions = sessionsFromDB;
    }

    public void setSessions(ArrayList<SessionModel> newSessions){
        this.sessions = newSessions;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return sessions.size();
    }

    public void clear() {
        sessions.clear();
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        if(position>=0){
            return sessions.get(position);
        }else {
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
            convertView = inflater.inflate(R.layout.sessions_row, null);
        }

        SessionModel session = (SessionModel) getItem(position);
        TextView sessionName = convertView.findViewById(R.id.listSession);
        TextView date = convertView.findViewById(R.id.listDate);
        TextView status = convertView.findViewById(R.id.listStatus);

        sessionName.setText(session.getName());
        date.setText(session.getDateInString());
        status.setText(session.getStatus());

    return convertView;
    }
}
