package com.example.smarthealth.calendar;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView dayOfMonth;
    public final View currentDayMarker;
    public final View hasEventMarker;
    private final CalendarAdapter.OnItemListener onItemListener;

    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener) {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        currentDayMarker = itemView.findViewById(R.id.currentDayMarker);
        hasEventMarker = itemView.findViewById(R.id.cellDayMarker);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onItemListener.onCalenderCellClick(getAdapterPosition(), (String) dayOfMonth.getText());
    }
}
