package com.example.decideit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VotesModel {
    private int yesVotes;
    private int noVotes;
    private int abstrainVotes;
    private String sessionName;
    private String sessionDate;
    private String sessionID;
    private String updated;

    //
    public VotesModel(int noVotes, int yesVotes, int abstrainVotes, String sessionID, String sessionDate, String sessionName) {
        this.noVotes = noVotes;
        this.yesVotes = yesVotes;
        this.abstrainVotes = abstrainVotes;
        this.sessionDate = sessionDate;
        this.sessionName = sessionName;
        this.sessionID = sessionID;
    }

    public int getYesVotes() {
        return yesVotes;
    }

    public void setYesVotes(int yesVotes) {
        this.yesVotes = yesVotes;
    }

    public int getNoVotes() {
        return noVotes;
    }

    public void setNoVotes(int noVotes) {
        this.noVotes = noVotes;
    }

    public int getAbstrainVotes() {
        return abstrainVotes;
    }

    public void setAbstrainVotes(int abstrainVotes) {
        this.abstrainVotes = abstrainVotes;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(String sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    //nema seter nego se racuna kada se pravi instanca klase
    private void updateTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        this.updated = sdf.format(new Date());
    }
    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated){this.updated = updated;}




}