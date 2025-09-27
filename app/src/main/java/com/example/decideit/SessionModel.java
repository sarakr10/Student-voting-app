package com.example.decideit;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SessionModel implements Serializable {
    private String name;
    private long date;
    private String description;      //status/description
    private String id;

    public SessionModel(String name, long date, String description){
        this.name = name;
        this.date = date;
        this.description = description;
    }

    public String getStatus() {
        return description;
    }

    public void setStatus(String description) {
        this.description = description;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getEndOfVotingTime(){
        long endOfVotingTime = this.getDate() + 3* 24 * 60 * 60 * 1000; //3 dana  u ms
        return endOfVotingTime;
    }

    public String getDateInString(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = sdf.format(date);
        return dateString;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
