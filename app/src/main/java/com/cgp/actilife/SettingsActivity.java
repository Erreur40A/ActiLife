package com.cgp.actilife;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.text.InputType;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.EditText;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        ImageView btnRetour = findViewById(R.id.iconBack);
        btnRetour.setOnClickListener(v -> finish());


        LinearLayout etNomLayout = (LinearLayout) findViewById(R.id.formCont).findViewById(R.id.etNom);
        if (etNomLayout != null) {
            EditText etNom = etNomLayout.findViewById(R.id.edit_text);
            if (etNom != null) {
                etNom.setHint("Nom");
            }
        }

       LinearLayout etPrenomLayout = (LinearLayout) findViewById(R.id.formCont).findViewById(R.id.etPrenom);
       if (etPrenomLayout != null) {
            EditText etPrenom = etPrenomLayout.findViewById(R.id.edit_text);
           if (etPrenom != null) {
                etPrenom.setHint("Prénom");
          }
       }

        LinearLayout etAgeLayout = findViewById(R.id.formCont).findViewById(R.id.etAge);
        if (etAgeLayout != null) {
            EditText etAge = etAgeLayout.findViewById(R.id.edit_text);
            if (etAge != null) {
                etAge.setHint("Date de naissance");
                etAge.setInputType(InputType.TYPE_NULL);  // Désactive le clavier

                etAge.setOnClickListener(v -> {
                    showDatePickerDialog(etAge);
                });
            }
        }

        LinearLayout etPoidsLayout = (LinearLayout) findViewById(R.id.formCont).findViewById(R.id.etPoids);
        if (etPoidsLayout != null) {
            EditText etPoids = etPoidsLayout.findViewById(R.id.edit_text);
            if (etPoids != null) {
                etPoids.setHint("Poids");
                etPoids.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
        }
    }

    private void showDatePickerDialog(EditText etAge) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etAge.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

}