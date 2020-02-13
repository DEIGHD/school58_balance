package com.deighd.school58.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.deighd.school58.MainActivity;
import com.deighd.school58.R;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class Notifications {
    private static final String TAG = "Notifications";

    private static int getRandomNumber() {
        return new Random().nextInt(100000);
    }

    public static Integer createNotification(
            Context context,
            String message,
            String title,
            Integer notificationCounter
    ) {
        String CHANNEL_ID = "Notifications";

        Intent iLaunchMainActivity = new Intent(context, MainActivity.class);
        iLaunchMainActivity.putExtra("action", 1);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                getRandomNumber(),
                iLaunchMainActivity,
                0
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_ruble)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(contentIntent)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setDefaults(
                                NotificationCompat.DEFAULT_SOUND
                                | NotificationCompat.DEFAULT_VIBRATE
                                | NotificationCompat.DEFAULT_LIGHTS
                        )
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Notifications";
            String description = "Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new  NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(notificationCounter++, builder.build());
        return notificationCounter;
    }
}
