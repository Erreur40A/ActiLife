package com.cgp.actilife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListeDeroulanteAdapter extends RecyclerView.Adapter<ListeDeroulanteAdapter.ViewHolder> {
    private ArrayList<String> items;
    private View.OnClickListener onClickListener;

    public ListeDeroulanteAdapter(ArrayList<String> items, View.OnClickListener onClickListener) {
        this.items = items;
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_settings, parent, false);

        return new ViewHolder(v, onClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = items.get(position);

        holder.item.setText(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView item;
        View.OnClickListener clickListener;

        public ViewHolder(View itemView, View.OnClickListener clickListener) {
            super(itemView);
            item = (TextView) itemView.findViewById(R.id.activity_settings);
            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onClick(view);
            }
        }
    }
}
