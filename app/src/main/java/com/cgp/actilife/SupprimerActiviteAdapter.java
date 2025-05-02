package com.cgp.actilife;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SupprimerActiviteAdapter extends RecyclerView.Adapter<SupprimerActiviteAdapter.ViewHolder> {

    private final List<Pair<Activite, String>> activitesAvecJour;
    private final OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Activite activite, String jour);
    }

    public SupprimerActiviteAdapter(List<Pair<Activite, String>> activitesAvecJour, OnDeleteClickListener listener) {
        this.activitesAvecJour = activitesAvecJour;
        this.deleteListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtActivite;
        ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            txtActivite = itemView.findViewById(R.id.textActivite);
            btnDelete = itemView.findViewById(R.id.btnSupprimerActivite);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_supprimer_activite, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pair<Activite, String> pair = activitesAvecJour.get(position);
        Activite a = pair.first;
        String jour = pair.second;

        // Format du jour : "Lundi 08/04"
        String jourFormate = jour;
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
            Date date = inputFormat.parse(jour);
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE dd/MM", Locale.FRENCH);
            jourFormate = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.txtActivite.setText(a.getHeureDebut() + " - " + a.getHeureFin() + " : " + a.getNom() + " " + jourFormate);

        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                // Informer la liste principale
                deleteListener.onDeleteClick(a, jour);

                // Supprimer localement pour mise à jour visuelle
                activitesAvecJour.remove(currentPosition);
                notifyItemRemoved(currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return activitesAvecJour.size();
    }
}
