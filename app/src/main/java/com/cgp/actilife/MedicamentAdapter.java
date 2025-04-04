package com.cgp.actilife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicamentAdapter extends RecyclerView.Adapter<MedicamentAdapter.MedicamentViewHolder> {

    private List<Medicament> medicamentList;

    public MedicamentAdapter(List<Medicament> medicamentList) {
        this.medicamentList = medicamentList;
    }

    @Override
    public MedicamentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicament, parent, false);
        return new MedicamentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MedicamentViewHolder holder, int position) {
        Medicament medicament = medicamentList.get(position);

        // On affiche une seule prise ici : exemple "Doliprane (Comprimé) à 08:00"
        holder.nomTextView.setText(medicament.getNom());
        holder.typeTextView.setText("(" + medicament.getType() + ")");
        holder.heureTextView.setText(medicament.getHeure());
    }

    @Override
    public int getItemCount() {
        return medicamentList.size();
    }

    public static class MedicamentViewHolder extends RecyclerView.ViewHolder {
        public TextView nomTextView, typeTextView, heureTextView;

        public MedicamentViewHolder(View view) {
            super(view);
            nomTextView = view.findViewById(R.id.nomMedicament);
            typeTextView = view.findViewById(R.id.typeMedicament);
            heureTextView = view.findViewById(R.id.heureMedicament);
        }
    }
}
