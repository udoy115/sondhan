package com.sondhan.app.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.sondhan.app.MainActivity;
import com.sondhan.app.R;

public class FactCheckForegroundService extends Service {
    public static final String CHANNEL_ID = "sondhan_channel_verification";
    public static final int NOTIFICATION_ID = 101;

    public static final String ACTION_START = "com.sondhan.app.action.START_VERIFICATION";
    public static final String ACTION_UPDATE = "com.sondhan.app.action.UPDATE_VERIFICATION";
    public static final String ACTION_STOP = "com.sondhan.app.action.STOP_VERIFICATION";

    public static final String EXTRA_CLAIM = "extra_claim";
    public static final String EXTRA_VERDICT = "extra_verdict";
    public static final String EXTRA_CONFIDENCE = "extra_confidence";

    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) return START_NOT_STICKY;

        String action = intent.getAction();
        if (ACTION_START.equals(action)) {
            String claim = intent.getStringExtra(EXTRA_CLAIM);
            startForeground(NOTIFICATION_ID, buildProgressNotification(claim != null ? claim : "Verifying claim..."));
        } else if (ACTION_UPDATE.equals(action)) {
            String verdict = intent.getStringExtra(EXTRA_VERDICT);
            int confidence = intent.getIntExtra(EXTRA_CONFIDENCE, 0);
            if (notificationManager != null) {
                notificationManager.notify(NOTIFICATION_ID, buildResultNotification(verdict, confidence));
            }
        } else if (ACTION_STOP.equals(action)) {
            stopForeground(STOP_FOREGROUND_REMOVE);
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Fact-Check Progress",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Shows active verification status for fact-checked claims");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private Notification buildProgressNotification(String claim) {
        Intent openAppIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("Sondhani AI Fact Check Active")
                .setContentText(claim)
                .setSubText("Analyzing claim against trusted sources")
                .setProgress(100, 50, true)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private Notification buildResultNotification(String verdict, int confidence) {
        Intent openAppIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        String verdictText = (verdict != null ? verdict.toUpperCase() : "VERIFIED") + " (" + confidence + "% confidence)";

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("Fact-Check Complete")
                .setContentText("Verdict: " + verdictText)
                .setProgress(0, 0, false)
                .setOngoing(false)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
    }

    public static void startVerification(Context context, String claim) {
        try {
            Intent intent = new Intent(context, FactCheckForegroundService.class);
            intent.setAction(ACTION_START);
            intent.putExtra(EXTRA_CLAIM, claim);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        } catch (Exception ignored) {}
    }

    public static void completeVerification(Context context, String verdict, int confidence) {
        try {
            Intent intent = new Intent(context, FactCheckForegroundService.class);
            intent.setAction(ACTION_UPDATE);
            intent.putExtra(EXTRA_VERDICT, verdict);
            intent.putExtra(EXTRA_CONFIDENCE, confidence);
            context.startService(intent);
        } catch (Exception ignored) {}
    }

    public static void stopVerification(Context context) {
        try {
            Intent intent = new Intent(context, FactCheckForegroundService.class);
            intent.setAction(ACTION_STOP);
            context.startService(intent);
        } catch (Exception ignored) {}
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

