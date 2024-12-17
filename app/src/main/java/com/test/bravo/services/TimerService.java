package com.test.bravo.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.test.bravo.R;
public class TimerService extends Service {
    private CountDownTimer timer;
    private long remainingTime;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        remainingTime = intent.getLongExtra("timeInSeconds", 0) * 1000;

        // Start the service as a foreground service
        startForeground(1, createNotification("Timer started"));

        // Initialize and start the timer
        timer = new CountDownTimer(remainingTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished;
                int minutes = (int) ((remainingTime/ 1000) / 60);
                int seconds = (int) ((remainingTime / 1000) % 60);
                String formattedTime = String.format("%02d:%02d", minutes, seconds);
                updateNotification(String.valueOf(formattedTime));
            }

            @Override
            public void onFinish() {
                // Cancel the notification
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.cancel(1); // Use the same notification ID
                }

                // Stop the service when the timer finishes
                stopSelf();
            }
        };
        timer.start();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification(String contentText) {
        return new NotificationCompat.Builder(this, "timer_channel")
                .setContentTitle("Bravo: set timer")
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true) // Persistent notification
                .build();
    }

    private void updateNotification(String contentText) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = createNotification(contentText);
        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "timer_channel",
                    "Timer Notifications",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}