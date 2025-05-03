package com.cgp.actilife;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SupprimmerMedicamentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicamentSuppressionAdapter adapter;
    private ArrayList<Medicament> listeMedocs;
    private int tailleAvantSuppression;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Crée une fenêtre de type Dialog au lieu de setContentView classique
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.PopUpArrondi);
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_suppression_medoc, null);
        builder.setView(popupView);

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        // Récupération de la liste partagée
        listeMedocs = (ArrayList<Medicament>) RappelMedicamentActivity.medicamentList;
        tailleAvantSuppression = listeMedocs.size();

        recyclerView = popupView.findViewById(R.id.recyclerViewMedicaments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MedicamentSuppressionAdapter(listeMedocs, () -> {});
        recyclerView.setAdapter(adapter);

        AppCompatButton btnRetour = popupView.findViewById(R.id.btnRetourDeSuppression);
        if (btnRetour != null) {
            btnRetour.setOnClickListener(v -> {
                if (listeMedocs.size() < tailleAvantSuppression) {
                    setResult(RESULT_OK);
                }
                dialog.dismiss();
                finish(); // termine l'activité
            });
        }

        dialog.show();

    }
}
