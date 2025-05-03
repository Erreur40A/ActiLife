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

        // ✅ Nom du médicament
        holder.nomTextView.setText(medicament.getNom() + " : ");

        // ✅ Format de l'heure : 08:30 → 08h30
        String heureOriginale = medicament.getHeure(); // Ex: "08:30"
        String heureFormatee = heureOriginale.replace(":", "h");
        holder.heureTextView.setText(heureFormatee);
    }

    @Override
    public int getItemCount() {
        return medicamentList.size();
    }

    public void addMedicament(Medicament medicament) {
        medicamentList.add(medicament);
        notifyItemInserted(medicamentList.size() - 1);
    }

    public void removeMedicament(Medicament medicament){
        int pos = medicamentList.indexOf(medicament);
        if(pos!=-1){
            medicamentList.remove(medicament);
            notifyItemRemoved(pos);
        }
    }

    public static class MedicamentViewHolder extends RecyclerView.ViewHolder {
        public TextView nomTextView, heureTextView;

        public MedicamentViewHolder(View view) {
            super(view);
            nomTextView = view.findViewById(R.id.nomMedicament);
            heureTextView = view.findViewById(R.id.heureMedicament);
        }
    }
}
