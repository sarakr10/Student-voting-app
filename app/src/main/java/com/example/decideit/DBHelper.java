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
    public static final String COLUMN_SESSION_NAME = "Session_name";
    public static final String COLUMN_DESCRIPTION = "Description";
    public static final String COLUMN_EOVT = "End_of_voting_time";
/// /////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String TABLE_VOTES = "VOTES";
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
                "(" + COLUMN_DATE + " TEXT, " + COLUMN_SESSION_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " + COLUMN_EOVT + " TEXT" + ")"
        );

        db.execSQL("CREATE TABLE " + TABLE_VOTES +
                "(" + COLUMN_YES + " INTEGER, " + COLUMN_NO + " INTEGER, " +
                COLUMN_ABSTAIN + " INTEGER, " + COLUMN_SESSION_NAME + " TEXT, " +
                COLUMN_DATE + " TEXT" + ")"
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

    public boolean sessionExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SESSIONS, new String[]{COLUMN_SESSION_NAME}, COLUMN_SESSION_NAME+"=?", new String[]{name}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public long getEndOfVotingTime(String name, String date){
        SQLiteDatabase db = this.getReadableDatabase();
        long endOfVotingTime=0;

        Cursor cursor = db.query(TABLE_SESSIONS,
                                new String[]{COLUMN_EOVT},
                                COLUMN_SESSION_NAME+"=? AND "+COLUMN_DATE+"=?",
                                new String[]{name, date}, null, null,null);

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
        long result = db.insert(TABLE_VOTES, null, values);
        if(result == -1){
            Log.e("DB", "insert VOTES failed");
        }else{
            Log.i("DB", "insert VOTES successful");
        }
        db.close();

    }

    public void updateVotes(String sessionName, long sessionDate, String type){
        SQLiteDatabase db = this.getWritableDatabase();
        String column = null;
        if("YES".equals(type)){column = COLUMN_YES;}
        else if("NO".equals(type)){column = COLUMN_NO;}
        else if("ABSTAIN".equals(type)){column = COLUMN_ABSTAIN;}

        String sql = "UPDATE " + TABLE_VOTES +
                " SET " + column + " = " + column + " + 1" +
                " WHERE " + COLUMN_SESSION_NAME + "=? AND " + COLUMN_DATE + "=?";
        db.execSQL(sql, new Object[]{sessionName, getDateInString(sessionDate)});
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
                        long date = getDateFromString(dateString);
                        SessionModel session = new SessionModel(name, date, status);
                        sessions.add(session);
                    } while (cursor.moveToNext());

            }

        Log.i("LISTA SESIJA","BROJ SESIJA U DBHELPER "+ sessions.size());
        cursor.close();
        return sessions;
    }

    public VotesModel getVotes(String sessionName, String sessionDate){
        SQLiteDatabase db = this.getReadableDatabase();
        VotesModel votes = null;

        Cursor cursor = db.query(TABLE_VOTES, new String[]{COLUMN_YES, COLUMN_NO, COLUMN_ABSTAIN},
                        COLUMN_SESSION_NAME+"=? AND "+COLUMN_DATE+"=?",
                                new String[] {sessionName,sessionDate}, null, null, null);
        if(cursor!=null && cursor.moveToFirst()){
        int yes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_YES));
        int no = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NO));
        int abstain = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ABSTAIN));
        Log.i("BROJ GLASOVA", "yes  "+yes+"  no  "+no+"  abstain  "+abstain);
        votes = new VotesModel(no, yes, abstain, sessionName, sessionDate);
        cursor.close();
        }
        return votes;
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
