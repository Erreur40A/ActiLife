package com.cgp.actilife;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SleepReminderAdapter extends RecyclerView.Adapter<SleepReminderAdapter.ViewHolder>{
    private final List<SleepReminder> reminders;
    private final Context context;

    public SleepReminderAdapter(Context context, List<SleepReminder> reminders) {
        this.reminders = reminders;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dayTextView;
        public EditText timeEditText;

        public ViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            timeEditText = itemView.findViewById(R.id.timeEditText);
        }
    }

    @Override
    public SleepReminderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sleep_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SleepReminder reminder = reminders.get(position);
        holder.dayTextView.setText(reminder.getDay());
        holder.timeEditText.setText(reminder.getTime());

        holder.timeEditText.setOnClickListener(v -> {
            String[] parts = reminder.getTime().split("h");
            int hour = Integer.parseInt(parts[0]);
            int minute = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    (view, hourOfDay, minute1) -> {
                        String time = hourOfDay + "h" + (minute1 < 10 ? "0" : "") + minute1;
                        reminder.setTime(time);
                        holder.timeEditText.setText(time);
                    }, hour, minute, true);

            timePickerDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }
}
