package com.example.smarthealth.calendar;

import java.util.Calendar;
import java.util.List;

public interface CalendarEventProvider {
    boolean hasEvent(Calendar date);
    List<CalendarEvent> getEventsForDay(Calendar date);
}
