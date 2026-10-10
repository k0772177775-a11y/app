package com.mobiletune.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    private LinearLayout content, nav;
    private TextView pageTitle;
    private SharedPreferences prefs;
    private boolean dark = false;
    private String currentTab = "בית";
    private int bg, card, ink, muted, purple, green, orange;
    private final ArrayList<String> tasks = new ArrayList<>();
    private final ArrayList<String> expenses = new ArrayList<>();
    private final ArrayList<String> notes = new ArrayList<>();
    private final ArrayList<String> documents = new ArrayList<>();
    private final ArrayList<String> goals = new ArrayList<>();
    private final ArrayList<String> events = new ArrayList<>();
    private double totalSpent = 0;
    private static final int PICK_DOCUMENT = 52;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("lifehub", MODE_PRIVATE);
        dark = prefs.getBoolean("dark", false);
        loadList("tasks", tasks);
        loadList("expenses", expenses);
        loadList("notes", notes);
        loadList("documents", documents);
        loadList("goals", goals);
        loadList("events", events);
        totalSpent = prefs.getFloat("spent", 0f);
        updatePalette();
        buildShell();
        showTab("בית");
    }

    private void updatePalette() {
        bg = dark ? Color.rgb(16,18,30) : Color.rgb(246,247,252);
        card = dark ? Color.rgb(29,32,49) : Color.WHITE;
        ink = dark ? Color.rgb(245,246,255) : Color.rgb(27,34,57);
        muted = dark ? Color.rgb(166,174,199) : Color.rgb(112,122,148);
        purple = Color.rgb(105,83,245);
        green = Color.rgb(32,169,125);
        orange = Color.rgb(235,143,65);
    }
    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); return d;
    }
    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setGravity(Gravity.CENTER_VERTICAL); return t;
    }
    private LinearLayout column() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l; }
    private LinearLayout row() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); l.setGravity(Gravity.CENTER_VERTICAL); return l; }
    private void pad(View v,int l,int t,int r,int b) { v.setPadding(l,t,r,b); }
    private LinearLayout.LayoutParams lp(int w,int h) { return new LinearLayout.LayoutParams(w,h); }
    private Button button(String s,int color,int fg) {
        Button b = new Button(this); b.setText(s); b.setTextSize(14); b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        b.setAllCaps(false); b.setTextColor(fg); b.setBackground(shape(color,22)); b.setPadding(10,7,10,7); return b;
    }
    private void buildShell() {
        LinearLayout root = column(); root.setBackgroundColor(bg);
        LinearLayout header = row(); pad(header,18,12,18,10);
        TextView logo = text("✦",25,Color.WHITE,true); logo.setGravity(Gravity.CENTER); logo.setBackground(shape(purple,17)); header.addView(logo,lp(48,48));
        LinearLayout brand = column(); pad(brand,12,0,0,0);
        brand.addView(text("LifeHub",23,ink,true));
        brand.addView(text("כל מה שחשוב. במקום אחד.",12,muted,false));
        header.addView(brand,new LinearLayout.LayoutParams(0,-2,1));
        TextView avatar = text("●",13,green,true); avatar.setGravity(Gravity.CENTER); avatar.setBackground(shape(card,22)); header.addView(avatar,lp(42,42));
        root.addView(header);

        LinearLayout hero = column(); hero.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(69,59,151),Color.rgb(102,78,225)})); hero.setPadding(20,18,20,18);
        hero.addView(text("היום שלך, בשליטה ✨",22,Color.WHITE,true));
        TextView sub = text("משימות, כסף, מסמכים ותכנון — בלי להתפזר.",13,Color.rgb(230,227,255),false);
        LinearLayout.LayoutParams subp=lp(-1,-2); subp.topMargin=5; hero.addView(sub,subp);
        LinearLayout.LayoutParams hp=lp(-1,-2); hp.setMargins(16,3,16,12); root.addView(hero,hp);

        pageTitle=text("לוח הבקרה",19,ink,true); pad(pageTitle,20,4,20,9); root.addView(pageTitle);
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true);
        content=column(); pad(content,16,0,16,14); scroll.addView(content);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));

        nav=row(); nav.setBackgroundColor(card); pad(nav,4,7,4,7);
        String[] tabs={"בית","משימות","כסף","מסמכים","תכנון","עוד"};
        String[] icons={"⌂","✓","＄","▣","➤","•••"};
        for(int i=0;i<tabs.length;i++){
            final String tab=tabs[i]; TextView item=text(icons[i]+"\n"+tab,10,muted,true); item.setGravity(Gravity.CENTER); item.setTag(tab);
            nav.addView(item,new LinearLayout.LayoutParams(0,53,1)); item.setOnClickListener(v->showTab(tab));
        }
        root.addView(nav,lp(-1,-2)); setContentView(root);
        getWindow().setStatusBarColor(bg); getWindow().setNavigationBarColor(bg);
        getWindow().getDecorView().setSystemUiVisibility(dark?0:View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
    }
    private void showTab(String tab) {
        currentTab=tab; content.removeAllViews();
        String title=tab.equals("בית")?"לוח הבקרה":tab.equals("משימות")?"משימות ותכנון":tab.equals("כסף")?"התקציב שלי":tab.equals("מסמכים")?"מסמכים וקבצים":tab.equals("תכנון")?"תכנון מסלול": "כלים אישיים";
        pageTitle.setText(title);
        for(int i=0;i<nav.getChildCount();i++){
            TextView item=(TextView)nav.getChildAt(i); boolean selected=item.getTag().equals(tab);
            item.setTextColor(selected?purple:muted); item.setBackground(selected?shape(dark?Color.rgb(48,42,83):Color.rgb(240,237,255),15):shape(card,15));
        }
        switch(tab) {
            case "בית": homeView(); break;
            case "משימות": tasksView(); break;
            case "כסף": moneyView(); break;
            case "מסמכים": documentsView(); break;
            case "תכנון": travelView(); break;
            default: moreView(); break;
        }
    }
    private void addCard(LinearLayout target, View child) {
        LinearLayout box=column(); box.setBackground(shape(card,21)); pad(box,15,13,15,13); box.addView(child);
        LinearLayout.LayoutParams p=lp(-1,-2); p.bottomMargin=10; target.addView(box,p);
    }
    private void addSectionTitle(String s) {
        TextView t=text(s,16,ink,true); pad(t,3,9,3,8); content.addView(t);
    }
    private void addAction(String label, Runnable action) {
        Button b=button(label,purple,Color.WHITE); LinearLayout.LayoutParams p=lp(-1,47); p.bottomMargin=10; content.addView(b,p); b.setOnClickListener(v->action.run());
    }
    private void homeView() {
        LinearLayout stats=row();
        addStat(stats,"משימות",String.valueOf(tasks.size()),purple);
        addStat(stats,"הוצאות","₪"+String.format(Locale.US,"%.0f",totalSpent),green);
        addStat(stats,"מסמכים",String.valueOf(documents.size()),orange);
        content.addView(stats);
        addSectionTitle("קיצורי דרך");
        addAction("＋  הוספת משימה",()->addTextDialog("משימה חדשה","מה צריך לעשות?",tasks,"tasks",()->showTab("בית")));
        addAction("＋  רישום הוצאה",()->addExpenseDialog());
        addAction("▣  הוספת מסמך או PDF",()->pickDocument());
        addAction("➤  תכנון מסלול נסיעה",()->showTab("תכנון"));
        addSectionTitle("מה קורה היום");
        if(tasks.isEmpty()&&events.isEmpty()) {
            LinearLayout empty=column(); empty.addView(text("היומן שלך מתחיל כאן",16,ink,true)); empty.addView(text("הוסף משימה, אירוע או מטרה כדי לראות אותם כאן.",13,muted,false)); addCard(content,empty);
        } else {
            for(String item:tasks.subList(0,Math.min(3,tasks.size()))) { TextView t=text("☐  "+item,14,ink,false); addCard(content,t); }
            for(String item:events.subList(0,Math.min(2,events.size()))) { TextView t=text("◷  "+item,14,ink,false); addCard(content,t); }
        }
        TextView footer=text("נוצר ע\"י מטען נייד",12,muted,true); footer.setGravity(Gravity.CENTER); pad(footer,0,12,0,12); content.addView(footer);
    }
    private void addStat(LinearLayout parent,String label,String value,int color) {
        LinearLayout box=column(); box.setBackground(shape(card,18)); pad(box,10,12,10,12);
        box.addView(text(value,19,color,true)); box.addView(text(label,11,muted,false));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1); p.setMargins(3,0,3,0); parent.addView(box,p);
    }
    private void tasksView() {
        addAction("＋  משימה חדשה",()->addTextDialog("משימה חדשה","שם המשימה",tasks,"tasks",()->showTab("משימות")));
        addAction("＋  אירוע ליומן",()->addTextDialog("אירוע חדש","שם האירוע / תאריך ושעה",events,"events",()->showTab("משימות")));
        addAction("＋  מטרה אישית",()->addTextDialog("מטרה חדשה","מה תרצה להשיג?",goals,"goals",()->showTab("משימות")));
        addSectionTitle("המשימות שלי");
        if(tasks.isEmpty()) addCard(content,text("אין משימות כרגע. הוסף משימה חדשה למעלה.",14,muted,false));
        for(int i=0;i<tasks.size();i++) {
            final int index=i; LinearLayout r=row(); LinearLayout d=column(); d.addView(text("☐  "+tasks.get(i),15,ink,true)); d.addView(text("אפשר לסמן כהושלמה ולהסיר מהרשימה",11,muted,false));
            r.addView(d,new LinearLayout.LayoutParams(0,-2,1)); Button done=button("בוצע",green,Color.WHITE); r.addView(done,lp(78,42));
            done.setOnClickListener(v->{tasks.remove(index);saveList("tasks",tasks);showTab("משימות");}); addCard(content,r);
        }
        addSectionTitle("יומן");
        if(events.isEmpty()) addCard(content,text("אין אירועים שמורים.",13,muted,false));
        for(String e:events) addCard(content,text("◷  "+e,14,ink,false));
        addSectionTitle("מטרות");
        for(String g:goals) addCard(content,text("◎  "+g,14,ink,false));
    }
    private void moneyView() {
        LinearLayout summary=column(); summary.addView(text("סה״כ הוצאות שנרשמו",13,muted,false));
        summary.addView(text("₪"+String.format(Locale.US,"%.2f",totalSpent),30,ink,true));
        summary.addView(text("רישום מקומי פשוט — הנתונים נשמרים במכשיר.",12,muted,false)); addCard(content,summary);
        addAction("＋  הוספת הוצאה",()->addExpenseDialog());
        addSectionTitle("הוצאות אחרונות");
        if(expenses.isEmpty()) addCard(content,text("עדיין לא נרשמו הוצאות.",14,muted,false));
        for(int i=expenses.size()-1;i>=0;i--) addCard(content,text("↗  "+expenses.get(i),14,ink,false));
        Button reset=button("איפוס הוצאות",dark?Color.rgb(48,51,70):Color.rgb(236,238,249),muted); content.addView(reset,lp(-1,44));
        reset.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("איפוס הוצאות?").setMessage("פעולה זו תמחק את ההוצאות שנשמרו.").setNegativeButton("ביטול",null).setPositiveButton("איפוס",(d,w)->{expenses.clear();totalSpent=0;saveList("expenses",expenses);prefs.edit().putFloat("spent",0f).apply();showTab("כסף");}).show());
    }
    private void addExpenseDialog() {
        LinearLayout form=column(); EditText title=new EditText(this); title.setHint("על מה שילמת?"); EditText amount=new EditText(this); amount.setHint("סכום בש״ח"); amount.setInputType(8194); form.addView(title); form.addView(amount); pad(form,8,5,8,5);
        new AlertDialog.Builder(this).setTitle("הוצאה חדשה").setView(form).setNegativeButton("ביטול",null).setPositiveButton("שמירה",(d,w)->{
            String n=title.getText().toString().trim(); try { double a=Double.parseDouble(amount.getText().toString().trim().replace(',','.')); if(n.isEmpty()||a<0) throw new Exception(); totalSpent+=a; expenses.add(n+" — ₪"+String.format(Locale.US,"%.2f",a)); saveList("expenses",expenses); prefs.edit().putFloat("spent",(float)totalSpent).apply(); showTab("כסף"); } catch(Exception ex) { Toast.makeText(this,"נא להזין תיאור וסכום תקין",Toast.LENGTH_SHORT).show(); }
        }).show();
    }
    private void documentsView() {
        LinearLayout intro=column(); intro.addView(text("כל המסמכים במקום מסודר",16,ink,true)); intro.addView(text("בחר תמונה או PDF מהמכשיר ושמור אותו ברשימת המסמכים שלך.",13,muted,false)); addCard(content,intro);
        addAction("＋  בחירת מסמך / תמונה / PDF",()->pickDocument());
        addSectionTitle("הקבצים ששמרת");
        if(documents.isEmpty()) addCard(content,text("עדיין לא נוספו מסמכים.",14,muted,false));
        for(String doc:documents) {
            String[] parts=doc.split("§",2); String displayName=parts[0];
            LinearLayout r=row(); TextView label=text("▣  "+displayName,13,ink,false); r.addView(label,new LinearLayout.LayoutParams(0,-2,1));
            Button open=button("פתיחה",purple,Color.WHITE); r.addView(open,lp(75,40));
            open.setOnClickListener(v->{if(parts.length>1){try{Intent openIntent=new Intent(Intent.ACTION_VIEW,Uri.parse(parts[1]));openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivity(openIntent);}catch(Exception e){Toast.makeText(this,"אין אפליקציה מתאימה לפתיחת הקובץ",Toast.LENGTH_SHORT).show();}}else{Toast.makeText(this,"בחר את הקובץ שוב כדי לפתוח אותו",Toast.LENGTH_SHORT).show();}});
            addCard(content,r);
        }
        TextView note=text("הערה: הגרסה הזו מארגנת קבצים שבוחרים. OCR אוטומטי וסריקה מקצועית דורשים תוספת נפרדת.",12,muted,false); pad(note,5,5,5,10); content.addView(note);
    }
    private void pickDocument() {
        Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT); i.setType("*/*"); i.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"application/pdf","image/*"}); i.addCategory(Intent.CATEGORY_OPENABLE);
        try { startActivityForResult(i,PICK_DOCUMENT); } catch(Exception e) { Toast.makeText(this,"לא ניתן לפתוח בורר קבצים",Toast.LENGTH_SHORT).show(); }
    }
    private void travelView() {
        LinearLayout form=column();
        form.addView(text("מאיפה יוצאים?",13,muted,true)); EditText origin=new EditText(this); origin.setHint("כתובת או מקום מוצא"); origin.setSingleLine(true); form.addView(origin);
        form.addView(text("לאן נוסעים?",13,muted,true)); EditText destination=new EditText(this); destination.setHint("יעד"); destination.setSingleLine(true); form.addView(destination);
        addCard(content,form);
        Button route=button("פתיחת מסלול במפות",purple,Color.WHITE); content.addView(route,lp(-1,49));
        route.setOnClickListener(v->{String to=destination.getText().toString().trim(),from=origin.getText().toString().trim(); if(to.isEmpty()){destination.setError("יש להזין יעד");return;}
            String uri="https://www.google.com/maps/dir/?api=1&destination="+Uri.encode(to)+(from.isEmpty()?"":"&origin="+Uri.encode(from));
            try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(uri)));}catch(Exception e){Toast.makeText(this,"לא נמצאה אפליקציית מפות זמינה",Toast.LENGTH_SHORT).show();}
        });
        addSectionTitle("טיפ");
        addCard(content,text("אפשר להזין כתובת, עסק או נקודת עניין. אפליקציית המפות תחשב את המסלול ואת זמן הנסיעה.",14,muted,false));
        addSectionTitle("יעדים אחרונים");
        if(events.isEmpty()) addCard(content,text("אפשר לשמור תכניות ויעדים בלשונית משימות.",13,muted,false));
    }
    private void moreView() {
        addAction("＋  פתק חדש",()->addTextDialog("פתק חדש","כתוב פתק קצר",notes,"notes",()->showTab("עוד")));
        addSectionTitle("הפתקים שלי");
        if(notes.isEmpty()) addCard(content,text("אין פתקים עדיין.",14,muted,false));
        for(String n:notes) addCard(content,text("✎  "+n,14,ink,false));
        addSectionTitle("הגדרות");
        LinearLayout theme=row(); theme.addView(text("🌗  מצב כהה",15,ink,true),new LinearLayout.LayoutParams(0,44,1));
        Button toggle=button(dark?"כיבוי":"הפעלה",purple,Color.WHITE); theme.addView(toggle,lp(95,40)); addCard(content,theme);
        toggle.setOnClickListener(v->{dark=!dark;prefs.edit().putBoolean("dark",dark).apply();updatePalette();buildShell();showTab("עוד");});
        LinearLayout about=column(); about.addView(text("LifeHub",18,ink,true)); about.addView(text("ניהול אישי, תקציב, מסמכים ותכנון מסלולים.",13,muted,false)); about.addView(text("נוצר ע\"י מטען נייד",12,muted,true)); addCard(content,about);
    }
    private void addTextDialog(String titleText,String hint,ArrayList<String> list,String key,Runnable refresh) {
        EditText input=new EditText(this); input.setHint(hint); input.setMinLines(1); input.setMaxLines(3); pad(input,10,6,10,6);
        new AlertDialog.Builder(this).setTitle(titleText).setView(input).setNegativeButton("ביטול",null).setPositiveButton("שמירה",(d,w)->{
            String value=input.getText().toString().trim(); if(!value.isEmpty()){list.add(value);saveList(key,list);refresh.run();}
        }).show();
    }
    private void saveList(String key,ArrayList<String> list) {
        StringBuilder b=new StringBuilder(); for(String item:list) b.append(item.replace("\n"," ")).append("\n");
        prefs.edit().putString(key,b.toString()).apply();
    }
    private void loadList(String key,ArrayList<String> list) {
        String saved=prefs==null?"":prefs.getString(key,""); if(saved==null||saved.isEmpty()) return;
        for(String line:saved.split("\n")) if(!line.trim().isEmpty()) list.add(line);
    }
    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==PICK_DOCUMENT&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null) {
            Uri uri=data.getData();
            try { getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION); } catch(Exception ignored) {}
            String label=uri.getLastPathSegment(); if(label==null||label.trim().isEmpty()) label="מסמך חדש";
            documents.add(label+"§"+uri.toString()); saveList("documents",documents); Toast.makeText(this,"המסמך נוסף לרשימה",Toast.LENGTH_SHORT).show(); showTab("מסמכים");
        }
    }
}
