package com.cgp.actilife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicamentSuppressionAdapter extends RecyclerView.Adapter<MedicamentSuppressionAdapter.ViewHolder> {

    private final List<Medicament> medicamentList;

    public MedicamentSuppressionAdapter(List<Medicament> list) {
        this.medicamentList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_supprimer_medoc, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicament m = medicamentList.get(position);

        holder.nomTextView.setText(m.getNom());
        holder.heureTextView.setText(" - " + m.getHeure());

        holder.btnSupprimer.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            medicamentList.remove(pos);
            notifyItemRemoved(pos);
        });
    }

    @Override
    public int getItemCount() {
        return medicamentList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomTextView, typeTextView, heureTextView;
        ImageButton btnSupprimer;

        ViewHolder(View itemView) {
            super(itemView);
            nomTextView = itemView.findViewById(R.id.nomMedicament);
            heureTextView = itemView.findViewById(R.id.heureMedicament);
            btnSupprimer = itemView.findViewById(R.id.btnSupprimerItem);
        }
    }
}
