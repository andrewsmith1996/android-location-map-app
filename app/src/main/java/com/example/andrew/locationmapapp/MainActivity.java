package com.example.andrew.locationmapapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.util.Log;

import java.util.Locale;
import android.net.Uri;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


// TODO add searching via textbox
// TODO add permissions toggle switch
// TODO add stop updating toggle switch

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;

    // Set the latitude and longitude values
    public Double longVal = 0.0;
    public Double latVal = 0.0;

    // Updating location variables
    public boolean mRequestingLocationUpdates = true;
    private LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Disable the map button
        Button mapButton = (Button)findViewById(R.id.mapButton);
        mapButton.setEnabled(false);

        // Start location callback for updating the location
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update the text with the new location
                    updateText(location);
                }
            };
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        // On resume of app begin updating the location again
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // On pause stop updating the location
        stopLocationUpdates();
    }

    // Stops the location updates
    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    // Begins the locaation updates
    private void startLocationUpdates() {

        // Begin updating the location
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    // Function to begin getting the use location
    public void onLocationButtonClick(View view){

        // Check for permissions
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

    }

    public void getLocation(){

        // Get the last known location of the user
        mFusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                @Override
                public void onSuccess(Location location) {

                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {

                        updateText(location);

                        // Re enable the view on map button
                        Button mapButton = (Button)findViewById(R.id.mapButton);
                        mapButton.setEnabled(true);
                    }
                }
            });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        // If we have all the correct permissions then get the user location
                        this.getLocation();

                } else {

                    // If the user has denied the permissions then show an alert dialog

                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Permissions needed");
                    alertDialog.setMessage("You need to enable permissions to use this app");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        }
    }

    public void viewOnMap(View view){

        // Intent for Google Maps, if another map app handles the geo tag it may show more than one app option
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latVal, longVal);
        Intent intent_map = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent_map);
    }

    public void updateText(Location location){

        longVal = location.getLongitude();
        latVal = location.getLatitude();

        // Get the text view to change the text of
        TextView locationText = (TextView)findViewById(R.id.locationText);

        // Set the text of the view
        locationText.setText("Longitude: " + Double.toString(longVal).format("%.2f", longVal) + ", Latitude: " + Double.toString(latVal).format("%.2f", latVal));

    }

}
