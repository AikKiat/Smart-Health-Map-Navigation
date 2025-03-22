package com.example.smarthealth.calendar;

import java.util.Calendar;

public class CalendarEvent {
    private String eventTitle;
    private String eventDescription;
    private Calendar eventDateCalendar;

    CalendarEvent(String eventTitle, String eventDescription, Calendar eventDateCalendar) {
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventDateCalendar = (Calendar) eventDateCalendar.clone();
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Calendar getEventDateCalendar() {
        return (Calendar) eventDateCalendar.clone();
    }

    public void setEventDate(Calendar eventDateCalendar) {
        this.eventDateCalendar = eventDateCalendar;
    }
}
