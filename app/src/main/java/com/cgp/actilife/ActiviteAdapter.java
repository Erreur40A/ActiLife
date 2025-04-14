package com.cgp.actilife;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActiviteAdapter extends RecyclerView.Adapter<ActiviteAdapter.ViewHolder> {

    private final ArrayList<Activite> liste;

    public ActiviteAdapter(ArrayList<Activite> liste) {
        this.liste = liste;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomActivite, heureDebut, heureFin;
        LinearLayout layoutJours;

        public ViewHolder(View itemView) {
            super(itemView);
            nomActivite = itemView.findViewById(R.id.nomActivite);
            heureDebut = itemView.findViewById(R.id.heureDebutActivite);
            heureFin = itemView.findViewById(R.id.heureFinActivite);
            layoutJours = itemView.findViewById(R.id.layoutJours);
        }
    }

    @Override
    public ActiviteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activite, parent, false);
        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(ActiviteAdapter.ViewHolder holder, int position) {
        holder.layoutJours.removeAllViews();

        Activite a = liste.get(position);
        List<String> jours = a.getJours(); // Exemple : ["08/04/2025", "09/04/2025"]

        SimpleDateFormat formatBrut = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat formatSouhaite = new SimpleDateFormat("EEEE dd/MM", Locale.FRENCH);

        for (String jour : jours) {
            String jourFormate = jour;
            try {
                Date date = formatBrut.parse(jour);
                jourFormate = formatSouhaite.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Exemple : 15h - 18h : Développé couché Lundi 08/04
            String ligne = a.getHeureDebut() + " - " + a.getHeureFin() + " : " + a.getNom() + "    " + jourFormate;

            TextView textViewLigne = new TextView(holder.itemView.getContext());
            textViewLigne.setTypeface(null, Typeface.BOLD);
            textViewLigne.setText(ligne);
            textViewLigne.setTextSize(16);
            textViewLigne.setTextColor(Color.BLACK);
            textViewLigne.setPadding(8, 8, 8, 8);

            holder.layoutJours.addView(textViewLigne);
        }
    }



    @Override
    public int getItemCount() {
        return liste.size();
    }
}

