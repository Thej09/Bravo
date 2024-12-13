package com.test.bravo.workers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;

import com.test.bravo.R;

public class TimerWorker extends Worker {

    private static final String CHANNEL_ID = "TimerWorkerChannel";

    public TimerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        createNotificationChannel(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setForegroundAsync(createForegroundInfo(context)); // Attach to foreground service
        }

        // Vibrate 3 times
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] vibrationPattern = {0, 500, 250, 500, 250, 500};
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1));
            }
        }

        // Add a delay to keep the foreground service active during vibration
        try {
            Thread.sleep(2000); // Delay in milliseconds to match vibration duration
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Result.success();
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private ForegroundInfo createForegroundInfo(Context context) {
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Timer Running")
                .setContentText("Vibration task in progress.")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Ensure the icon exists
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM) // Mark as an alarm notification
                .setOngoing(true) // Prevent user dismissal
                .build();

        return new ForegroundInfo(1, notification);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Timer Worker Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}