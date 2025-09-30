package com.example.decideit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    private static final String CHANNEL_ID = "SESSION_NOTIFICATION_CHANNEL";
    private static final long CHECK_INTERVAL = 60000; // proverava svaki minut
    private static final long WARNING_TIME = 2*24*60 * 60 * 1000; // u naredna tri dana radi lakseg testiranja

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private NotificationManager notificationManager;
    private Handler checkHandler;
    private Runnable checkRunnable;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.d("Notification service", "Service handler processing message");
            startSessionChecking();
        }
    }

    public NotificationService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //nit sa svojim looperom koja asinhrono obradjuje poruke
        HandlerThread thread = new HandlerThread("SessionNotificationThread", Process.THREAD_PRIORITY_FOREGROUND);
        thread.start();

        //kontrolise red poruka koje se obradjuju u niti
        serviceLooper = thread.getLooper();

        //klasa u kojoj definisemo handleMessage tj kako da se obradi svaka poruka
        serviceHandler = new ServiceHandler(serviceLooper);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();

        //kreiranje handlera za periodicne provere
        checkHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Notification service started", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Service started");

        Message msg = new Message();
        msg.arg1 = startId;                     //identifikator klijenta koji je pozvao servis
        serviceHandler.sendMessage(msg);

        return START_STICKY;            //ako servis bude unisten sam ce se ponovo pokrenuti
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (checkHandler != null && checkRunnable != null) {
            checkHandler.removeCallbacks(checkRunnable);
        }

        if (serviceLooper != null) {
            serviceLooper.quit();
        }

        Log.d(TAG, "NotificationService destroyed");
    }

    private void createNotificationChannel() {
            CharSequence name = "Session Notifications";
            String description = "Notifications for upcoming session expirations";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel created");
    }

    private void startSessionChecking() {
        //ocisti prethodne zadatke da se ne bi dupliralo ivrsavanje
        if (checkHandler != null && checkRunnable != null) {
            checkHandler.removeCallbacks(checkRunnable);
        }

        checkRunnable = new Runnable() {
            @Override
            public void run() {
                checkUpcomingSessions();
                checkHandler.postDelayed(this, CHECK_INTERVAL);     //nakon izvrsavanja sam sebe ponovo zakazuje
            }
        };

        checkHandler.post(checkRunnable);       //stavlja checkRunnable u red za izvrsenje na niti
        Log.d(TAG, "Session checking started - every minute");
    }

    private void checkUpcomingSessions() {
        try {
            long currentTime = System.currentTimeMillis();
            long warningTime = currentTime + WARNING_TIME;

            Log.d(TAG, "=== CHECKING SESSIONS ===");
            Log.d(TAG, "Current time: " + new Date(currentTime));
            Log.d(TAG, "Warning time: " + new Date(warningTime));

            List<SessionModel> upcomingSessions = getUpcomingSessions(currentTime, warningTime);
            Log.d(TAG, "Found " + upcomingSessions.size() + " upcoming sessions");

            for (SessionModel session : upcomingSessions) {
                sendNotification(session);
                Log.d(TAG, "Notification sent for session: " + session.getName());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error checking upcoming sessions", e);
        }
    }

    private List<SessionModel> getUpcomingSessions(long currentTime, long warningTime) {
        List<SessionModel> upcomingSessions = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(this);

        try {
            ArrayList<SessionModel> allSessions = dbHelper.getSessions(0);

            for (SessionModel session : allSessions) {
                long endOfVotingTime = dbHelper.getEndOfVotingTime(session.getId(), session.getDateInString());

                if (endOfVotingTime > currentTime && endOfVotingTime <= warningTime) {
                    upcomingSessions.add(session);
                    Log.d(TAG, "Found upcoming session: " + session.getName() +
                            " expires at: " + new Date(endOfVotingTime));
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting upcoming sessions from database", e);
        }

        return upcomingSessions;
    }

    private void sendNotification(SessionModel session) {
        Intent intent = new Intent(this, StudentViewActivity.class);
        intent.putExtra("OPEN_FRAGMENT", "CALENDAR");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int notificationId = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("DecideIT")
                .setContentText(session.getName() + " - Voting ends soon!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)                        //uklanja obavestenje kada se klikne na njega
                .setContentIntent(pendingIntent);           //reakcija na klik na obavestenje je otvaranje aktivnosti

        notificationManager.notify(notificationId, builder.build());    //da se obavestenje pojavi

        Log.d(TAG, "Notification sent for session: " + session.getName());
    }
}