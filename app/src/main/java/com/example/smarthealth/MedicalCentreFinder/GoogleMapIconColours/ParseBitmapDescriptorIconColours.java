package com.example.smarthealth.MedicalCentreFinder.GoogleMapIconColours;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class ParseBitmapDescriptorIconColours{
    public static float bitMapMarkerColour(Colours colours)
    {
        float value = 0.0f;
        switch (colours){
            case HUE_RED:
                value = BitmapDescriptorFactory.HUE_RED;
                break;

            case HUE_BLUE:
                value =BitmapDescriptorFactory.HUE_BLUE;
                break;

            case HUE_AZURE:
                value = BitmapDescriptorFactory.HUE_AZURE;
                break;

            case HUE_CYAN:
                value = BitmapDescriptorFactory.HUE_CYAN;
                break;

            case HUE_GREEN:
                value = BitmapDescriptorFactory.HUE_GREEN;
                break;

            case HUE_MAGENTA:
                value = BitmapDescriptorFactory.HUE_MAGENTA;
                break;
            case HUE_ORANGE:
                value = BitmapDescriptorFactory.HUE_ORANGE;
                break;

            case HUE_ROSE:
                value = BitmapDescriptorFactory.HUE_ROSE;
                break;

            case HUE_VIOLET:
                value = BitmapDescriptorFactory.HUE_VIOLET;
                break;

            case HUE_YELLOW:
                value = BitmapDescriptorFactory.HUE_YELLOW;
                break;
        }
        return value;
    }
}