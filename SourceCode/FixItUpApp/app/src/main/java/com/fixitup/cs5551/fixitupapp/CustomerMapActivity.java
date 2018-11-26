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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback, FetchAddressTask.OnTaskCompleted {
    private GoogleMap mMap;

    private Button mLogout, mRequest, mCancel, mSettings;
    private Boolean requestInitiated = false;

    private boolean LoggedOut;

    private LatLng repairLocation;
    private Marker repairMarker = null;

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
        //Initialize All Buttons
        mLogout = (Button) findViewById(R.id.logout);
        mRequest = (Button) findViewById(R.id.request);
        mCancel = (Button) findViewById(R.id.cancel);
        mSettings = (Button) findViewById(R.id.settings);

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
                    currentLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.mappin_icon));
                    currentLocationMarker.setSnippet("Latitude: " + mLastKnownLocation.getLatitude() + ", Longitude:" + mLastKnownLocation.getLongitude());
                }
            }
        };
        //Create Logout Button
        LoggedOut = false;

        mLogout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                LoggedOut = true;
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");

                GeoFire geoFire = new GeoFire(ref);
                geoFire.removeLocation(userID, new GeoFire.CompletionListener(){
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        //Do some stuff if you want to
                    }
                });
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomerMapActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mRequest.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (!requestInitiated){
                    requestInitiated = true; //for validation purposes
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
                    repairMarker = mMap.addMarker(new MarkerOptions().position(repairLocation)
                            .title("Repair Location"));
                    repairMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.repair_icon));
                    mRequest.setText("Sending request to Technicians...");
                    //Search for closest technician
                    getClosestTechnician();
                    mCancel.setVisibility(View.VISIBLE);
                }
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestInitiated = false;
                geoQuery.removeAllListeners();
                technicianRef.removeEventListener(technicianLocationRefListener);

                if (foundTechnicianID != null){
                    DatabaseReference technicianRef= FirebaseDatabase.getInstance().getReference().child("Users").child("Technicians").child(foundTechnicianID).child("requestCustomerID");
                    technicianRef.removeValue();
                    foundTechnicianID = null;
                }
                technicianFound = false;
                radius = 1; //reset diameter

                if (repairMarker != null){
                    repairMarker.remove();
                }
                if (mTechnicianMarker != null){
                    mTechnicianMarker.remove();
                }
                //Remove current customer's repair location out of the database
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                GeoFire geoFire = new GeoFire(ref);
                geoFire.removeLocation(userID, new GeoFire.CompletionListener(){
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        //Do some stuff if you want to
                    }
                });


                mRequest.setText("Request a Technician");
                mCancel.setVisibility(View.INVISIBLE);
            }
        });
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerMapActivity.this, CustomerHome.class);
                startActivity(intent);
                finish();
            }
        });
    }
    //Radius is 1 km
    private int radius = 1;
    private Boolean technicianFound = false;
    private String foundTechnicianID;
    //Make geoQuery a global variable so that we can cancel its listener
    GeoQuery geoQuery;
    private void getClosestTechnician(){
        final DatabaseReference technicianLocation = FirebaseDatabase.getInstance().getReference().child("TechnicianAvailable");
        GeoFire geoFire = new GeoFire(technicianLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(repairLocation.latitude, repairLocation.longitude), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!technicianFound && requestInitiated){
                    technicianFound = true;
                    foundTechnicianID = key;
                    DatabaseReference technicianRef= FirebaseDatabase.getInstance().getReference().child("Users").child("Technicians").child(foundTechnicianID);
                    String customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HashMap map = new HashMap();
                    //Assign the repair ID to the particular customer that made the request
                    map.put("requestCustomerID", customerID);
                    technicianRef.updateChildren(map);

                    //pass the technician's location to the customer
                    getTechnicianLocation();
                    mRequest.setText("A technician has been selected! Fetching location...");
                }



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
    //This is used to display the technician's current location
    Marker mTechnicianMarker;
    //Move Database Reference and Reference listener outside so that it can be cancelled later
    DatabaseReference technicianRef;
    ValueEventListener technicianLocationRefListener;
    private void getTechnicianLocation(){
        technicianRef= FirebaseDatabase.getInstance().getReference().child("TechnicianBusy").child(foundTechnicianID).child("l");
        technicianLocationRefListener = technicianRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && requestInitiated){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if (map.get(0) != null){
                        //Convert Longitude and Latitude from object list's string
                        locationLat = Double.parseDouble(map.get(0).toString());

                    }
                    if (map.get(1) != null){
                        //Convert Longitude and Latitude from object list's string
                        locationLng = Double.parseDouble(map.get(1).toString());

                    }
                    LatLng technicianLatLng = new LatLng(locationLat, locationLng);
                    if (mTechnicianMarker != null){
                        mTechnicianMarker.remove();
                    }
                    //Add location to display distance
                    Location loc1 = new Location("");
                    loc1.setLatitude(repairLocation.latitude);
                    loc1.setLongitude(repairLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(technicianLatLng.latitude);
                    loc2.setLongitude(technicianLatLng.longitude);
                    //Notification that technician is nearby
                    //Calculate distance
                    float distance = loc1.distanceTo(loc2);
                    if (distance < 200){
                        mRequest.setText("Technician is nearby (within 200m)!Please be prepared!");
                    }
                    else if (distance <100){
                        mRequest.setText("Technician is almost here (within 100m)! Please be prepared!");
                    }
                    else{
                        mRequest.setText("Technician Found at Location (" + String.valueOf(distance) + " meters) from you");
                    }
                    ;
                    //Add marker to map to show technician's current location
                    mTechnicianMarker = mMap.addMarker(new MarkerOptions().position(technicianLatLng).title("Currently Selected Technician"));
                    mTechnicianMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.technician_icon));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                                currentLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.mappin_icon));
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
        //We don't need real time update of Customers
        //Update Address to GeoFire
        if (LoggedOut == false){
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");

            GeoFire geoFire = new GeoFire(ref);
            geoFire.removeLocation(userID, new GeoFire.CompletionListener(){
                @Override
                public void onComplete(String key, DatabaseError error) {
                    //Do some stuff if you want to
                }
            });
        }

    }
}



