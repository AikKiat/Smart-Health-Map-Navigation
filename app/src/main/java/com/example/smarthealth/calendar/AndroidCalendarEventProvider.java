package com.example.smarthealth.calendar;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AndroidCalendarEventProvider implements CalendarEventProvider {
    private final Context context;

    public AndroidCalendarEventProvider(Context context) {
        this.context = context;
    }

    @Override
    public boolean hasEvent(Calendar date) {
        return !getEventsForDay(date).isEmpty();
    }

    @Override
    public List<CalendarEvent> getEventsForDay(Calendar date) {
        return queryAndroidCalendar(date);
    }

    private List<CalendarEvent> queryAndroidCalendar(Calendar date) {
        // TODO: placeholder code
         CalendarEvent calendarEvent = new CalendarEvent("title", "descp", (Calendar) Calendar.getInstance().clone());
        return new ArrayList<>(Arrays.asList(calendarEvent, calendarEvent, calendarEvent, calendarEvent, calendarEvent));
    }

}
