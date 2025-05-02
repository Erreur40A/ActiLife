package com.cgp.actilife;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class PopUp {
    Context context;
    View viewPopup;
    int layout;
    AlertDialog popup;

    public PopUp(Context c, int idLayout){
        this.context = c;
        this.layout = idLayout;

        LayoutInflater inflater = LayoutInflater.from(context);
        viewPopup = inflater.inflate(layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(viewPopup);

        popup = builder.create();
    }

    public void show(){
        popup.show();
    }

    public void dismiss(){
        popup.dismiss();
    }

    //permet de récuperer un TextView... d'une pop-up
    public <T extends View> T getView(int id){
        return viewPopup.findViewById(id);
    }

    //initialiser un listener pour un boutton, un textView... (toutes les views qui sont des View)
    public void setOnClickListener(int idView, View.OnClickListener listener){
        View v = viewPopup.findViewById(idView);

        assert v!= null;
        v.setOnClickListener(listener);
    }

    //initialiser un listener pour une liste déroulante... (toutes les views qui sont des Spinner)
    public void setOnItemSelectedListener(int idView, AdapterView.OnItemSelectedListener listener){
        Spinner v = viewPopup.findViewById(idView);

        assert v!= null;
        v.setOnItemSelectedListener(listener);
    }
}
