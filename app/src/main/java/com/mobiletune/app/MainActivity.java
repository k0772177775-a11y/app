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
import android.text.Editable;
import android.text.TextWatcher;
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
    private static final int PICK_AUDIO = 41;
    private EditText numberField, searchField;
    private TextView trackLabel, previewStatus, pageTitle;
    private LinearLayout content, nav;
    private Uri audioUri;
    private MediaPlayer player;
    private boolean playing = false, dark = false;
    private String currentTab = "חיוג";
    private final ArrayList<String[]> contacts = new ArrayList<>();
    private final ArrayList<String[]> recents = new ArrayList<>();
    private int ink, muted, bg, card, purple, green;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        contacts.add(new String[]{"אמא", "050-123-4567", "א"});
        contacts.add(new String[]{"אבא", "052-234-5678", "א"});
        contacts.add(new String[]{"דניאל כהן", "054-345-6789", "ד"});
        contacts.add(new String[]{"נועם לוי", "058-456-7890", "נ"});
        contacts.add(new String[]{"שירות לקוחות", "*123", "ש"});
        recents.add(new String[]{"דניאל כהן", "054-345-6789", "היום · 14:32"});
        recents.add(new String[]{"אמא", "050-123-4567", "אתמול · 20:15"});
        recents.add(new String[]{"נועם לוי", "058-456-7890", "אתמול · 18:04"});
        updatePalette();
        buildShell();
        showTab("חיוג");
    }

    private void updatePalette() {
        bg = dark ? Color.rgb(16, 18, 30) : Color.rgb(246, 247, 255);
        card = dark ? Color.rgb(29, 32, 49) : Color.WHITE;
        ink = dark ? Color.rgb(244, 245, 255) : Color.rgb(27, 34, 57);
        muted = dark ? Color.rgb(167, 174, 200) : Color.rgb(112, 122, 148);
        purple = Color.rgb(112, 87, 255);
        green = Color.rgb(35, 178, 130);
    }
    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); return d;
    }
    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setGravity(Gravity.CENTER_VERTICAL); return t;
    }
    private LinearLayout column() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l; }
    private LinearLayout row() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); l.setGravity(Gravity.CENTER_VERTICAL); return l; }
    private void pad(View v, int l, int t, int r, int b) { v.setPadding(l,t,r,b); }
    private LinearLayout.LayoutParams lp(int w, int h) { return new LinearLayout.LayoutParams(w,h); }
    private Button button(String label, int color, int fg) {
        Button b = new Button(this); b.setText(label); b.setTextSize(15); b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setAllCaps(false); b.setTextColor(fg); b.setBackground(shape(color, 24)); b.setPadding(14,10,14,10); return b;
    }
    private void buildShell() {
        LinearLayout root = column(); root.setBackgroundColor(bg);
        LinearLayout header = row(); pad(header,20,14,20,12);
        TextView logo = text("✦", 24, Color.WHITE, true); logo.setGravity(Gravity.CENTER); logo.setBackground(shape(purple,18));
        header.addView(logo,lp(48,48));
        LinearLayout brand = column(); pad(brand,12,0,0,0);
        brand.addView(text("MobileTune",22,ink,true));
        brand.addView(text("הטלפון שלך. בדרך שלך.",12,muted,false));
        header.addView(brand,new LinearLayout.LayoutParams(0,-2,1));
        TextView status = text("●",13,green,true); status.setGravity(Gravity.CENTER); status.setBackground(shape(card,18)); header.addView(status,lp(40,40));
        root.addView(header);

        LinearLayout hero = column(); hero.setBackground(shape(Color.rgb(54,48,108),26)); pad(hero,20,18,20,18);
        hero.addView(text("יותר מסתם חיוג ✨",23,Color.WHITE,true));
        TextView sub = text("אנשי קשר, מועדפים, היסטוריה והמוזיקה שלך — במקום אחד.",13,Color.rgb(226,224,255),false);
        LinearLayout.LayoutParams slp=lp(-1,-2); slp.topMargin=5; hero.addView(sub,slp);
        LinearLayout.LayoutParams hlp=lp(-1,-2); hlp.setMargins(16,4,16,14); root.addView(hero,hlp);

        pageTitle = text("חיוג מהיר",19,ink,true); pad(pageTitle,20,5,20,10); root.addView(pageTitle);
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true);
        content = column(); pad(content,16,0,16,12); scroll.addView(content);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));

        nav = row(); nav.setBackgroundColor(card); pad(nav,6,8,6,8);
        String[] tabs={"חיוג","אנשי קשר","אחרונים","מועדפים","הגדרות"};
        for(String tab:tabs){
            TextView item=text(navLabel(tab),11,muted,true); item.setGravity(Gravity.CENTER); item.setTag(tab);
            nav.addView(item,new LinearLayout.LayoutParams(0,54,1));
            item.setOnClickListener(v->showTab((String)v.getTag()));
        }
        root.addView(nav,lp(-1,-2));
        setContentView(root);
        getWindow().setStatusBarColor(bg); getWindow().setNavigationBarColor(bg);
        getWindow().getDecorView().setSystemUiVisibility((dark?0:View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR));
    }
    private String navLabel(String t) {
        if(t.equals("חיוג")) return "⌕\nחיוג";
        if(t.equals("אנשי קשר")) return "♙\nאנשי קשר";
        if(t.equals("אחרונים")) return "◷\nאחרונים";
        if(t.equals("מועדפים")) return "☆\nמועדפים";
        return "⚙\nהגדרות";
    }
    private void showTab(String tab) {
        currentTab=tab; content.removeAllViews();
        pageTitle.setText(tab.equals("חיוג")?"חיוג מהיר":tab.equals("אנשי קשר")?"אנשי הקשר שלך":tab.equals("אחרונים")?"שיחות אחרונות":tab.equals("מועדפים")?"המועדפים שלך":"התאמה אישית");
        for(int i=0;i<nav.getChildCount();i++){
            TextView item=(TextView)nav.getChildAt(i);
            item.setTextColor(item.getTag().equals(tab)?purple:muted);
            item.setBackground(item.getTag().equals(tab)?shape(dark?Color.rgb(48,42,83):Color.rgb(240,237,255),18):shape(card,18));
        }
        if(tab.equals("חיוג")) dialView();
        else if(tab.equals("אנשי קשר")) contactView(false);
        else if(tab.equals("אחרונים")) recentView();
        else if(tab.equals("מועדפים")) contactView(true);
        else settingsView();
    }
    private void addCard(LinearLayout target, View child) {
        LinearLayout box=column(); box.setBackground(shape(card,22)); pad(box,16,14,16,14); box.addView(child);
        LinearLayout.LayoutParams p=lp(-1,-2); p.bottomMargin=12; target.addView(box,p);
    }
    private void dialView() {
        LinearLayout dial=column(); dial.setGravity(Gravity.CENTER_HORIZONTAL);
        numberField=new EditText(this); numberField.setSingleLine(true); numberField.setTextSize(29); numberField.setGravity(Gravity.CENTER);
        numberField.setHint("הקלד מספר"); numberField.setInputType(3); numberField.setTextColor(ink); numberField.setHintTextColor(muted);
        numberField.setBackground(shape(card,20)); pad(numberField,12,12,12,12); dial.addView(numberField,lp(-1,68));
        LinearLayout.LayoutParams keyLp=lp(-1,-2); keyLp.topMargin=12;
        String[][] keys={{"1","2","3"},{"4","5","6"},{"7","8","9"},{"*","0","#"}};
        for(String[] line:keys){
            LinearLayout r=row();
            for(String key:line){
                Button b=button(key,card,ink); b.setTextSize(22);
                LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,55,1); p.setMargins(4,4,4,4); r.addView(b,p);
                b.setOnClickListener(v->numberField.append(key));
            }
            dial.addView(r);
        }
        Button call=button("●   התקשר",green,Color.WHITE); LinearLayout.LayoutParams cp=lp(-1,54); cp.topMargin=10; dial.addView(call,cp);
        call.setOnClickListener(v->openDialer());
        Button back=button("⌫  מחיקת ספרה",dark?Color.rgb(48,51,70):Color.rgb(236,238,249),muted);
        LinearLayout.LayoutParams bp=lp(-1,45); bp.topMargin=6; dial.addView(back,bp);
        back.setOnClickListener(v->{if(numberField.length()>0)numberField.getText().delete(numberField.length()-1,numberField.length());});
        addCard(content,dial);

        LinearLayout music=column(); music.addView(text("🎵  צליל משלך",17,ink,true));
        music.addView(text("בחר קובץ שמע והאזן לו לפני החיוג",13,muted,false));
        trackLabel=text(audioUri==null?"לא נבחר קובץ":"✓ קובץ מוזיקה נבחר",13,ink,true); pad(trackLabel,0,10,0,8); music.addView(trackLabel);
        Button choose=button("בחירת קובץ שמע",purple,Color.WHITE); LinearLayout.LayoutParams cp2=lp(-1,46); cp2.topMargin=5; music.addView(choose,cp2);
        choose.setOnClickListener(v->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("audio/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,PICK_AUDIO);});
        Button preview=button(playing?"⏸  עצירה":"▶  השמעה מקדימה",dark?Color.rgb(48,42,83):Color.rgb(240,237,255),purple);
        LinearLayout.LayoutParams pp=lp(-1,44); pp.topMargin=7; music.addView(preview,pp);
        preview.setOnClickListener(v->{togglePreview(); showTab("חיוג");});
        addCard(content,music);
        TextView info=text("ℹ️  המוזיקה מתנגנת במכשיר בלבד. חיוג רגיל ב-Android לא מאפשר לאפליקציה להחליף את קול המיקרופון בקובץ שמע.",12,muted,false);
        pad(info,8,2,8,10); content.addView(info);
    }
    private void contactView(boolean favorites) {
        EditText search=new EditText(this); search.setSingleLine(true); search.setHint("⌕  חיפוש שם או מספר"); search.setTextColor(ink); search.setHintTextColor(muted);
        search.setBackground(shape(card,18)); pad(search,14,10,14,10); content.addView(search,lp(-1,52));
        LinearLayout list=column(); LinearLayout.LayoutParams listLp=lp(-1,-2); listLp.topMargin=12; content.addView(list,listLp);
        Runnable refresh=()->{
            list.removeAllViews(); String q=search.getText().toString().toLowerCase(Locale.ROOT);
            for(String[] c:contacts){
                if(favorites && !(c[0].equals("אמא")||c[0].equals("אבא"))) continue;
                if(!c[0].toLowerCase(Locale.ROOT).contains(q)&&!c[1].contains(q)) continue;
                LinearLayout item=row(); TextView avatar=text(c[2],18,Color.WHITE,true); avatar.setGravity(Gravity.CENTER); avatar.setBackground(shape(purple,28)); item.addView(avatar,lp(48,48));
                LinearLayout details=column(); pad(details,12,0,0,0); details.addView(text(c[0],16,ink,true)); details.addView(text(c[1],13,muted,false));
                item.addView(details,new LinearLayout.LayoutParams(0,-2,1));
                Button call=button("☎",green,Color.WHITE); item.addView(call,lp(52,44)); call.setOnClickListener(v->dialNumber(c[1],c[0]));
                addCard(list,item);
            }
        };
        search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int count,int after){} public void onTextChanged(CharSequence s,int st,int before,int count){refresh.run();} public void afterTextChanged(Editable e){}});
        refresh.run();
        Button add=button("＋  הוספת איש קשר",purple,Color.WHITE); LinearLayout.LayoutParams ap=lp(-1,48); ap.topMargin=8; content.addView(add,ap);
        add.setOnClickListener(v->{
            EditText name=new EditText(this);name.setHint("שם"); EditText phone=new EditText(this);phone.setHint("מספר טלפון");phone.setInputType(3);
            LinearLayout form=column();form.addView(name);form.addView(phone);pad(form,8,4,8,4);
            new AlertDialog.Builder(this).setTitle("איש קשר חדש").setView(form).setNegativeButton("ביטול",null).setPositiveButton("שמירה",(d,w)->{
                String n=name.getText().toString().trim(),p=phone.getText().toString().trim();
                if(!n.isEmpty()&&!p.isEmpty()){contacts.add(new String[]{n,p,n.substring(0,1)});showTab(currentTab);}
            }).show();
        });
    }
    private void recentView() {
        for(String[] c:recents){
            LinearLayout item=row(); TextView avatar=text("↗",19,purple,true);avatar.setGravity(Gravity.CENTER);avatar.setBackground(shape(dark?Color.rgb(48,42,83):Color.rgb(240,237,255),25));item.addView(avatar,lp(48,48));
            LinearLayout details=column();pad(details,12,0,0,0);details.addView(text(c[0],16,ink,true));details.addView(text(c[2]+"  ·  "+c[1],12,muted,false));
            item.addView(details,new LinearLayout.LayoutParams(0,-2,1));Button call=button("☎",green,Color.WHITE);item.addView(call,lp(52,44));call.setOnClickListener(v->dialNumber(c[1],c[0]));addCard(content,item);
        }
        Button clear=button("ניקוי היסטוריה",dark?Color.rgb(48,51,70):Color.rgb(236,238,249),muted);content.addView(clear,lp(-1,46));
        clear.setOnClickListener(v->{recents.clear();showTab("אחרונים");});
    }
    private void settingsView() {
        LinearLayout theme=row();theme.addView(text("🌗  מצב כהה",16,ink,true),new LinearLayout.LayoutParams(0,48,1));
        Button toggle=button(dark?"הפעלה":"כיבוי",purple,Color.WHITE);theme.addView(toggle,lp(94,42));addCard(content,theme);
        toggle.setOnClickListener(v->{dark=!dark;updatePalette();buildShell();showTab("הגדרות");});
        LinearLayout about=column();about.addView(text("MobileTune",19,ink,true));about.addView(text("חייגן נקי ונוח עם אנשי קשר, מועדפים, היסטוריית שיחות, חיפוש ומוזיקה.",13,muted,false));
        about.addView(text("נוצר ע״י מטען נייד ממתמחים טופ 2026",12,muted,true));addCard(content,about);
        Button go=button("פתיחת החייגן של המכשיר",green,Color.WHITE);content.addView(go,lp(-1,48));go.setOnClickListener(v->startActivity(new Intent(Intent.ACTION_DIAL)));
    }
    private void dialNumber(String number,String name) {
        new AlertDialog.Builder(this).setTitle(name).setMessage(number).setNegativeButton("ביטול",null).setPositiveButton("פתיחת חייגן",(d,w)->{
            recents.add(0,new String[]{name,number,"עכשיו"});Intent i=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+Uri.encode(number)));startActivity(i);
        }).show();
    }
    private void openDialer() {
        String number=numberField.getText().toString().trim();
        if(number.isEmpty()){numberField.setError("נא להזין מספר טלפון");return;}
        new AlertDialog.Builder(this).setTitle("נוצר ע״י מטען נייד ממתמחים טופ 2026")
            .setMessage("החייגן הרגיל ייפתח עכשיו. מוזיקה מהאפליקציה לא תועבר לצד השני.")
            .setNegativeButton("ביטול",null).setPositiveButton("המשך",(d,w)->{
                recents.add(0,new String[]{"מספר שחויג",number,"עכשיו"});
                startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+Uri.encode(number))));
            }).show();
    }
    private void togglePreview() {
        if(audioUri==null){Toast.makeText(this,"קודם בחר קובץ שמע",Toast.LENGTH_SHORT).show();return;}
        try{
            if(playing&&player!=null){player.pause();playing=false;return;}
            if(player!=null){player.release();player=null;}
            player=new MediaPlayer();player.setDataSource(this,audioUri);player.setOnCompletionListener(mp->playing=false);player.prepare();player.start();playing=true;
        }catch(Exception e){Toast.makeText(this,"לא ניתן לנגן את הקובץ",Toast.LENGTH_LONG).show();}
    }
    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==PICK_AUDIO&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            audioUri=data.getData();try{getContentResolver().takePersistableUriPermission(audioUri,Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception ignored){}
            if(trackLabel!=null)trackLabel.setText("✓ קובץ שמע נבחר");
        }
    }
    @Override protected void onDestroy(){if(player!=null){player.release();player=null;}super.onDestroy();}
}
