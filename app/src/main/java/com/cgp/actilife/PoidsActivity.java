package com.cgp.actilife;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class PoidsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_poids);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LineChart graph = findViewById(R.id.graph);
        graph.getAxisRight().setEnabled(false);
        graph.setDescription(null);

        // Les points du graphe
        ArrayList<Entry> lesPoints = new ArrayList<>(7);

        /*----Date et poids à récuperer avec une requête SQL----*/
        ArrayList<String> lesDatesString = new ArrayList<>(7);
        lesDatesString.add("12/04/2025");
        lesDatesString.add("16/01/2025");
        lesDatesString.add("18/04/2025");
        lesDatesString.add("20/05/2025");
        lesDatesString.add("21/04/2025");
        lesDatesString.add("22/07/2025");
        lesDatesString.add("23/10/2025");

        /*-Trier les dates dans l'ordre croissant-*/
        ArrayList<Float> lesDatesNbJours = new ArrayList<>(7);

        for (int i = 0; i < 7; i++) {
            lesDatesNbJours.add(dateToNbJours(lesDatesString.get(i)));
        }

        Collections.sort(lesDatesNbJours);
        /*----------------------------------------*/

        for (int i = 0; i < 7; i++) {
            lesPoints.add(new Entry(i, i*(i+10)));
        }

        LineDataSet courbe = getCourbe(lesPoints);

        LineData lineData = new LineData(courbe); //l'ensemble des courbe à afficher (ici il n'y en a qu'une)
        graph.setData(lineData);

        XAxis abscisse = graph.getXAxis();
        abscisse.setPosition(XAxis.XAxisPosition.BOTTOM);
        abscisse.setDrawGridLines(false);

        abscisse.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float indice) {
                int i = (int) indice;
                Date d = new Date(lesDatesNbJours.get(i).longValue() * 86400000L);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.FRANCE);
                return sdf.format(d);
            }
        });

        YAxis ordonne = graph.getAxisLeft();
        ordonne.setDrawGridLines(false);

        LimitLine objectif = getObjectifLine(80f);

        ordonne.addLimitLine(objectif);
        ordonne.setAxisMaximum(ordonne.getAxisMaximum() + 10);
        ordonne.setAxisMinimum(0);

        graph.invalidate();
    }

    public float dateToNbJours(String date){
        float nb_jours;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date d = sdf.parse(date);
            nb_jours = (float) d.getTime() / 86400000L; //1 jour = 86 400 000 millisecondes
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return nb_jours;
    }

    public LineDataSet getCourbe(ArrayList<Entry> lesPoids){
        LineDataSet courbe = new LineDataSet(lesPoids, "L'évolution de votre poids");

        courbe.setDrawCircleHole(false); //les points sont des cercles plein
        courbe.setColor(R.color.primaryColor); //couleur de la courbe
        courbe.setCircleColor(R.color.primaryColor); //couleur des points de la courbe
        courbe.setLineWidth(3f); //largeur de la courbe
        courbe.setCircleRadius(7f); //largeur du cercle
        courbe.setValueTextSize(10f);

        return  courbe;
    }

    public LimitLine getObjectifLine(float valeur_objectif){
        LimitLine objectifLine = new LimitLine(valeur_objectif, "objectif 80kg");
        objectifLine.setLineWidth(3f);
        objectifLine.setLineColor(Color.GREEN);
        objectifLine.enableDashedLine(30f, 20f, 0f); // Ligne en pointillés
        objectifLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        objectifLine.setTextSize(16f);
        objectifLine.setTextColor(Color.RED);

        return  objectifLine;
    }
}