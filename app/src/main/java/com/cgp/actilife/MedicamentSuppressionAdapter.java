package com.cgp.actilife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class MedicamentSuppressionAdapter extends RecyclerView.Adapter<MedicamentSuppressionAdapter.ViewHolder> {

    private final List<Medicament> medicamentList;
    private final MedicamentSuppresionListener listener;

    public MedicamentSuppressionAdapter(List<Medicament> list, MedicamentSuppresionListener listener) {
        this.medicamentList = list;
        this.listener = listener;
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
        Medicament medicament = medicamentList.get(position);

        holder.nomTextView.setText(medicament.getNom());
        holder.heureTextView.setText(String.format(" - %s", medicament.getHeure()));

        holder.btnSupprimer.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            DatabaseOpenHelper db = new DatabaseOpenHelper(v.getContext());

            // Rechercher l’enregistrement correspondant dans la BDD
            List<Map<String, String>> tous = db.getAll(ConstDB.MEDICAMENTS);

            for (Map<String, String> ligne : tous) {
                if (ligne.get(ConstDB.MEDICAMENTS_NOM).equals(medicament.getNom())) {
                    String heuresConcat = ligne.get(ConstDB.MEDICAMENTS_HEURES_PRISE);
                    String[] heures = heuresConcat.split(",");
                    StringBuilder nouvellesHeures = new StringBuilder();

                    for (String h : heures) {
                        if (!h.trim().equals(medicament.getHeure())) {
                            if (nouvellesHeures.length() > 0) nouvellesHeures.append(",");
                            nouvellesHeures.append(h.trim());
                        }
                    }

                    String idStr = ligne.get("id");
                    if (idStr == null) continue;

                    long id = Long.parseLong(idStr);

                    if (nouvellesHeures.length() == 0) {
                        db.effacerEnregistrement(ConstDB.MEDICAMENTS, id);
                    } else {
                        db.updateTableWithId(
                                ConstDB.MEDICAMENTS,
                                Map.of(ConstDB.MEDICAMENTS_HEURES_PRISE, nouvellesHeures.toString()),
                                (int) id
                        );
                    }
                    break;
                }
            }

            // Supprimer visuellement de la liste
            medicamentList.remove(pos);
            notifyItemRemoved(pos);
            db.close();

            if(listener!=null){
                listener.onMedicamentSupprimer();
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicamentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomTextView, heureTextView;
        ImageButton btnSupprimer;

        ViewHolder(View itemView) {
            super(itemView);
            nomTextView = itemView.findViewById(R.id.nomMedicament);
            heureTextView = itemView.findViewById(R.id.heureMedicament);
            btnSupprimer = itemView.findViewById(R.id.btnSupprimerItem);
        }
    }
}
