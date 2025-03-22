package com.example.smarthealth.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.R;
import com.example.smarthealth.calendar.AndroidCalendarEventProvider;
import com.example.smarthealth.calendar.CalendarEvent;
import com.example.smarthealth.calendar.CalendarEventProvider;
import com.example.smarthealth.calendar.CalendarAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private Calendar selectedDate;
    private LinearLayout scheduleContainer;
    private CalendarEventProvider calendarEventProvider;
    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.landing_fragment, container, false);

        // TODO: change to other calendar adapter, most likely from database.
        calendarEventProvider = new AndroidCalendarEventProvider(requireContext());

        initCalendarWidgets();
        setUpMainContentSlider();
        initUpcomingScheduleWidgets();
        initBotSuggestionWidgets();

        selectedDate = Calendar.getInstance();
        setMonthView();

        return view;
    }

    private void initCalendarWidgets() {
        LinearLayout calendarParentView = view.findViewById(R.id.calendarView);
        View calendarRecycleView = LayoutInflater.from(requireContext()).inflate(R.layout.calendar_view, calendarParentView, false);
        calendarParentView.addView(calendarRecycleView);
        calendarRecyclerView = (RecyclerView) view.findViewById(R.id.calendarRecyclerView);
        monthYearText = (TextView) view.findViewById(R.id.calendarMonthYear);
    }

    private void setMonthView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
        String selectedDateText = dateFormat.format(selectedDate.getTime());
        monthYearText.setText(selectedDateText);

        Pair<ArrayList<Calendar>, Integer> results = daysInMonthArray(selectedDate);
        ArrayList<Calendar> daysInMonth = results.first;
        int currentDatePosition = results.second;

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, calendarEventProvider, currentDatePosition, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private Pair<ArrayList<Calendar>, Integer> daysInMonthArray(Calendar selectedDate) {
        ArrayList<Calendar> daysInMonth = new ArrayList<>();
        selectedDate.set(Calendar.DAY_OF_MONTH, 1);
        int totalDaysInMonth = selectedDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK);

        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int currentDatePosition = -1;

        // Add days of previous months
        Calendar prevMonth = (Calendar) selectedDate.clone();
        prevMonth.add(Calendar.MONTH, -1);
        int totalDaysPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i < dayOfWeek; i++) {
            Calendar day = (Calendar) selectedDate.clone();
            day.set(Calendar.DAY_OF_MONTH, totalDaysPrevMonth - (6 - i));
            daysInMonth.add(day);
        }

        // Add days of the current month
        for (int day = 1; day <= totalDaysInMonth; day++) {
            Calendar dayCalendar = (Calendar) selectedDate.clone();
            dayCalendar.set(Calendar.DAY_OF_MONTH, day);
            daysInMonth.add(dayCalendar);

            if (day == currentDay) {
                currentDatePosition = daysInMonth.size() - 1;
            }
        }

        // Add empty strings for days after the last day of the month
        Calendar nextMonth = (Calendar) selectedDate.clone();
        nextMonth.add(Calendar.MONTH, 1);
        int totalSlots = 42;
        for (int day = 1; daysInMonth.size() < totalSlots; day++) {
            Calendar dayCalendar = (Calendar) nextMonth.clone();
            dayCalendar.set(Calendar.DAY_OF_MONTH, day);
            daysInMonth.add(dayCalendar);
        }

        return new Pair<>(daysInMonth, currentDatePosition);
    }


    public void previousMonthAction(View view) {
        selectedDate.add(Calendar.MONTH, -1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        selectedDate.add(Calendar.MONTH, 1);
        setMonthView();
    }

    private void setUpMainContentSlider() {
        View mainContentView = view.findViewById(R.id.mainContentView);
        View calendarView = view.findViewById(R.id.calendarView);
        mainContentView.setOnTouchListener(new View.OnTouchListener() {
            private float distanceContentFinger; // distance from top of mainContent view to finger, used for repositioning of mainContent during dragging
            private float mainContentInitialY = -1; // initial y of main content, will be set only once after view initialization, used for repositioning of mainContent after dragging and limiting drag distance
            private float previousFingerY = -1; // previous finger y for dragging distance tracking
            private float currentFingerY = -1; // current finger y for dragging distance tracking
            private boolean isMoving = false; // is moving mainContent or not, is set to true when user finger down on top of mainContent view within moveThresholdY
            private float touchDownY = -1; // record of y of finger touch down for y translation of calendar
            private float initialTranslation = 0; // record of old y translation value of calendar before finger touch down

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // threshold distance from top of mainContent view to enable dragging
                float moveThresholdY = 200;

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        // coordinate of mainContentView relative to screen
                        int[] mainContentViewCoord = new int[2];
                        mainContentView.getLocationOnScreen(mainContentViewCoord);
                        int mainContentViewY = mainContentViewCoord[1];

                        // If user drag the top part mainContentView, disable scrolling of page
                        if (event.getRawY() - mainContentViewY <= moveThresholdY) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        }

                        // Getting originalY for resetting of mainContent after user slide up mainContent
                        if (mainContentInitialY == -1) {
                            mainContentView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mainContentInitialY = mainContentView.getY();
                                }
                            });
                        }

                        // Getting finger touchdownY for translation of calendar view
                        touchDownY = event.getRawY();

                        // Getting initialTranslation of calendar view for clamping of translation value
                        if (calendarRecyclerView.getChildCount() > 0) {
                            initialTranslation = calendarRecyclerView.getChildAt(0).getTranslationY();
                        }

                        // Getting position of mainContentView relative to finger position for repositioning according to finger position
                        distanceContentFinger = v.getY() - event.getRawY();

                        // Set isMoving to true if user drags mainContentView in designated area
                        if (event.getRawY() - mainContentViewY <= moveThresholdY) {
                            isMoving = true;
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (!isMoving) {
                            return true;
                        }

                        previousFingerY = currentFingerY;
                        currentFingerY = event.getRawY();

                        float mainContentNewY = event.getRawY() + distanceContentFinger;
                        float dyTranslationCalendar = event.getRawY() - touchDownY;
                        // Getting the first child to get y position value for clamping of y translation of calendar
                        int calendarFirstItemPosition = ((GridLayoutManager) calendarRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                        View calendarFirstItemView = calendarRecyclerView.getLayoutManager().findViewByPosition(calendarFirstItemPosition);

                        if (mainContentNewY >= mainContentInitialY && mainContentNewY <= calendarView.getY() + calendarView.getHeight() + 30) {
                            // Move mainContent according to finger position
                            v.setY(mainContentNewY);
                            // Translate y position of each cells in calendar
                            if (calendarFirstItemView != null && (calendarFirstItemView.getY() < 0)) {
                                for (int i = 0; i < calendarRecyclerView.getChildCount(); i++) {
                                    View child = calendarRecyclerView.getChildAt(i);
                                    child.setTranslationY(Math.min(initialTranslation + dyTranslationCalendar, 0));  // Apply translation to each item in calendar
                                }
                            }
                        }

                        return true;
                    case MotionEvent.ACTION_UP:
                        isMoving = false;

                        float topMost = mainContentInitialY;
                        float bottomMost = calendarView.getY() + calendarView.getHeight() + 30;
                        float swipeDistance = currentFingerY - previousFingerY;

                        // Set to position of mainContent to original position if user swipe up
                        // otherwise set to designated bottom position to expand calendar
                        if (swipeDistance > 0) {
                            // Set mainContent position
                            v.animate().y(bottomMost).setDuration(400).setInterpolator(new OvershootInterpolator()).start();

                            // Set calendar translation
                            for (int i = 0; i < calendarRecyclerView.getChildCount(); i++) {
                                View child = calendarRecyclerView.getChildAt(i);
                                child.setTranslationY(0);
                            }
                        } else {
                            // Set mainContent position
                            v.animate().y(topMost).setDuration(400).setInterpolator(new OvershootInterpolator()).start();

                            // Set calendar translation
                            Calendar calendar = Calendar.getInstance();
                            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                            int currentDateRow = (currentDay + calendar.get(Calendar.DAY_OF_WEEK) - 2) / 7;
                            for (int i = 0; i < calendarRecyclerView.getChildCount(); i++) {
                                View child = calendarRecyclerView.getChildAt(i);
                                child.setTranslationY(-child.getHeight() * currentDateRow);
                            }
                        }
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onCalenderCellClick(int position, String dayText) {
        // TODO 1: link to Android Calendar? and complete this function.
        // TODO 2: Set popup title to current date
        Dialog calendarEventDialog = new Dialog(requireActivity());
        calendarEventDialog.setContentView(R.layout.calendar_event_popup);
        if (calendarEventDialog.getWindow() != null) {
            WindowManager.LayoutParams params = calendarEventDialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            calendarEventDialog.getWindow().setAttributes(params);
        }

        List<CalendarEvent> calendarEvents = calendarEventProvider.getEventsForDay((Calendar) Calendar.getInstance().clone());
        LinearLayout eventListContainer = calendarEventDialog.findViewById(R.id.calendarPopupItemContainer);

        TextView dateTextView = calendarEventDialog.findViewById(R.id.calendarPopupDate);
        dateTextView.setText(dayText);

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (CalendarEvent calendarEvent : calendarEvents) {
            View eventView = inflater.inflate(R.layout.calendar_event_popup_item, eventListContainer, false);
            TextView eventTextView = eventView.findViewById(R.id.calendarPopupEventTitle);
            eventTextView.setText(calendarEvent.getEventTitle());
            eventListContainer.addView(eventView);
        }

        calendarEventDialog.show();
    }

    private void initBotSuggestionWidgets() {
        // TODO: Replace placeholders with suggestion from AI (pls set word limits)

        String[] titles = {"Increase Protein Intake", "Choose complex carbohydrates", "Stay hydrated"};
        String[] descriptions = {"Increase in take of fish, chicken, eggs, tofu, legumes to...",
                "Choose complex carbohydrates for example whole grains, vegetables to avoid sugar spikes. Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao." +
                        "Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                        +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                        +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                        +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                        +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                        +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao."
                        +"Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.Lorem ipsum dolmao.",
                "Drink more water, or herbal teas, soups to stay hydrated, because H2O make you human."};

        for (int i = 0; i < titles.length; i++) {
            addBotSuggestion(titles[i], descriptions[i]);
        }
    }

    public void addBotSuggestion(String title, String description) {
        LinearLayout suggestionContainer = view.findViewById(R.id.botSuggestionsBox);
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View suggestionView = inflater.inflate(R.layout.bot_suggestion_view, suggestionContainer, false);

        TextView titleView = suggestionView.findViewById(R.id.suggestionTitle);
        TextView descView = suggestionView.findViewById(R.id.suggestionDesc);

        titleView.setText(title);
        descView.setText(description);

        suggestionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a popup view for user to view full text (if text is too long)
                Dialog botSuggestionDialog = new Dialog(requireActivity());
                botSuggestionDialog.setContentView(R.layout.bot_suggestion_popup);
                if (botSuggestionDialog.getWindow() != null) {
                    WindowManager.LayoutParams params = botSuggestionDialog.getWindow().getAttributes();
                    params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    botSuggestionDialog.getWindow().setAttributes(params);
                }

                TextView popupTitleView = botSuggestionDialog.findViewById(R.id.suggestionPopupTitle);
                TextView popupDescView = botSuggestionDialog.findViewById(R.id.suggestionPopupDesc);
                popupTitleView.setText(title);
                popupDescView.setText(description);

                botSuggestionDialog.show();
            }
        });

        suggestionContainer.addView(suggestionView);
    }

    private void initUpcomingScheduleWidgets() {
        // TODO: fetch schedule from database.

        scheduleContainer = view.findViewById(R.id.upcomingScheduleLayout);
        addSchedule("Aspirin", "Today", "12:00pm", R.drawable.up_schedule_medicine);
        addSchedule("Lunch", "Today", "12:30pm", R.drawable.up_schedule_meal);
        addSchedule("Nospirit", "Today", "01:00pm", R.drawable.up_schedule_medicine);
    }

    public void addSchedule(String scheduleName, String scheduleDay, String scheduleTime, int iconResId) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View scheduleView = inflater.inflate(R.layout.upcoming_schedule_view, scheduleContainer, false);

        TextView scheduleNameView = scheduleView.findViewById(R.id.upcomingScheduleName);
        ImageView scheduleIconView = scheduleView.findViewById(R.id.upcomingScheduleIcon);
        TextView scheduleDayView = scheduleView.findViewById(R.id.upcomingScheduleDay);
        TextView scheduleTimeView = scheduleView.findViewById(R.id.upcomingScheduleTime);

        scheduleNameView.setText(scheduleName);
        scheduleDayView.setText(scheduleDay);
        scheduleTimeView.setText(scheduleTime);

        scheduleIconView.setImageResource(iconResId);
        scheduleContainer.addView(scheduleView);
    }

}
