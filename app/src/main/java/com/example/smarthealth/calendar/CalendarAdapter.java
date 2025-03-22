package com.example.smarthealth.calendar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<Calendar> daysOfMonth;
    private final OnItemListener onItemListener;
    private final int currentDatePosition;
    private final Calendar selectedDate;
    private final CalendarEventProvider calendarEventProvider;

    public CalendarAdapter(ArrayList<Calendar> daysOfMonth, CalendarEventProvider calendarEventProvider, int currentDatePosition, OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.currentDatePosition = currentDatePosition;
        this.onItemListener = onItemListener;
        this.calendarEventProvider = calendarEventProvider;
        selectedDate = Calendar.getInstance();
    }

    private int getFirstDayIndex() {
        Calendar calendar = (Calendar) selectedDate.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    private int getLastDayIndex() {
        return getFirstDayIndex()-2 + selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent ,false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.16666666);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        Calendar currentDayCalendar = daysOfMonth.get(position);
        String currentDay = String.valueOf(currentDayCalendar.get(Calendar.DAY_OF_MONTH));
        holder.dayOfMonth.setText(currentDay);

        // Set text to gray for days of previous and next month
        if (position < getFirstDayIndex()-1 || position > getLastDayIndex()) {
            holder.dayOfMonth.setTextColor(Color.parseColor("#8C8C8C"));
        }

        holder.itemView.post(() ->
                // Transition Y up to show only current week
                holder.itemView.setTranslationY(-holder.itemView.getHeight() * (float)(currentDatePosition/7)));

        // TODO: link events to Android calendar system
        if (!calendarEventProvider.hasEvent(currentDayCalendar)) {
            holder.hasEventMarker.setVisibility(View.INVISIBLE);
        }

        if (position != currentDatePosition) {
            holder.currentDayMarker.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public interface OnItemListener {
        void onCalenderCellClick(int position, String dayText);
    }
}
