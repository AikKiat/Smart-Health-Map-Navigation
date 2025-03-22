package com.example.smarthealth.MedicalCentreFinder.MapPageNavigation;

import android.app.Activity;
import android.os.Handler;

abstract class HandleAsyncInputProcess{

    Activity activity;
    long delayTime;
    HandleAsyncInputProcess(Activity activity, long delayTime)
    {
        this.activity = activity;
        this.delayTime = delayTime;
    }

    public abstract void outerFunction();

    public abstract void backgroundThreadFunction(String inputText);

    public abstract void mainThreadFunction();

    public abstract void exitFunction();

    public void createAsynchronousRunnerProcess(String newText) {
        Handler handler = new Handler();
        // Create a new Runnable for the search operation
        Runnable searchRunnable = () -> {
            if (!newText.isEmpty()) {
                outerFunction();
                // Perform the search on a background thread
                new Thread(() -> {
                    backgroundThreadFunction(newText);
                    // Update the UI on the main thread
                    activity.runOnUiThread(this::mainThreadFunction);
                }).start();
            } else {
                // Clear the results if the query is empty
                activity.runOnUiThread(this::exitFunction);
            }
        };
        // Schedule the Runnable with a debounce delay (e.g., 500ms)
        handler.postDelayed(searchRunnable, delayTime);
    }
}
