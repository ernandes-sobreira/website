package br.com.suavez;

import android.content.Context;
import android.content.SharedPreferences;
import java.time.LocalDate;

final class LocalSettings {
    private static final String NAME = "suavez_local";
    private static final String LAST = "last_application";
    private static final String NOTIFIED = "last_notification";

    private LocalSettings() {}

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    static LocalDate getLastApplication(Context context) {
        String value = prefs(context).getString(LAST, null);
        if (value == null) return null;
        try {
            return LocalDate.parse(value);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    static void setLastApplication(Context context, LocalDate day) {
        prefs(context).edit().putString(LAST, day.toString()).remove(NOTIFIED).apply();
    }

    static boolean hasNotified(Context context, LocalDate day) {
        return day.toString().equals(prefs(context).getString(NOTIFIED, ""));
    }

    static void markNotified(Context context, LocalDate day) {
        prefs(context).edit().putString(NOTIFIED, day.toString()).apply();
    }
}
