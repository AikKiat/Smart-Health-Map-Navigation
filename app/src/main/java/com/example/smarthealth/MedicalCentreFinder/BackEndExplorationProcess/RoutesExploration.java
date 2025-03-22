package com.example.smarthealth.MedicalCentreFinder.BackEndExplorationProcess;

import static android.provider.Settings.System.getString;

import com.example.smarthealth.MedicalCentreFinder.MapPageNavigation.MapClinicFinder;
import com.example.smarthealth.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoutesExploration extends MapExploration{
    double originLatitude;
    double originLongitude;

    double destinationLatitude;
    double destinationLongitude;

    private String travelMode;

    public RoutesExploration(String fieldMask, MapClinicFinder mapClinicFinder)
    {
        super(fieldMask, mapClinicFinder);
    }
    public void setOriginAndDestination(LatLng origin, LatLng destination)
    {
        originLatitude = origin.latitude;
        originLongitude = origin.longitude;

        destinationLatitude = destination.latitude;
        destinationLongitude = destination.longitude;
    }

    public void setTravelMode(TravelModes travelMode)
    {
        switch (travelMode){
            case DRIVE:
                this.travelMode = "DRIVE";
                break;
            case WALK:
                this.travelMode = "WALK";
                break;
        }
    }

    @Override
    public String getRequestBody() {
        return "{" +
                "  \"origin\": {" +
                "    \"location\": {" +
                "      \"latLng\": {" +
                "        \"latitude\": \""+ originLatitude + "\" ," +
                "        \"longitude\": \"" + originLongitude + "\","+
                "      }" +
                "    }" +
                "  }," +
                "  \"destination\": {" +
                "    \"location\": {" +
                "      \"latLng\": {" +
                "        \"latitude\": \"" + destinationLatitude +"\","+
                "        \"longitude\": \"" + destinationLongitude +"\","+
                "      }" +
                "    }" +
                "  }," +
                "  \"travelMode\": \"" + travelMode + "\"," +
                "  \"routingPreference\": \"TRAFFIC_AWARE\"," +
                "  \"computeAlternativeRoutes\": false," +
                "  \"routeModifiers\": {" +
                "    \"avoidTolls\": false," +
                "    \"avoidHighways\": false," +
                "    \"avoidFerries\": false" +
                "  }," +
                "  \"languageCode\": \"en-US\"," +
                "  \"units\": \"IMPERIAL\"" +
                "}";
    }

    @Override
    public String getURL() {
        String apiKey = mapClinicFinder.requireActivity().getString(R.string.my_gmap_api_key);
        return "https://routes.googleapis.com/directions/v2:computeRoutes?key=" + apiKey;
    }

    @Override
    public List<?> getExplorationResults() throws JSONException {
        List<JSONArray> result = new ArrayList<>();
        result.add(jsonArray);
        return  result;
    }


}
