package com.example.busapp.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;

import com.example.busapp.MainActivity;
import com.example.busapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.sucho.placepicker.AddressData;
import com.sucho.placepicker.Constants;
import com.sucho.placepicker.MapType;
import com.sucho.placepicker.PlacePicker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class HomeFragment extends Fragment {


    private HomeViewModel homeViewModel;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    // A default location (Volos, Greece) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(39.366498, 22.950852);
    private static final String TAG = MainActivity.class.getSimpleName();
    static final String EXTRA_MESSAGE = "com.example.busapp.MESSAGE";
    private String GOOGLE_API_KEY = "AIzaSyBq0cTUyR_j53H6RPo9Lcg6J9CAsl-1Uls" ;

    // The entry point to the Places API.
    private PlacesClient mPlacesClient;
    private String MyLocationString;
    private String SelectInMapLocationString;
    int PLACE_PICKER_REQUEST=1;
    int RESULT_OK = 1;
    private boolean flag_start_set = false;
    private boolean flag_end_set = false;
    private String message = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        //here we retrieve the ids for the layout
        ImageButton selectInMap = root.findViewById(R.id.imageselectinthemap);
        TextView textSelectInMap = root.findViewById(R.id.selectinthemap);
        ImageButton myLocation = root.findViewById(R.id.imageselectmylocation);
        TextView textMyLocation = root.findViewById(R.id.selectmylocation);
        ImageButton findRoute = root.findViewById(R.id.buttontogo);


        selectInMap.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            //System.out.println("Everything is ok!");
            Intent intent = new PlacePicker.IntentBuilder()
                    .setLatLong(39.366498, 22.950852)  // Initial Latitude and Longitude the Map will load into
                    .showLatLong(true)  // Show Coordinates in the Activity
                    .setMapZoom(12.0f)  // Map Zoom Level. Default: 14.0
                    .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
                    .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
                    .setMarkerImageImageColor(R.color.colorPrimary)
                    .setFabColor(R.color.colorPrimary)
                    .setPrimaryTextColor(R.color.colorPrimary) // Change text color of Shortened Address
                    .setSecondaryTextColor(R.color.colorDefault) // Change text color of full Address
                    .setBottomViewColor(R.color.colorPrimaryDark) // Change Address View Background Color (Default: White)
                    .setMapType(MapType.NORMAL)
                    .setPlaceSearchBar(true, GOOGLE_API_KEY) //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
                    .onlyCoordinates(true)  //Get only Coordinates from Place Picker
                    .build(getActivity());
            startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);
        });

        textSelectInMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //System.out.println("Everything is ok!");
                Intent intent = new PlacePicker.IntentBuilder()
                        .setLatLong(39.366498, 22.950852)  // Initial Latitude and Longitude the Map will load into
                        .showLatLong(true)  // Show Coordinates in the Activity
                        .setMapZoom(15.0f)  // Map Zoom Level. Default: 14.0
                        .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
                        .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
                        .setMarkerImageImageColor(R.color.colorPrimary)
                        .setFabColor(R.color.colorPrimary)
                        .setPlaceSearchBar(true,GOOGLE_API_KEY)
                        .setPrimaryTextColor(R.color.colorPrimary) // Change text color of Shortened Address
                        .setSecondaryTextColor(R.color.colorAccent) // Change text color of full Address
                        .setBottomViewColor(R.color.colorPrimaryDark) // Change Address View Background Color (Default: White)
                        .setMapType(MapType.NORMAL)
                        //.setPlaceSearchBar(true, GOOGLE_API_KEY) //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
                        .onlyCoordinates(true)  //Get only Coordinates from Place Picker
                        .build(getActivity());
                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);
            }
        });

        myLocation.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if(!flag_start_set) {
                    getDeviceLocationAndAdress();
                    SearchView searchViewOfMine = root.findViewById(R.id.searchView);
                    searchViewOfMine.setQuery(MyLocationString, true);
                    flag_start_set = true;
                }
                else{
                    getDeviceLocationAndAdress();
                    SearchView searchViewOfMine = root.findViewById(R.id.searchView3);
                    searchViewOfMine.setQuery(MyLocationString, true);
                    flag_end_set = true;
                }
            }
        });

        textMyLocation.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if(!flag_start_set) {
                    getDeviceLocationAndAdress();
                    SearchView searchViewOfMine = root.findViewById(R.id.searchView);
                    searchViewOfMine.setQuery(MyLocationString, true);
                    flag_start_set = true;
                }
                else{
                    getDeviceLocationAndAdress();
                    SearchView searchViewOfMine = root.findViewById(R.id.searchView3);
                    searchViewOfMine.setQuery(MyLocationString, true);
                    flag_end_set = true;
                }

            }
        });

        findRoute.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(),ResultActivity.class);
            intent.putExtra(EXTRA_MESSAGE,message);
            startActivity(intent);
        });

        return root;
    }

    //this activity displays the map and allow the user to select a place in the map
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
                assert addressData != null;
                double lat = addressData.component1();
                double longt = addressData.component2();
                message = message + lat + " " + longt + " ";
                getAddress(lat,longt,1);
                if(!flag_start_set) {
                    SearchView searchViewOfSelected = Objects.requireNonNull(getActivity()).findViewById(R.id.searchView);
                    searchViewOfSelected.setQuery(SelectInMapLocationString, true);
                    flag_start_set = true;
                }
                else{
                    SearchView searchViewOfSelected = Objects.requireNonNull(getActivity()).findViewById(R.id.searchView3);
                    searchViewOfSelected.setQuery(SelectInMapLocationString, true);
                    flag_end_set = true;
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getDeviceLocationAndAdress(){

        // Construct a PlacesClient
        Places.initialize(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.google_maps_key));
        mPlacesClient = Places.createClient(getActivity().getApplicationContext());

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Prompt the user for permission.
        getLocationPermission();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }


    //Prompts the user for permission to use the device location.
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()).getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    //Gets the current location of the device, and positions the map's camera.
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getDeviceLocation() {
         // Get the best and most recent location of the device, which may be null in rare
         // cases when a location is not available.
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            getAddress(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude(),0);
                            message = message + mLastKnownLocation.getLatitude() + " " + mLastKnownLocation.getLongitude() + " ";

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
    }

     //Handles the result of the request for location permissions.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getAddress(double lat, double lng, int flag) {
        Geocoder geocoder = new Geocoder(Objects.requireNonNull(getActivity()).getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();
            if(flag == 1){
                SelectInMapLocationString = obj.getAddressLine(0);
            }
            else{
                MyLocationString = obj.getAddressLine(0);
            }
            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

