package com.mobiletune.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int PICK_AUDIO = 41;
    private EditText numberField;
    private TextView trackLabel;
    private Uri audioUri;
    private MediaPlayer player;
    private boolean playing = false;
    private final int ink = Color.rgb(27, 34, 57);
    private final int muted = Color.rgb(112, 122, 148);
    private final int purple = Color.rgb(112, 87, 255);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(246, 247, 255));
        getWindow().setNavigationBarColor(Color.rgb(246, 247, 255));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        buildUi();
    }
    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); return d;
    }
    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return t;
    }
    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l;
    }
    private void pad(View v, int left, int top, int right, int bottom) { v.setPadding(left, top, right, bottom); }
    private LinearLayout.LayoutParams lp(int w, int h) { return new LinearLayout.LayoutParams(w, h); }
    private Button button(String label, int bg, int fg) {
        Button b = new Button(this); b.setText(label); b.setTextSize(15); b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setAllCaps(false); b.setTextColor(fg); b.setBackground(shape(bg, 28)); b.setPadding(22, 12, 22, 12); return b;
    }
    private void buildUi() {
        LinearLayout root = column(); root.setBackgroundColor(Color.rgb(246, 247, 255)); pad(root, 22, 18, 22, 18);
        LinearLayout header = new LinearLayout(this); header.setGravity(Gravity.CENTER_VERTICAL);
        TextView logo = text("♫", 26, Color.WHITE, true); logo.setGravity(Gravity.CENTER); logo.setBackground(shape(purple, 20));
        header.addView(logo, lp(54, 54));
        LinearLayout titleBox = column(); pad(titleBox, 12, 0, 0, 0);
        titleBox.addView(text("MobileTune", 24, ink, true)); titleBox.addView(text("השיחה שלך, בקצב שלך", 13, muted, false));
        header.addView(titleBox, new LinearLayout.LayoutParams(0, -2, 1)); root.addView(header);

        TextView hero = text("הופכים את החיוג לחוויה 🎧", 24, Color.WHITE, true);
        pad(hero, 20, 24, 20, 8); hero.setBackground(shape(Color.rgb(54, 48, 108), 26));
        LinearLayout.LayoutParams heroLp = lp(-1, -2); heroLp.topMargin = 24; root.addView(hero, heroLp);
        TextView heroSub = text("בחרו קטע מוזיקה, האזינו לו ואז המשיכו לחיוג הרגיל.", 14, Color.rgb(228, 226, 255), false);
        pad(heroSub, 20, 4, 20, 20); heroSub.setBackground(shape(Color.rgb(54, 48, 108), 26)); root.addView(heroSub, lp(-1, -2));

        LinearLayout musicCard = column(); musicCard.setBackground(shape(Color.WHITE, 24)); pad(musicCard, 18, 18, 18, 18);
        LinearLayout.LayoutParams cardLp = lp(-1, -2); cardLp.topMargin = 20; root.addView(musicCard, cardLp);
        musicCard.addView(text("01  ·  קטע המוזיקה שלך", 17, ink, true));
        musicCard.addView(text("בחרו קובץ MP3 או אודיו מהמכשיר", 13, muted, false));
        trackLabel = text("לא נבחר קובץ עדיין", 14, ink, true); pad(trackLabel, 14, 14, 14, 14);
        trackLabel.setBackground(shape(Color.rgb(244, 243, 255), 16));
        LinearLayout.LayoutParams trackLp = lp(-1, -2); trackLp.topMargin = 14; musicCard.addView(trackLabel, trackLp);
        Button choose = button("＋  בחירת קובץ מוזיקה", purple, Color.WHITE);
        LinearLayout.LayoutParams chooseLp = lp(-1, -2); chooseLp.topMargin = 12; musicCard.addView(choose, chooseLp);
        choose.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT); i.setType("audio/*"); i.addCategory(Intent.CATEGORY_OPENABLE); startActivityForResult(i, PICK_AUDIO);
        });
        Button preview = button("▶  נגן / עצור תצוגה מקדימה", Color.rgb(237, 235, 255), purple);
        LinearLayout.LayoutParams previewLp = lp(-1, -2); previewLp.topMargin = 8; musicCard.addView(preview, previewLp);
        preview.setOnClickListener(v -> togglePreview());

        LinearLayout dialCard = column(); dialCard.setBackground(shape(Color.WHITE, 24)); pad(dialCard, 18, 18, 18, 18);
        LinearLayout.LayoutParams dialLp = lp(-1, -2); dialLp.topMargin = 16; root.addView(dialCard, dialLp);
        dialCard.addView(text("02  ·  למי מתקשרים?", 17, ink, true));
        dialCard.addView(text("הקלידו מספר טלפון כדי לפתוח את החייגן", 13, muted, false));
        numberField = new EditText(this); numberField.setSingleLine(true); numberField.setTextSize(22); numberField.setHint("050-000-0000");
        numberField.setInputType(3); numberField.setTextColor(ink); numberField.setBackground(shape(Color.rgb(246, 247, 255), 16));
        pad(numberField, 16, 14, 16, 14); LinearLayout.LayoutParams numberLp = lp(-1, -2); numberLp.topMargin = 14; dialCard.addView(numberField, numberLp);
        Button call = button("פתיחת החייגן  →", Color.rgb(35, 178, 130), Color.WHITE);
        LinearLayout.LayoutParams callLp = lp(-1, -2); callLp.topMargin = 14; dialCard.addView(call, callLp);
        call.setOnClickListener(v -> openDialer());

        TextView note = text("חשוב לדעת: Android לא מאפשר לאפליקציה רגילה להחליף את המיקרופון במוזיקה בשיחה סלולרית. האפליקציה מנגנת את הקובץ במכשיר ופותחת את החייגן הרגיל — היא לא משדרת את הקובץ לצד השני.", 12, muted, false);
        pad(note, 6, 16, 6, 8); root.addView(note);
        TextView footer = text("נוצר ע״י מטען נייד ממתמחים טופ 2026", 12, muted, true);
        footer.setGravity(Gravity.CENTER); pad(footer, 0, 12, 0, 4); root.addView(footer);
        setContentView(root);
    }
    private void togglePreview() {
        if (audioUri == null) { Toast.makeText(this, "קודם בחרו קובץ מוזיקה", Toast.LENGTH_SHORT).show(); return; }
        try {
            if (playing && player != null) { player.pause(); playing = false; return; }
            if (player != null) { player.release(); player = null; }
            player = new MediaPlayer(); player.setDataSource(this, audioUri);
            player.setOnCompletionListener(mp -> playing = false); player.prepare(); player.start(); playing = true;
        } catch (Exception e) { Toast.makeText(this, "לא הצלחנו לנגן את הקובץ הזה", Toast.LENGTH_LONG).show(); }
    }
    private void openDialer() {
        String number = numberField.getText().toString().trim();
        if (number.isEmpty()) { numberField.setError("נא להזין מספר טלפון"); return; }
        new AlertDialog.Builder(this).setTitle("נוצר ע״י מטען נייד ממתמחים טופ 2026")
            .setMessage("החייגן הרגיל ייפתח עכשיו. שימו לב: המוזיקה לא תועבר לצד השני בשיחה.")
            .setNegativeButton("ביטול", null).setPositiveButton("המשך לחייגן", (dialog, which) -> {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(number))); startActivity(i);
            }).show();
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            audioUri = data.getData();
            try { getContentResolver().takePersistableUriPermission(audioUri, Intent.FLAG_GRANT_READ_URI_PERMISSION); } catch (Exception ignored) {}
            String name = audioUri.getLastPathSegment(); trackLabel.setText("✓ קובץ נבחר\n" + (name == null ? "קובץ אודיו" : name));
        }
    }
    @Override protected void onDestroy() {
        if (player != null) { player.release(); player = null; } super.onDestroy();
    }
}
