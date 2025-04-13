package com.cgp.actilife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ActiviteAdapter extends RecyclerView.Adapter<ActiviteAdapter.ViewHolder> {

    private final ArrayList<Activite> liste;

    public ActiviteAdapter(ArrayList<Activite> liste) {
        this.liste = liste;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomActivite, heureDebut, heureFin;

        public ViewHolder(View itemView) {
            super(itemView);
            nomActivite = itemView.findViewById(R.id.nomActivite);
            heureDebut = itemView.findViewById(R.id.heureDebutActivite);
            heureFin = itemView.findViewById(R.id.heureFinActivite);
        }
    }

    @Override
    public ActiviteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activite, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ActiviteAdapter.ViewHolder holder, int position) {
        Activite a = liste.get(position);
        String heuresFormates = a.getHeureDebut() + " - " + a.getHeureFin();
        holder.nomActivite.setText( "  "+ a.getNom());
        holder.heureDebut.setText(heuresFormates);

    }

    @Override
    public int getItemCount() {
        return liste.size();
    }
}

