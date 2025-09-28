package com.example.decideit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "DecideITdb.db";
    public static final int DATABASE_VERSION = 1;
    ///  ///////////////////////////////////////////////////////////////////////////////////////
    public static final String TABLE_USERS = "USERS";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_SURNAME = "Surname";
    public static final String COLUMN_USERNAME = "Username";
    public static final String COLUMN_STUDENT_INDEX = "Student_index";
    public static final String COLUMN_PASSWORD = "Password";
    public static final String COLUMN_ROLE = "Role";
    /// /////////////////////////////////////////////////////////////////////////////////////////////
    public static final String TABLE_SESSIONS = "SESSIONS";
    public static final String COLUMN_DATE = "Date";
    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_SESSION_NAME = "Session_name";
    public static final String COLUMN_DESCRIPTION = "Description";
    public static final String COLUMN_EOVT = "End_of_voting_time";
    /// /////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String TABLE_VOTES = "VOTES";
    public static final String COLUMN_UPDATED = "Updated_at";
    public static final String COLUMN_SESSION_ID = "Session_ID";
    public static final String COLUMN_YES = "Number_of_yes_votes";
    public static final String COLUMN_NO = "Number_of_no_votes";
    public static final String COLUMN_ABSTAIN = "Number_of_abstain_votes";
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("BAZA", "KREIRANA BAZA");
        db.execSQL("CREATE TABLE " + TABLE_USERS +
                "(" + COLUMN_NAME + " TEXT, " + COLUMN_SURNAME + " TEXT, " +
                COLUMN_USERNAME + " TEXT, " + COLUMN_PASSWORD + " TEXT, " +
                COLUMN_ROLE + " TEXT, " + COLUMN_STUDENT_INDEX + " TEXT" + ")"
        );

        db.execSQL("CREATE TABLE " + TABLE_SESSIONS +
                "(" + COLUMN_DATE + " TEXT, "  + COLUMN_ID + " TEXT, "+ COLUMN_SESSION_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " + COLUMN_EOVT + " TEXT" + ")"
        );

        db.execSQL("CREATE TABLE " + TABLE_VOTES +
                "(" + COLUMN_SESSION_NAME + " TEXT, " + COLUMN_DATE + " TEXT, " +
                COLUMN_YES + " INTEGER, " + COLUMN_NO + " INTEGER, " + COLUMN_ABSTAIN + " INTEGER, " +
                COLUMN_SESSION_ID + " TEXT, " + COLUMN_UPDATED + " TEXT" + ")"
        );
    }
    @Override

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertSession(SessionModel session){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SESSION_NAME, session.getName());
        values.put(COLUMN_DATE, session.getDateInString());
        values.put(COLUMN_DESCRIPTION, session.getStatus());
        values.put(COLUMN_ID, session.getId());

        //racunanje end of voting time - 3 dana nakon datuma sesije
        long endOfVotingTime = session.getDate() + 3* 24 * 60 * 60 * 1000; //3 dana  u ms
        values.put(COLUMN_EOVT, endOfVotingTime);

        long result = db.insert(TABLE_SESSIONS, null, values);
        if(result == -1){
            Log.e("DB", "insert VOTES failed");
        }else{
            Log.i("DB", "insert VOTES successful");
        }
        db.close();

    }
    public void clearAllSessions() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_SESSIONS, null, null); // Obriše sve redove iz tabele
            Log.i("DB_HELPER", "All sessions cleared from database");
        } catch (Exception e) {
            Log.e("DB_HELPER", "Error clearing sessions: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }


    public boolean sessionExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SESSIONS, new String[]{COLUMN_SESSION_NAME}, COLUMN_SESSION_NAME+"=?", new String[]{name}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public long getEndOfVotingTime(String id, String date){
        SQLiteDatabase db = this.getReadableDatabase();
        long endOfVotingTime=0;

        Cursor cursor = db.query(TABLE_SESSIONS,
                new String[]{COLUMN_EOVT},
                COLUMN_ID+"=? AND "+COLUMN_DATE+"=?",
                new String[]{id, date}, null, null,null);

        if(cursor.moveToFirst()){
            endOfVotingTime = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_EOVT));
        }
        Log.i("END OF VOTING TIME", "EOVT"+ endOfVotingTime);
        cursor.close();
        return endOfVotingTime;
    }
    public void insertVote(VotesModel vote){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_YES, vote.getYesVotes());
        values.put(COLUMN_NO, vote.getNoVotes());
        values.put(COLUMN_ABSTAIN, vote.getAbstrainVotes());
        values.put(COLUMN_SESSION_NAME, vote.getSessionName());
        values.put(COLUMN_DATE, vote.getSessionDate());
        values.put(COLUMN_SESSION_ID, vote.getSessionID());
        values.put(COLUMN_UPDATED, vote.getUpdated());
        long result = db.insert(TABLE_VOTES, null, values);
        if(result == -1){
            Log.e("DB", "insert VOTES failed");
        }else{
            Log.i("DB", "insert VOTES successful");
        }
        db.close();

    }

    public void updateVotes(String sessionID, String type){
        SQLiteDatabase db = this.getWritableDatabase();
        String column = null;
        if("YES".equals(type)){column = COLUMN_YES;}
        else if("NO".equals(type)){column = COLUMN_NO;}
        else if("ABSTAIN".equals(type)){column = COLUMN_ABSTAIN;}

        String sql = "UPDATE " + TABLE_VOTES +
                " SET " + column + " = " + column + " + 1" +
                " WHERE " + COLUMN_SESSION_ID + "=? ";
        db.execSQL(sql, new Object[]{sessionID});
        db.close();
        Log.i("UPDATED VOTE", "VOTE HAS BEEN UPDATED       " +column);
    }

    public String hashPassword(String password){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for(byte b : hashedBytes){
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
        catch(NoSuchAlgorithmException e){
            throw new RuntimeException("Error hashing password", e);
        }
    }
    public void insertUser(UserModel user){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_SURNAME, user.getSurname());
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_PASSWORD, hashPassword(user.getPassword()));
        values.put(COLUMN_STUDENT_INDEX, user.getIndex());
        values.put(COLUMN_ROLE, user.isRole()?"admin":"user");
        long result = db.insert(TABLE_USERS, null, values);
        if(result == -1){
            Log.e("DB", "insert USER failed");
        }else{
            Log.i("DB", "insert USER successful");
        }
        db.close();
    }

    public void updateVotesFromServer(String sessionId, int yes, int no, int abstain, String updated) {
        SQLiteDatabase db = this.getWritableDatabase();

        Log.i("DB_UPDATE_SERVER", "Updating votes for sessionId: " + sessionId);

        ContentValues values = new ContentValues();
        values.put(COLUMN_YES, yes);
        values.put(COLUMN_NO, no);
        values.put(COLUMN_ABSTAIN, abstain);
        values.put(COLUMN_UPDATED, updated);

        int rows = db.update(
                TABLE_VOTES,
                values,
                COLUMN_SESSION_ID+" = ?",
                new String[]{sessionId}
        );

        Log.i("DB_UPDATE_SERVER", "Rows updated: " + rows);

        // Ako nema redova za update, kreiraj novi
        if(rows == 0) {
            Log.i("DB_UPDATE_SERVER", "No existing row found, creating new vote record");

            // Pronađi session podatke
            Cursor sessionCursor = db.query(TABLE_SESSIONS,
                    new String[]{COLUMN_SESSION_NAME, COLUMN_DATE},
                    COLUMN_ID+"=?",
                    new String[]{sessionId}, null, null, null);

            String sessionName = "Unknown Session";
            String sessionDate = getDateInString(System.currentTimeMillis());

            if(sessionCursor != null && sessionCursor.moveToFirst()) {
                sessionName = sessionCursor.getString(sessionCursor.getColumnIndexOrThrow(COLUMN_SESSION_NAME));
                sessionDate = sessionCursor.getString(sessionCursor.getColumnIndexOrThrow(COLUMN_DATE));
                sessionCursor.close();
            }

            // Insert nov red
            ContentValues insertValues = new ContentValues();
            insertValues.put(COLUMN_YES, yes);
            insertValues.put(COLUMN_NO, no);
            insertValues.put(COLUMN_ABSTAIN, abstain);
            insertValues.put(COLUMN_UPDATED, updated);
            insertValues.put(COLUMN_SESSION_ID, sessionId);
            insertValues.put(COLUMN_SESSION_NAME, sessionName);
            insertValues.put(COLUMN_DATE, sessionDate);

            long result = db.insert(TABLE_VOTES, null, insertValues);
            Log.i("DB_UPDATE_SERVER", "Insert result: " + result);
        }

        db.close();
    }


    public List<StudentModel> getAllStudents(Context context){
        List<StudentModel> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, "Role=?", new String[]{"user"}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String surname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURNAME));
                String fullName = name + " " + surname;
                String index = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_INDEX));
                boolean gender = name.toLowerCase().endsWith("a");
                StudentModel student = new StudentModel(gender? ContextCompat.getDrawable(context, R.drawable.female) : ContextCompat.getDrawable(context, R.drawable.male ),
                        fullName,
                        index
                );
                students.add(student);
            }while(cursor.moveToNext());
        }
        Log.i("LISTA STUDENATA","BROJ STUDENATA U LISTI DBHELPER "+ students.size());
        cursor.close();
        return students;
    }

    public ArrayList<SessionModel> getSessions(long selectedDate){
        ArrayList<SessionModel> sessions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String stringDate = getDateInString(selectedDate);

        Cursor cursor;
        if(selectedDate != 0) {
            cursor = db.query(TABLE_SESSIONS, null, COLUMN_DATE + "=?", new String[]{stringDate}, null, null, null);
        }else {
            cursor = db.query(TABLE_SESSIONS, null, null, null, null, null, null);
        }
        if(cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SESSION_NAME));
                String dateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
                long date = getDateFromString(dateString);
                SessionModel session = new SessionModel(name, date, status);
                session.setId(id);
                sessions.add(session);
            } while (cursor.moveToNext());

        }

        Log.i("LISTA SESIJA","BROJ SESIJA U DBHELPER "+ sessions.size());
        cursor.close();
        return sessions;
    }

    public VotesModel getVotes(String sessionID){
        SQLiteDatabase db = this.getReadableDatabase();
        VotesModel votes = null;

        Log.i("DB_GET_VOTES", "Looking for sessionID: " + sessionID);

        // ISPRAVKA: Uključite sve potrebne kolone u SELECT
        Cursor cursor = db.query(TABLE_VOTES,
                new String[]{COLUMN_YES, COLUMN_NO, COLUMN_ABSTAIN, COLUMN_SESSION_NAME, COLUMN_DATE, COLUMN_SESSION_ID},
                COLUMN_SESSION_ID+"=?",
                new String[] {sessionID}, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
            int yes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_YES));
            int no = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NO));
            int abstain = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ABSTAIN));
            String sessionName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SESSION_NAME));
            String sessionDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));

            Log.i("DB_GET_VOTES", "Found votes - yes: "+yes+", no: "+no+", abstain: "+abstain);
            votes = new VotesModel(no, yes, abstain, sessionID, sessionName, sessionDate);
        } else {
            Log.w("DB_GET_VOTES", "No votes found for sessionID: " + sessionID);
            // Kreirajte prazan votes objekat ako ne postoji
            votes = createEmptyVotesForSession(sessionID);
        }

        if(cursor != null) {
            cursor.close();
        }
        db.close();
        return votes;
    }
    private VotesModel createEmptyVotesForSession(String sessionID) {
        Log.i("DB_CREATE_EMPTY", "Creating empty votes for sessionID: " + sessionID);

        SQLiteDatabase db = this.getReadableDatabase();

        // Pronađi session podatke
        Cursor sessionCursor = db.query(TABLE_SESSIONS,
                new String[]{COLUMN_SESSION_NAME, COLUMN_DATE},
                COLUMN_ID+"=?",
                new String[]{sessionID}, null, null, null);

        String sessionName = "Unknown Session";
        String sessionDate = getDateInString(System.currentTimeMillis());

        if(sessionCursor != null && sessionCursor.moveToFirst()) {
            sessionName = sessionCursor.getString(sessionCursor.getColumnIndexOrThrow(COLUMN_SESSION_NAME));
            sessionDate = sessionCursor.getString(sessionCursor.getColumnIndexOrThrow(COLUMN_DATE));
            sessionCursor.close();
        }

        db.close();

        // Kreiraj prazan votes objekat
        VotesModel emptyVotes = new VotesModel(0, 0, 0, sessionID, sessionName, sessionDate);

        // Ubaci u bazu
        insertVote(emptyVotes);

        return emptyVotes;
    }
    public String getDateInString(long date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = sdf.format(date);
        return dateString;
    }
    public boolean findUsername(String username){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, null, "Username=?", new String[]{username}, null, null, null);
        boolean exists = c.moveToFirst();
        c.close();
        db.close();
        return exists;
    }

    public UserModel checkUsernamePassword(String username, String password){
        SQLiteDatabase db = getReadableDatabase();
        String hashedPassword = hashPassword(password);
        Cursor c = db.query(TABLE_USERS, null, "Username=? AND Password=?", new String[]{username, hashedPassword}, null, null, null);
        UserModel user = null;
        if(c.moveToFirst()){
            String name = c.getString(c.getColumnIndexOrThrow(COLUMN_NAME));
            String surname = c.getString(c.getColumnIndexOrThrow(COLUMN_SURNAME));
            String index = c.getString(c.getColumnIndexOrThrow(COLUMN_STUDENT_INDEX));
            boolean role = "admin".equals(index);

            user = new UserModel(name, surname, username, password, role, index);
        }
        c.close();
        db.close();
        return user;
    }

    public void removeUser(String index){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_USERS, COLUMN_STUDENT_INDEX + "=?", new String[]{index});
        db.close();
        Log.i("DELETE", "user deleted from TABLE_USERS");
    }

    public long getDateFromString(String stringDate){
        long dateMillis = 0;
        try {
            // Format u kojem se datum čuva kao string
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            Date date = sdf.parse(stringDate);
            if (date != null) {
                dateMillis = date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateMillis;
    }



}