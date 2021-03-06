package com.fixitup.cs5551.fixitupapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.location.LocationListener;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.events.EventHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*
Location List:

Plaza: 39.099790, -94.578560
Plaza Apartment Center, 39.040010,-94.595630
UMKC: 39.035790, -94.577890
*/
//Besides the default OnMapReadyCallback, we need to implement GoogleApiClient interfaces in order to update the map
//more frequently (make it more continuous and in-motion, not statically working whenever a function is called)
public class TechnicianMapActivity extends FragmentActivity implements OnMapReadyCallback, FetchAddressTask.OnTaskCompleted {
    private GoogleMap mMap;

    private Button mLogout, mSettings, mNotification;
    private boolean LoggedOut, sessionStarted;

    private Marker repairMarker;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String TAG = TechnicianMapActivity.class.getSimpleName();
    boolean mLocationPermissionGranted;
    private final LatLng mDefaultLocation = new LatLng(88.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Location mLastKnownLocation;
    private LocationCallback mLocationCallback;
    //In order for LocationRequest to work, we need to install the latest version of Google Play Service (dependencies tab in Project Structure)
    LocationRequest mLocationRequest;
    private Marker currentLocationMarker;
    // Constants
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_PICK_PLACE = 2;
    private static final String TRACKING_LOCATION_KEY = "tracking_location";
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1200);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    //Declare customerID
    private String customerID = "", lastCustomerID;
    private String userID, orderID = null;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_map);

        mLogout = (Button) findViewById(R.id.logout);
        mSettings = (Button) findViewById(R.id.settings);
        mNotification = (Button) findViewById(R.id.notification);

        sessionStarted = false;
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

                //Fetch new Address, Display the current location in real time, move marker, and update Firebase
                new FetchAddressTask(TechnicianMapActivity.this, TechnicianMapActivity.this)
                        .execute(locationResult.getLastLocation());
                mLastKnownLocation=locationResult.getLastLocation();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude() )));
                if (currentLocationMarker != null) {
                    currentLocationMarker.remove();
                    currentLocationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                            .title("Current Location"));
                    currentLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.mappin_icon));
                    currentLocationMarker.setSnippet("Latitude: " + mLastKnownLocation.getLatitude() + ", Longitude:" + mLastKnownLocation.getLongitude());
                }

                //Update Address to GeoFire

                userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference availableRef = FirebaseDatabase.getInstance().getReference("TechnicianAvailable");
                DatabaseReference busyRef = FirebaseDatabase.getInstance().getReference("TechnicianBusy");

                GeoFire geoFireAvailable = new GeoFire(availableRef);
                GeoFire geoFireBusy = new GeoFire(busyRef);

                //Check whether this technician has been assigned a customer or not, update the corresponding location tables
                switch (customerID){
                    case "":
                        //Technician is free (not assigned)
                        geoFireBusy.removeLocation(userID, new GeoFire.CompletionListener(){
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                //Do some stuff if you want to
                            }
                        });
                        geoFireAvailable.setLocation(userID, new GeoLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), new GeoFire.CompletionListener(){
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                //Do some stuff if you want to
                            }
                        });
                        break;
                    default:
                        //Technician is now assigned
                        lastCustomerID=customerID; //for rating
                        geoFireAvailable.removeLocation(userID, new GeoFire.CompletionListener(){
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                //Do some stuff if you want to
                            }
                        });
                        geoFireBusy.setLocation(userID, new GeoLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), new GeoFire.CompletionListener(){
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                //Do some stuff if you want to
                            }
                        });
                        //Add location to display distance
                        Location loc1 = new Location("");
                        loc1.setLatitude(mLastKnownLocation.getLatitude());
                        loc1.setLongitude(mLastKnownLocation.getLongitude());

                        Location loc2 = new Location("");
                        loc2.setLatitude(customerLatLng.latitude);
                        loc2.setLongitude(customerLatLng.longitude);
                        //Notification that technician is nearby
                        //Calculate distance
                        float distance = loc1.distanceTo(loc2);
                        if (!sessionStarted){
                            if (distance <15){
                                mNotification.setVisibility(View.VISIBLE);
                                mNotification.setText("You are here! Click to start Session!");
                            }
                            else if (distance <100){
                                mNotification.setVisibility(View.VISIBLE);
                                mNotification.setText("You are almost here (within 100m)! Please be prepared!");
                            }
                            else if (distance < 200){
                                mNotification.setVisibility(View.VISIBLE);
                                mNotification.setText("You are nearby (within 200m)! Please be prepared!");
                            }
                            else{
                                mNotification.setVisibility(View.INVISIBLE);
                                mNotification.setText("Customer is at the location (" + String.valueOf(distance) + " meters) from you");
                            }
                            ;
                            break;
                        }

                }

            }
        };
        //Create Logout button and its behavior

        LoggedOut = false;
        mLogout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //Delete current customerID;
                customerID="";
                getAssignedCustomer(); //resets customerID in database
                //Delete location from current available list
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TechnicianAvailable");
                GeoFire geoFire = new GeoFire(ref);
                geoFire.removeLocation(userID, new GeoFire.CompletionListener(){
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        //Do some stuff if you want to
                    }
                });
                //Clear up technician in busy queue
                ref = FirebaseDatabase.getInstance().getReference("TechnicianBusy");
                geoFire = new GeoFire(ref);
                geoFire.removeLocation(userID, new GeoFire.CompletionListener(){
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        //Do some stuff if you want to
                    }
                });
                DatabaseReference assignedCustomerRef= FirebaseDatabase.getInstance().getReference().child("Users").child("Technicians").child(userID).child("requestCustomerID");
                if (assignedCustomerRef != null){
                    assignedCustomerRef.removeValue();
                }
                FirebaseAuth.getInstance().signOut();
                LoggedOut=true;

                Intent intent = new Intent(TechnicianMapActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TechnicianMapActivity.this);
                builder.setMessage(R.string.warning_msg)
                        .setPositiveButton("Yes, I Understand!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(TechnicianMapActivity.this, TechnicianHome.class);
                                startActivity(intent);
                                //finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                builder.show();

            }
        });

        mNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To Start a session
                if (!sessionStarted){
                    sessionStarted=true;
                    mNotification.setVisibility(View.VISIBLE);
                    mNotification.setText("Session is Active!\nClick to End Session");
                    //Add order details
                    Order order = new Order (userID, customerID, "ongoing");
                    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders").push();
                    orderRef.setValue(order);
                    orderID = orderRef.getKey().toString();
                    DatabaseReference customerRef= FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerID);
                    customerRef.child("orderID").setValue(orderID);
                    
                }

                //To finish a session
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TechnicianMapActivity.this);
                    builder.setMessage(R.string.warning_msg4)
                            .setPositiveButton("Yes, I do!", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    sessionStarted=false;
                                    mNotification.setVisibility(View.GONE);
                                    mNotification.setText("");
                                    //Add Order Details to Firebase
                                    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders").child(orderID);
                                    orderRef.child("status").setValue("completed");


                                    //Delete customerRequest
                                    DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerID);
                                    requestRef.removeValue();

                                    AlertDialog.Builder builder = new AlertDialog.Builder(TechnicianMapActivity.this);
                                    builder.setMessage("Would you like to rate this customer?")
                                            .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent intent = new Intent(TechnicianMapActivity.this, TechnicianRatingActivity.class);
                                                    intent.putExtra("cID", lastCustomerID);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .setNegativeButton("No!", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                }
                                            });
                                    builder.show();
                                    //Delete requestcustomerID
                                    DatabaseReference requestCustID= FirebaseDatabase.getInstance().getReference().child("Users").child("Technicians").child(userID).child("requestCustomerID");
                                    requestCustID.removeValue();


                                    //remove markers on the map
                                    if (repairMarker!=null){
                                        repairMarker.remove();
                                    }
                                    if (repairLocationRef != null){
                                        repairLocationRef.removeEventListener(repairLocationRefListener);
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    builder.show();

                }
            }
        });
        getAssignedCustomer();


    }
    private void getAssignedCustomer(){
        String technicianID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference assignedCustomerRef= FirebaseDatabase.getInstance().getReference().child("Users").child("Technicians").child(technicianID).child("requestCustomerID");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //If a customer id is found
                if (dataSnapshot.exists()){
                    customerID = dataSnapshot.getValue().toString();
                    getRepairLocation();
                }
                //if the customer cancels the request
                else {
                    customerID = "";
                    sessionStarted = false;
                    mNotification.setText("");
                    mNotification.setVisibility(View.INVISIBLE);
                    /*if (orderID != null){
                        DatabaseReference orderRef= FirebaseDatabase.getInstance().getReference().child("Orders").child(orderID);
                        orderRef.removeValue();
                    }*/
                    if (repairMarker!=null){
                        repairMarker.remove();
                    }
                    if (repairLocationRef != null){
                        repairLocationRef.removeEventListener(repairLocationRefListener);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    private DatabaseReference repairLocationRef;
    private ValueEventListener repairLocationRefListener;
    //Assign local variables global so that these can be canceled later
    private LatLng customerLatLng;
    private void getRepairLocation(){
        repairLocationRef= FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerID).child("l");
        repairLocationRefListener = repairLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Check for validity of condition
                if (dataSnapshot.exists() && !customerID.equals("")){
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
                    customerLatLng = new LatLng(locationLat, locationLng);
                    //To do: Should assign it to a marker variable
                    repairMarker = mMap.addMarker(new MarkerOptions().position(customerLatLng)
                            .title("Repair Location"));
                    repairMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.repair_icon));
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

        //Based on the provided location permission
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
                                currentLocationMarker=mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude() ))
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
        //Update Address to GeoFire
        if (LoggedOut == false){
            customerID="";
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TechnicianAvailable");

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


