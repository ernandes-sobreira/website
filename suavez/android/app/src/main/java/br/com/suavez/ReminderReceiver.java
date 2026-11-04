package br.com.suavez;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import java.time.LocalDate;

public final class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "lembretes";

    @Override
    public void onReceive(Context context, Intent intent) {
        LocalDate last = LocalSettings.getLastApplication(context);
        LocalDate today = LocalDate.now();

        if (last != null && DateCycle.next(last, today).equals(today)
                && !LocalSettings.hasNotified(context, today)) {
            LocalSettings.markNotified(context, today);
            notifyUser(context);
        }
        ReminderScheduler.schedule(context);
    }

    private static void notifyUser(Context context) {
        if (Build.VERSION.SDK_INT >= 33 && context.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager == null) return;
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "Lembretes Suavez", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Um lembrete a cada 10 dias.");
        manager.createNotificationChannel(channel);

        Intent open = new Intent(context, MainActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent action = PendingIntent.getActivity(context, 12, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Suavez")
                .setContentText("Hoje é um dia programado. Confira no aplicativo.")
                .setContentIntent(action)
                .setAutoCancel(true)
                .build();
        manager.notify(10, notification);
    }
}
