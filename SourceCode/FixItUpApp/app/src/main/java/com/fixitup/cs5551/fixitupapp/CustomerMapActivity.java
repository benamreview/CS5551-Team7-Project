package com.fixitup.cs5551.fixitupapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback, FetchAddressTask.OnTaskCompleted {
    private GoogleMap mMap;
    private Button mLogout, mRequest;
    private LatLng repairLocation;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String TAG = TechnicianMapActivity.class.getSimpleName();
    boolean mLocationPermissionGranted;
    private final LatLng mDefaultLocation = new LatLng(88.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Location mLastKnownLocation;
    private LocationCallback mLocationCallback;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    //In order for LocationRequest to work, we need to install the latest version of Google Play Service (dependencies tab in Project Structure)
    LocationRequest mLocationRequest;
    private Marker currentLocationMarker;
    // Constants
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_PICK_PLACE = 2;
    private static final String TRACKING_LOCATION_KEY = "tracking_location";
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Construct a FusedLocationProviderClient.
        // The LocationServices interface is responsible for returning the current location of the device
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //To-be implemented: Periodic Update
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                new FetchAddressTask(CustomerMapActivity.this, CustomerMapActivity.this)
                        .execute(locationResult.getLastLocation());
                mLastKnownLocation=locationResult.getLastLocation();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude() )));
                if (currentLocationMarker != null) {
                    Log.e("currentLocation", "not null");
                    currentLocationMarker.remove();
                    currentLocationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                            .title("Current Location"));
                    currentLocationMarker.setSnippet("Latitude: " + mLastKnownLocation.getLatitude() + ", Longitude:" + mLastKnownLocation.getLongitude());
                }
                /* GeoFire - update real time location of customer is not needed
                //Update Address to GeoFire


                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TechnicianAvailable");

                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userID, new GeoLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), new GeoFire.CompletionListener(){
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        //Do some stuff if you want to
                    }
                });
                */
            }
        };
        //Create Logout Button
        mLogout = (Button) findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomerMapActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        mRequest = (Button) findViewById(R.id.request);
        mRequest.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
               String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
               DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
               GeoFire geoFire = new GeoFire(ref);
               //getDeviceLocation();
                geoFire.setLocation(userID, new GeoLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), new GeoFire.CompletionListener(){
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        //Do some stuff if you want to
                    }
                });
                repairLocation = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(repairLocation)
                        .title("Repair Location"));
                mRequest.setText("Sending request to Technicians...");
            }
        });

        //Search for closest technician
        //getClosestTechnician();
    }
    //Radius is 1km
    private int radius = 1;
    private Boolean technicianFound = false;
    private String foundTechnicianID;
    private void getClosestTechnician(){
        final DatabaseReference technicianLocation = FirebaseDatabase.getInstance().getReference().child("TechnicianAvailable");
        GeoFire geoFire = new GeoFire(technicianLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(repairLocation.latitude, repairLocation.longitude), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                technicianFound = true;
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }
            //This function will be called as soon as the current query finishes with all the results
            //within the radius
            @Override
            public void onGeoQueryReady() {
                if (!technicianFound){
                    radius++;
                    getClosestTechnician();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady (GoogleMap googleMap){
        mMap = googleMap;

        // Prompt the user for permission. If permission is denied, no location is provided
        getLocationPermission();

        //Based on the provided location permision
        //,turn on (or off) the My Location layer and the related control on the map.
        updateLocationUI();
        //This will be called to move the camera to its initial position
        getDeviceLocation();

        //Based on the provided location permission,
        // Get the current location of the device and set the position of the map.
        startTrackingLocation();

    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        /*There are many variables for location such as ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, ... */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        //Update the location accordingly. If permission is not granted, location is not displayed
        updateLocationUI();
    }
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                //Update the location accordingly. If permission is not granted, location is not displayed
                //and last known location will be set to null


                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationProviderClient.requestLocationUpdates
                    (getLocationRequest(),
                            mLocationCallback,
                            null /* Looper */);
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            /*I modified this portion of the method because there is a tricky case
                             * such that even the FusedLocationProviderClient's getLastLocation was successful,
                             * the mLastKnownLocation is set to null for some reason. As a result,
                             * the getLatitude() and getLongitude() will crash the app due to errors from trying to
                             * access a null value*/
                            if (mLastKnownLocation == null) {
                                Log.d(TAG, "Current location is null. Using defaults.");
                                Log.e(TAG, "Exception: %s", task.getException());
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            } else {
                                // Set the map's camera position to the current location of the device.

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                currentLocationMarker= mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude() ))
                                        .title("Current Location"));
                                currentLocationMarker.setSnippet("Latitude: " + mLastKnownLocation.getLatitude() + ", Longitude:" + mLastKnownLocation.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude() )));


                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        stopTrackingLocation();
    }
    @Override
    protected void onResume() {
        startTrackingLocation();
        super.onResume();
    }
    /**
     * Method that stops tracking the device. It removes the location
     * updates, stops the animation and reset the UI.
     */
    private void stopTrackingLocation() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }
    @Override
    public void onTaskCompleted(String result) {

    }
    @Override
    protected void onStop() {
        super.onStop();
        /* We don't need real time update of Customers
        //Update Address to GeoFire

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TechnicianAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userID, new GeoFire.CompletionListener(){
            @Override
            public void onComplete(String key, DatabaseError error) {
                //Do some stuff if you want to
            }
        });*/
    }
}



