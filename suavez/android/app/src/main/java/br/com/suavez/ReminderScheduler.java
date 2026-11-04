package br.com.suavez;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.time.LocalDate;
import java.time.ZoneId;

final class ReminderScheduler {
    private ReminderScheduler() {}

    private static PendingIntent pending(Context context) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        return PendingIntent.getBroadcast(context, 10, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    static void schedule(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager == null) return;

        PendingIntent pi = pending(context);
        manager.cancel(pi);
        LocalDate last = LocalSettings.getLastApplication(context);
        if (last == null) return;

        LocalDate today = LocalDate.now();
        LocalDate due = DateCycle.next(last, today);
        if (due.equals(today) && LocalSettings.hasNotified(context, today)) {
            due = due.plusDays(10);
        }

        // 9h locais. Alarmes inexatos dispensam permissões especiais no Android 14.
        long timestamp = due.atTime(9, 0).atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();
        if (due.equals(today) && timestamp <= System.currentTimeMillis()) {
            timestamp = System.currentTimeMillis() + 10000L;
        }

        manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, pi);
    }
}
