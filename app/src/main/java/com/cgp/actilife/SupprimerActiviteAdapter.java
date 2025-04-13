package com.cgp.actilife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SupprimerActiviteAdapter extends RecyclerView.Adapter<SupprimerActiviteAdapter.ViewHolder> {

    private final ArrayList<Activite> activites;
    private final OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public SupprimerActiviteAdapter(ArrayList<Activite> activites, OnDeleteClickListener listener) {
        this.activites = activites;
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
        Activite a = activites.get(position);
        holder.txtActivite.setText(a.getHeureDebut() + "-" + a.getHeureFin() + " : " + a.getNom());

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return activites.size();
    }
}
