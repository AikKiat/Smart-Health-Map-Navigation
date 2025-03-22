package com.example.smarthealth.MedicalCentreFinder.BackEndExplorationProcess;

import android.util.Log;

import com.example.smarthealth.MedicalCentreFinder.MapPageNavigation.MapClinicFinder;
import com.example.smarthealth.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProximityExploration extends MapExploration{

    private int proximityRad = 5000;

    public ProximityExploration(String fieldMask, int proximityRad, MapClinicFinder mapClinicFinder)
    {
        super(fieldMask, mapClinicFinder);
        this.proximityRad = proximityRad;
    }

    @Override
    public String getRequestBody()
    {
        return "{"
                + "\"includedTypes\": [\"" + placeName + "\"],"
                + "\"maxResultCount\": 10,"
                + "\"locationRestriction\": {"
                + "  \"circle\": {"
                + "    \"center\": {"
                + "      \"latitude\": " + latitude + ","
                + "      \"longitude\": " + longitude + " "
                + "    },"
                + "    \"radius\": " + proximityRad + ".0"
                + "  }"
                + "}"
                + "}";
    }
    public String getFieldMask()
    {
        return fieldMask;
    }

    public boolean getHasFieldMask()
    {
        return bhasFieldMask;
    }

    @Override
    public String getURL()
    {
        String apiKey = mapClinicFinder.requireActivity().getString(R.string.my_gmap_api_key);
        return "https://places.googleapis.com/v1/places:searchNearby?key="+ apiKey;
    }

    private HashMap<String,String> getSingleNearbyPlace(JSONObject googlePlaceJSON) throws JSONException {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        if (!googlePlaceJSON.isNull("displayName")) {
            placeName = googlePlaceJSON.getString("displayName");
        }
        if (!googlePlaceJSON.isNull("formattedAddress")) {
            vicinity = googlePlaceJSON.getString("formattedAddress");
        }
        latitude = googlePlaceJSON.getJSONObject("location").getString("latitude");
        longitude = googlePlaceJSON.getJSONObject("location").getString("longitude");

        googlePlaceMap.put("place_name", placeName);
        googlePlaceMap.put("vicinity", vicinity);
        googlePlaceMap.put("lat", latitude);
        googlePlaceMap.put("lng", longitude);

        Log.d("Place data", googlePlaceMap.toString());

        return googlePlaceMap;
    }

    @Override
    public List<?> getExplorationResults() throws JSONException {
        int counter = jsonArray.length();
        List<HashMap<String, String>> allNearbyPlaces = new ArrayList<>();
        HashMap<String, String> nearbyPlaceDetails = null;

        for (int i = 0; i < counter; i++)
        {
            nearbyPlaceDetails = getSingleNearbyPlace((JSONObject) jsonArray.get(i));
            allNearbyPlaces.add(nearbyPlaceDetails);
        }
        return allNearbyPlaces;
    }
}
