package br.com.suavez;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public final class MainActivity extends Activity {
    private static final int BG = Color.rgb(248, 249, 246);
    private static final int INK = Color.rgb(35, 54, 48);
    private static final int GREEN = Color.rgb(41, 93, 77);
    private static final int MUTED = Color.rgb(119, 133, 126);
    private static final int PERMISSION_CODE = 31;

    private TextView countText, unitText, dateText, noticeText, changeText;
    private Button mainButton;

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        scroll.setVerticalScrollBarEnabled(false);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(28), dp(32), dp(28), dp(34));
        scroll.addView(root, new ScrollView.LayoutParams(-1, -1));

        TextView brand = text("suavez.", 33, GREEN, true);
        brand.setGravity(Gravity.CENTER);
        root.addView(brand);

        TextView tagline = text("seu lembrete, sem complicação", 13, MUTED, false);
        tagline.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tagLp = new LinearLayout.LayoutParams(-1, -2);
        tagLp.topMargin = dp(5);
        root.addView(tagline, tagLp);

        root.addView(spacer(), new LinearLayout.LayoutParams(1, 0, 1));

        TextView heading = text("PRÓXIMA APLICAÇÃO", 12, MUTED, true);
        heading.setLetterSpacing(0.18f);
        heading.setGravity(Gravity.CENTER);
        root.addView(heading);

        countText = text("—", 91, INK, true);
        countText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams countLp = new LinearLayout.LayoutParams(-1, -2);
        countLp.topMargin = dp(10);
        root.addView(countText, countLp);

        unitText = text("", 15, MUTED, false);
        unitText.setGravity(Gravity.CENTER);
        root.addView(unitText);

        dateText = text("", 22, INK, true);
        dateText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams dateLp = new LinearLayout.LayoutParams(-1, -2);
        dateLp.topMargin = dp(25);
        root.addView(dateText, dateLp);

        root.addView(spacer(), new LinearLayout.LayoutParams(1, 0, 1));

        mainButton = new Button(this);
        mainButton.setAllCaps(false);
        mainButton.setTextSize(16);
        mainButton.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        mainButton.setTextColor(Color.WHITE);
        mainButton.setBackground(rounded(GREEN, 18));
        mainButton.setElevation(0);
        LinearLayout.LayoutParams buttonLp = new LinearLayout.LayoutParams(-1, dp(59));
        root.addView(mainButton, buttonLp);

        changeText = text("Alterar última data", 14, GREEN, false);
        changeText.setGravity(Gravity.CENTER);
        changeText.setPadding(dp(12), dp(17), dp(12), dp(15));
        LinearLayout.LayoutParams changeLp = new LinearLayout.LayoutParams(-1, -2);
        changeLp.topMargin = dp(8);
        root.addView(changeText, changeLp);

        TextView interval = text("A cada 10 dias  ·  aviso às 9h", 13, MUTED, false);
        interval.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams intervalLp = new LinearLayout.LayoutParams(-1, -2);
        intervalLp.topMargin = dp(18);
        root.addView(interval, intervalLp);

        noticeText = text("", 12, MUTED, false);
        noticeText.setGravity(Gravity.CENTER);
        noticeText.setPadding(0, dp(15), 0, 0);
        root.addView(noticeText);

        mainButton.setOnClickListener(view -> {
            if (LocalSettings.getLastApplication(this) == null) {
                chooseDate();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Registrar aplicação hoje?")
                        .setMessage("O próximo lembrete será em 10 dias.")
                        .setNegativeButton("Cancelar", null)
                        .setPositiveButton("Registrar", (dialog, which) -> saveDate(LocalDate.now()))
                        .show();
            }
        });
        changeText.setOnClickListener(view -> chooseDate());
        noticeText.setOnClickListener(view -> requestNotificationPermission());

        setContentView(scroll);
        showState();
    }

    private View spacer() { return new View(this); }

    private TextView text(String value, int sp, int color, boolean bold) {
        TextView label = new TextView(this);
        label.setText(value);
        label.setTextSize(sp);
        label.setTextColor(color);
        label.setTypeface(Typeface.create(
                bold ? "sans-serif-medium" : "sans-serif", Typeface.NORMAL));
        return label;
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(dp(radius));
        return shape;
    }

    private void chooseDate() {
        LocalDate current = LocalSettings.getLastApplication(this);
        if (current == null) current = LocalDate.now();
        DatePickerDialog picker = new DatePickerDialog(this,
                (view, year, month, day) -> saveDate(LocalDate.of(year, month + 1, day)),
                current.getYear(), current.getMonthValue() - 1, current.getDayOfMonth());
        picker.getDatePicker().setMaxDate(System.currentTimeMillis());
        picker.setTitle("Data da última aplicação");
        picker.show();
    }

    private void saveDate(LocalDate day) {
        LocalSettings.setLastApplication(this, day);
        ReminderScheduler.schedule(this);
        showState();
        requestNotificationPermission();
    }

    private void showState() {
        LocalDate last = LocalSettings.getLastApplication(this);
        if (last == null) {
            countText.setText("—");
            countText.setTextSize(91);
            unitText.setText("Defina a última aplicação");
            dateText.setText("Sem data definida");
            mainButton.setText("Definir data");
            changeText.setVisibility(View.GONE);
            noticeText.setText("");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate next = DateCycle.next(last, today);
        long days = ChronoUnit.DAYS.between(today, next);

        countText.setText(days == 0 ? "Hoje" : String.valueOf(days));
        countText.setTextSize(days == 0 ? 72 : 91);
        unitText.setText(days == 0 ? "é o dia programado" : days == 1 ? "dia restante" : "dias restantes");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d 'de' MMMM", new Locale("pt", "BR"));
        String when = next.format(fmt);
        if (next.getYear() != today.getYear()) when += " de " + next.getYear();
        dateText.setText(when);

        mainButton.setText("Apliquei hoje");
        changeText.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            noticeText.setText("Avisos bloqueados · toque para permitir");
        } else {
            noticeText.setText("Notificações ativas no aparelho");
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ReminderScheduler.schedule(this);
        if (countText != null) showState();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == PERMISSION_CODE) showState();
    }
}
