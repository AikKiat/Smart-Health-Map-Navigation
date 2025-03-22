package com.example.smarthealth.MedicalCentreFinder.MapPageNavigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthealth.MedicalCentreFinder.BackEndExplorationProcess.RoutesExploration;
import com.example.smarthealth.MedicalCentreFinder.BackEndExplorationProcess.TravelModes;
import com.example.smarthealth.MedicalCentreFinder.GoogleMapIconColours.Colours;
import com.example.smarthealth.MedicalCentreFinder.GoogleMapIconColours.ParseBitmapDescriptorIconColours;
import com.example.smarthealth.MedicalCentreFinder.BackEndExplorationProcess.ProximityExploration;
import com.example.smarthealth.MedicalCentreFinder.BackEndExplorationProcess.SearchBarExploration;
import com.example.smarthealth.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.squareup.okhttp.Route;

import org.json.JSONArray;


public class MapClinicFinder extends Fragment implements OnMapReadyCallback, MyRecyclerViewAdapter.ItemClickListener {

    //Definitions
    private View view;
    private GoogleMap myMap;
    Location currentLocation;
    private RecyclerView recyclerView;
    private MyRecyclerViewAdapter adapter;
    private SearchBarExploration searchBarExploration;
    private ProximityExploration proximityExploration;
    private List<HashMap<String, String>> searchResults;

    LatLng destination;
    //Getters
    public GoogleMap getMyMap() {
        return myMap;
    }
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
    public MyRecyclerViewAdapter getAdapter()
    {
        return adapter;
    }
    public List<HashMap<String, String>> getSearchResults()
    {
        return searchResults;
    }
    public SearchBarExploration getSearchBarExploration()
    {
        return searchBarExploration;
    }
    public ProximityExploration getProximityExploration()
    {
        return proximityExploration;
    }
    public Double getLatitude()
    {
        return currentLocation.getLatitude();
    }
    public Double getLongitude()
    {
        return currentLocation.getLongitude();
    }

    //Setter
    public void setLocation(Location location)
    {
        currentLocation = location;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.clinic_map, container, false);

        //Initialising Classes that reference this main class
        InitializeUserLocation initializeUserLocation = new InitializeUserLocation(this);
        searchBarExploration = new SearchBarExploration("places.displayName,places.formattedAddress,places.location", 5000, this);
        proximityExploration = new ProximityExploration("places.displayName,places.formattedAddress,places.location", 5000,this);
                ;

        super.onCreate(savedInstanceState);
        // Set the layout file as the content view.

        SupportMapFragment mapFragment = (SupportMapFragment) this.requireActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
        {
            mapFragment.getMapAsync(this);
        }

        initializeUserLocation.getLastLocation();


        // data to populate the RecyclerView with
        searchResults = new ArrayList<>();
        // set up the RecyclerView
        recyclerView = view.findViewById(R.id.searchResultsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MyRecyclerViewAdapter(requireContext(), searchResults);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        //SearchView Initialisation and Input Query
        //Set up the search view
        SearchView searchView = view.findViewById(R.id.mapSearch);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                TextInputAsyncProcess textInputAsyncProcess = new TextInputAsyncProcess(MapClinicFinder.this, newText);
                textInputAsyncProcess.createAsynchronousRunnerProcess(newText);
                return false;
            }
        });

        View findNearbyMedCtrsButton = view.findViewById(R.id.find_nearby_med_ctrs_btn);

        findNearbyMedCtrsButton.setOnClickListener(view -> {
            IconButtonInputAsyncProcess iconButtonInputAsyncProcess = new IconButtonInputAsyncProcess(MapClinicFinder.this, 20);
            iconButtonInputAsyncProcess.createAsynchronousRunnerProcess("hospital");

            Toast.makeText(requireContext(), "Finding Nearby Hospitals", Toast.LENGTH_SHORT).show();
            Toast.makeText(requireContext(), "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        HashMap<String, String> placeClicked = adapter.getmData().get(position);
        String placeName = placeClicked.get("Place Name");
        String latitude = placeClicked.get("Latitude");
        String longitude = placeClicked.get("Longitude");
        String address = placeClicked.get("Address");
        assert latitude != null;
        assert longitude != null;
        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        createMapMarker(myMap, placeName, address, latLng, Colours.HUE_AZURE, 30);
        Toast.makeText(requireContext(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        destination = latLng;

        RouteFinderAsyncProcess routeFinderAsyncProcess = new RouteFinderAsyncProcess(this);
        routeFinderAsyncProcess.createAsynchronousRunnerProcess("routes");
    }

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //Called automatically when the Map Fragment UI is loaded,
        //by Google Maps API itself. So, must set a location before that.
        //Hence, we call getLastLocation() before mapFragment.getMap(async);
        myMap = googleMap;
        if (currentLocation == null) {
            Log.i("Error", "current location is null");
            return;
        }

        LatLng userLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(userLocation).title("You"));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
    }


    protected void createMapMarker(GoogleMap mapData, String placeName, String address, LatLng latLngCoordinates, Colours colours, int zoomFactor)
    {
        float colourValue = ParseBitmapDescriptorIconColours.bitMapMarkerColour(colours);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLngCoordinates);
        markerOptions.title(placeName + " : " + address);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(colourValue));
        mapData.addMarker(markerOptions);
        mapData.moveCamera(CameraUpdateFactory.newLatLng(latLngCoordinates));
        mapData.animateCamera(CameraUpdateFactory.zoomTo(zoomFactor));
    }
}


//Class for setting up the main map layout on startup
class InitializeUserLocation {

    Activity context;
    OnMapReadyCallback mapReadyCallback;
    FusedLocationProviderClient fusedLocationProviderClient;

    MapClinicFinder mapClinicFinder;

    InitializeUserLocation(MapClinicFinder mapClinicFinder)
    {
        this.mapReadyCallback = mapClinicFinder;
        this.context = mapClinicFinder.getActivity();
        assert this.context != null;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.context);
        this.mapClinicFinder = mapClinicFinder;
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void requestNewLocationData() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdateDelayMillis(100)
                .build();

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        mapClinicFinder.setLocation(location);
                        SupportMapFragment mapFragment = (SupportMapFragment) ((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.map);
                        if (mapFragment != null) {
                            mapFragment.getMapAsync(mapReadyCallback);
                        }
                        fusedLocationProviderClient.removeLocationUpdates(this);
                        break;
                    }
                    else {
                        Log.d("Location","Location is null");
                    }
                }
            }
        }, Looper.getMainLooper());
    }

    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int FINE_PERMISSION_CODE = 1;

            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            Log.d("Permissions","Here2"); // Exit method if permission is not granted
        }

        if (!isLocationEnabled(context)) {
            Log.i("Error", "Please Enable Location services");
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                mapClinicFinder.setLocation(location);
                SupportMapFragment mapFragment = (SupportMapFragment) mapClinicFinder.getChildFragmentManager().findFragmentById(R.id.map);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(mapReadyCallback);
                }
                else{
                    Log.d("Null", ",mapFragment is null");
                }
            } else {
                Log.d("Resquesting new data","Requesting new data");
                requestNewLocationData();
            }
        }).addOnFailureListener(e -> {
            Log.e("LocationError", "Failed to get last known location: " + e.getMessage());
            requestNewLocationData();
        });
    }

    public static Boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            // This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }
}

class TextInputAsyncProcess extends HandleAsyncInputProcess {
    MyRecyclerViewAdapter adapter;

    RecyclerView recyclerView;

    MapClinicFinder mapClinicFinder;

    String newText;

    List<HashMap<String, String>> asyncCollectedResults;

    TextInputAsyncProcess(MapClinicFinder mapClinicFinder, String newText)
    {
        super(mapClinicFinder.requireActivity(),100);
        this.mapClinicFinder = mapClinicFinder;
        this.adapter = mapClinicFinder.getAdapter();
        this.recyclerView = mapClinicFinder.getRecyclerView();
        this.newText = newText;
    }

    @Override
    public void outerFunction()
    {

        // Show the RecyclerView
        recyclerView.setVisibility(View.VISIBLE);


        //return mapClinicFinder.prepareDetails(mapClinicFinder.getSearchBarExploration(), inputText);

    }
    @Override
    public void backgroundThreadFunction(String inputText){
        mapClinicFinder.getSearchBarExploration().setPlaceName(inputText);
        mapClinicFinder.getSearchBarExploration().setLongitude(mapClinicFinder.getLongitude());
        mapClinicFinder.getSearchBarExploration().setLatitude(mapClinicFinder.getLatitude());
        mapClinicFinder.getSearchBarExploration().setSearchKey("places");

        asyncCollectedResults = (List<HashMap<String, String>>) mapClinicFinder.getSearchBarExploration().search();

        // Prepare details for the search
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void mainThreadFunction(){
        mapClinicFinder.getSearchResults().clear();
        mapClinicFinder.getSearchResults().addAll(asyncCollectedResults);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void exitFunction(){
        mapClinicFinder.getSearchResults().clear();
        adapter.notifyDataSetChanged();
    }
}

class IconButtonInputAsyncProcess extends HandleAsyncInputProcess{
    MapClinicFinder mapClinicFinder;

    List<HashMap<String, String>> asyncCollectedResults;

    int zoomFactor = 10;

    IconButtonInputAsyncProcess(MapClinicFinder mapClinicFinder, int zoomFactor)
    {
        super(mapClinicFinder.requireActivity(),100);
        this.mapClinicFinder = mapClinicFinder;
        this.zoomFactor = zoomFactor;

    }
    @Override
    public void outerFunction()
    {
        // Prepare details for the search

    }
    @Override
    public void backgroundThreadFunction(String newText){
        mapClinicFinder.getProximityExploration().setPlaceName("hospital");
        mapClinicFinder.getProximityExploration().setLatitude(mapClinicFinder.getLatitude());
        mapClinicFinder.getProximityExploration().setLongitude(mapClinicFinder.getLongitude());
        mapClinicFinder.getProximityExploration().setSearchKey("places");
        asyncCollectedResults = (List<HashMap<String, String>>) mapClinicFinder.getProximityExploration().search();
    }
    @Override
    public void mainThreadFunction(){
        displayNearbyPlaces(asyncCollectedResults);
    }

    @Override
    public void exitFunction(){

    }
    private void displayNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList)
    {
        Log.d("MyList", nearbyPlacesList.toString());
        for(int i = 0; i < nearbyPlacesList.size(); i++)
        {
            HashMap<String, String> googleNearbyPlace = nearbyPlacesList.get(i);
            String nameOfPlace = googleNearbyPlace.get("place_name");
            String vicinity = googleNearbyPlace.get("vicinity");
            double lat = Double.parseDouble(Objects.requireNonNull(googleNearbyPlace.get("lat")));
            double lng = Double.parseDouble(Objects.requireNonNull(googleNearbyPlace.get("lng")));
            LatLng latLng = new LatLng(lat,lng);

            //create the lat-long marker UI
            mapClinicFinder.createMapMarker(mapClinicFinder.getMyMap(), nameOfPlace, vicinity, latLng, Colours.HUE_GREEN, zoomFactor);
        }
    }
}

class RouteFinderAsyncProcess extends HandleAsyncInputProcess{

    MapClinicFinder mapClinicFinder;

    List<JSONArray> results;

    RouteFinderAsyncProcess(MapClinicFinder mapClinicFinder)
    {
        super(mapClinicFinder.requireActivity(), 100);
        this.mapClinicFinder = mapClinicFinder;
    }

    @Override
    public void outerFunction() {
        Log.d("Outer", "Getting routes");
    }

    @Override
    public void backgroundThreadFunction(String inputText) {
        RoutesExploration routesExploration = new RoutesExploration("routes.duration,routes.distanceMeters,routes.polyline.encodedPolyline", mapClinicFinder);
        routesExploration.setTravelMode(TravelModes.DRIVE);
        routesExploration.setSearchKey(inputText);
        LatLng originLoc = new LatLng(mapClinicFinder.getLatitude(), mapClinicFinder.getLongitude());
        routesExploration.setOriginAndDestination(originLoc, mapClinicFinder.destination);

        results = (List<JSONArray>) routesExploration.search();
    }

    @Override
    public void mainThreadFunction() {
        Log.d("Routes",results.toString());
    }

    @Override
    public void exitFunction() {

    }
}




