package com.cgp.actilife;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActiviteAdapter extends RecyclerView.Adapter<ActiviteAdapter.ViewHolder> {

    private final List<Pair<Activite, String>> activitesAvecJours;
    //va contenir pour chaque activity  un jour dans la liste de jour
    public ActiviteAdapter(List<Pair<Activite, String>> activitesAvecJours) {
        this.activitesAvecJours = activitesAvecJours;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textActivite;
        View separateur;

        public ViewHolder(View itemView) {
            super(itemView);
            textActivite = itemView.findViewById(R.id.textActivite);
            separateur = itemView.findViewById(R.id.separateur);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activite, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pair<Activite, String> pair = activitesAvecJours.get(position);
        Activite activite = pair.first;
        String jour = pair.second;

        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(jour);
            String jourFormate = new SimpleDateFormat("dd/MM", Locale.FRENCH).format(date);

            String ligne = jourFormate + " " + activite.getHeureDebut() + "h-" + activite.getHeureFin() + "h : " + activite.getNom();
            holder.textActivite.setText(ligne);
        } catch (Exception e) {
            holder.textActivite.setText("Erreur de date");
        }

        // Cacher le séparateur uniquement pour le dernier
        if (position == getItemCount() - 1) {
            holder.separateur.setVisibility(View.GONE);
        } else {
            holder.separateur.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return activitesAvecJours.size();
    }

    //Méthode ajoutée pour mettre à jour les données dynamiquement
    public void updateData(List<Pair<Activite, String>> nouvellesDonnees) {
        activitesAvecJours.clear(); // Vide l’ancienne liste
        activitesAvecJours.addAll(nouvellesDonnees); // Ajoute la nouvelle liste
        notifyDataSetChanged(); // Rafraîchit l’affichage
    }
}
