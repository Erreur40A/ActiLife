package com.cgp.actilife;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import java.util.Calendar;

import java.util.Calendar;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RappelSommeilActivity extends AppCompatActivity {
    private DatabaseOpenHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rappel_sommeil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DatabaseOpenHelper(RappelSommeilActivity.this);

        // fermer lorsqu'on clique sur le bouton retour
        View header = findViewById(R.id.header);
        ImageView btnRetour = header.findViewById(R.id.btnRetour);
        btnRetour.setOnClickListener(v -> {
            db.close();
            finish();
        });

        TextView motivation = findViewById(R.id.motivation_sommeil);
        motivation.setText(db.getMotivation(ConstDB.MOTIVATIONS_TYPE_SOMMEIL));

        GridLayout bedGrid = findViewById(R.id.bedGrid);
        GridLayout wakeGrid = findViewById(R.id.wakeGrid);

        String[] days = {"L", "Ma", "Me", "J", "V", "S", "D"};
        String[] bedtime = {"1", "1", "1", "1", "1", "1", "1"};
        String[] waketime = {"1", "1", "1", "1", "1", "1", "1"};

        addRowToGrid(this, bedGrid, days, false, null);
        addRowToGrid(this, bedGrid, bedtime, true, LesNotifications.RAPPEL_HEURE_COUCHER);

        addRowToGrid(this, wakeGrid, days, false, null);
        addRowToGrid(this, wakeGrid, waketime, true, LesNotifications.RAPPEL_HEURE_REVEIL);
    }

    private void addRowToGrid(Context context, GridLayout grid, String[] values, boolean clickable, LesNotifications typeNotif) {
        int columns = values.length;
        int row = grid.getChildCount() / columns;

        for (int i = 0; i < columns; i++) {
            TextView tv = new TextView(context);
            tv.setText(values[i]);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(ContextCompat.getColor(this, R.color.blueColor));
            tv.setTypeface(null, Typeface.BOLD);
            tv.setPadding(2, 24, 2, 24);
            int widthInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, context.getResources().getDisplayMetrics());
            tv.setWidth(widthInDp);

            Drawable bg;

            if (row == 0 && i == 0) {
                bg = ContextCompat.getDrawable(context, R.drawable.cell_corner_top_left);
            } else if (row == 0 && i == values.length - 1) {
                bg = ContextCompat.getDrawable(context, R.drawable.cell_corner_top_right);
            } else if (row == 1 && i == 0) {
                bg = ContextCompat.getDrawable(context, R.drawable.cell_corner_bottom_left);
            } else if (row == 1 && i == values.length - 1) {
                bg = ContextCompat.getDrawable(context, R.drawable.cell_corner_bottom_right);
            } else {
                bg = ContextCompat.getDrawable(context, R.drawable.cell_middle);
            }

            tv.setBackground(bg);

            // Positionnement dans la grille
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(i);
            params.rowSpec = GridLayout.spec(row);
            tv.setLayoutParams(params);

            // Si cliquable, ajouter l'action de TimePicker
            if (clickable) {
                final int index = i;
                tv.setOnClickListener(v -> {
                    String[] parts = values[index].split("h");
                    int hour = Integer.parseInt(parts[0]);
                    int minute = (parts.length > 1) ? Integer.parseInt(parts[1]) : 0;

                    TimePickerDialog tpd = new TimePickerDialog(context,
                            (view, h, m) -> setListenerRappelCoucher(h, m, index, tv, typeNotif)
                            , hour, minute, true);
                    tpd.show();
                });
            }

            grid.addView(tv);
        }
    }

    private void setListenerRappelCoucher(int heure, int minute, int jourChosit, TextView tv, LesNotifications typeNotif){
        String time = heure + "h" + (minute < 10 ? "0" : "") + minute;
        tv.setText(time);
        Calendar date = Calendar.getInstance();

        int jourCourantDeLaSemaine = date.get(Calendar.DAY_OF_WEEK);
        int jourCible = jourChosit + 2; //Calendar.MONDAY vaut 2, puis mardi vaut 3... donc on ajoute 2
        int jourRestant = jourCible + 7 - jourCourantDeLaSemaine;
        jourRestant = jourRestant % 7;

        if(jourRestant == 0){
            jourRestant = 7;
        }

        date.add(Calendar.DAY_OF_MONTH, jourRestant);
        int jour = date.get(Calendar.DAY_OF_MONTH);
        int id = (date.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        String h = heure + ":" + minute;

        Map<String, Object> fields = new HashMap<>();
        if(typeNotif == LesNotifications.RAPPEL_HEURE_COUCHER)
            fields.put(ConstDB.RAPPELS_SOMMEIL_HEURE_COUCHER, h);

        if (typeNotif == LesNotifications.RAPPEL_HEURE_REVEIL)
            fields.put(ConstDB.RAPPELS_SOMMEIL_HEURE_REVEIL, h);

        db.updateTableWithId(ConstDB.RAPPELS_SOMMEIL, fields, id);

        AlarmScheduler.setAlarm(RappelSommeilActivity.this, jour, heure, minute, typeNotif);
    }
}