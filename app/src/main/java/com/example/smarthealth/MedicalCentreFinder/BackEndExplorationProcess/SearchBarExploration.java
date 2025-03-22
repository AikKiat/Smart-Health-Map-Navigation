package com.example.smarthealth.MedicalCentreFinder.BackEndExplorationProcess;

import com.example.smarthealth.MedicalCentreFinder.MapPageNavigation.MapClinicFinder;
import com.example.smarthealth.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchBarExploration extends MapExploration{


    private  int proximityRad = 5000;

    public SearchBarExploration(String fieldMask, int proximityRad, MapClinicFinder mapClinicFinder)
    {
        super(fieldMask, mapClinicFinder);
        this.proximityRad = proximityRad;
    }
    @Override
    public String getRequestBody()
    {
        //Request body for Text autocomplete predictions
//        return "{"
//                + "\"input\": \"" + placeName + "\","
//                + "\"locationBias\": {"
//                + "  \"circle\": {"
//                + "    \"center\": {"
//                + "      \"latitude\": " + latitude + ","
//                + "      \"longitude\": " + longitude + ""
//                + "    },"
//                + "    \"radius\": " + proximityRad + ".0"
//                + "  }"
//                + "}"
//                + "}";

        return "{"
                + "\"textQuery\": \"" + placeName + "\","
                + "\"pageSize\": \"" + 10 + "\","
                + "\"locationBias\": {"
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
        //For text autocomplete predictions
        //return "https://places.googleapis.com/v1/places:autocomplete?key=";
        return "https://places.googleapis.com/v1/places:searchText?key=" + apiKey;
    }

    @Override
    public List<?> getExplorationResults() throws JSONException {
        List<HashMap<String, String>> placePredictions = new ArrayList<>();
        for (int i =0; i < jsonArray.length(); i++) //iterate through all JSON objects!
        {
            HashMap<String, String> onePlacePredictionDetails = new HashMap<>();
            String formattedAddress = jsonArray.getJSONObject(i).getString("formattedAddress");
            String latitude = jsonArray.getJSONObject(i).getJSONObject("location").getString("latitude");
            String longitude = jsonArray.getJSONObject(i).getJSONObject("location").getString("longitude");
            String placeName = jsonArray.getJSONObject(i).getJSONObject("displayName").getString("text");

            onePlacePredictionDetails.put("Place Name", placeName);
            onePlacePredictionDetails.put("Address", formattedAddress);
            onePlacePredictionDetails.put("Latitude", latitude);
            onePlacePredictionDetails.put("Longitude", longitude);

            placePredictions.add(onePlacePredictionDetails);
        }
        return placePredictions;
    }
}
