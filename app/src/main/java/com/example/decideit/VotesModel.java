package com.example.decideit;

public class VotesModel {
    private int yesVotes;
    private int noVotes;
    private int abstrainVotes;
    private String sessionName;
    private String sessionDate;

    public VotesModel(int noVotes, int yesVotes, int abstrainVotes, String sessionDate, String sessionName) {
        this.noVotes = noVotes;
        this.yesVotes = yesVotes;
        this.abstrainVotes = abstrainVotes;
        this.sessionDate = sessionDate;
        this.sessionName = sessionName;
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
}
