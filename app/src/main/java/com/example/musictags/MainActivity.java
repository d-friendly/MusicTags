package com.example.musictags;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private NavigationBarView bottomNavigationView;
    LocationManager locationManager;
    LocationListener locationListener;

    private static final String CLIENT_ID = "10ee2098620d4a0b8fde685d19d8a0ab";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    //private static final String REDIRECT_URI = "http://com.yourdomain.musictags/callback;
    private SpotifyAppRemote mSpotifyAppRemote;
    private static GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient; //Save the instance
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Set the connection parameters
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        //Spotify app remote created. Use throughout app
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        //connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });

       // mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnItemSelectedListener(bottomnavFunction);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateLocationInfo(location);
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle){

            }
            @Override
            public void onProviderEnabled(String s){

            }
            @Override
            public void onProviderDisabled(String s){

            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    updateLocationInfo(location);
                }
            }
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();



    }

    public void startListening() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if ((grantResults.length >= 0) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }

    public void updateLocationInfo(Location location) {
        Log.i("LocationInfo", location.toString());

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try{
            String addy = "Could not find address";
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (listAddresses != null && listAddresses.size() > 0) {


                Log.i("PlaceInfo", listAddresses.get(0).toString());
                addy = "Address: \n";
                if(listAddresses.get(0).getSubThoroughfare() != null){
                    addy += listAddresses.get(0).getSubThoroughfare() + " ";
                }
                if(listAddresses.get(0).getThoroughfare() != null){
                    addy += listAddresses.get(0).getThoroughfare() + "\n";
                }
                if(listAddresses.get(0).getLocality() != null){
                    addy += listAddresses.get(0).getLocality() + "\n";
                }
                if(listAddresses.get(0).getPostalCode() != null){
                    addy += listAddresses.get(0).getPostalCode() + "\n";
                }
                if(listAddresses.get(0).getCountryName() != null){
                    addy += listAddresses.get(0).getCountryName() + "\n";
                }
            }

            //TextView address = (TextView) findViewById(R.id.textView2);
            //address.setText(addy);
            Log.println(Log.ASSERT,"TRY",addy);
            //
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void displayMyLocation() {
//        //check if permission is granted
//        int permission = ActivityCompat.checkSelfPermission(this.getApplicationContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION);
//        //If not, ask for it
//        if (permission == PackageManager.PERMISSION_DENIED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//
//        else {
//            mFusedLocationProviderClient.getLastLocation()
//                    .addOnCompleteListener(this, task -> {
//                        Location mLastKnownLocation = task.getResult();
//                        if (task.isSuccessful() && mLastKnownLocation != null) {
//
//                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
//                            mapFragment.getMapAsync(googleMap -> {
//                                mMap = googleMap;
//
//                                mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),
//                                        mLastKnownLocation.getLongitude())).title("Current Location"));
//                                displayMyLocation();
//                            });
//                        }
//
//                    });
//        }
//
//
//    }

    private NavigationBarView.OnItemSelectedListener bottomnavFunction = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch(item.getItemId()) {
                case R.id.home:
                    fragment = new HomeFragment();
                    break;
                case R.id.search:
                    fragment= new SearchFragment();
                    break;
                case R.id.queue:
                    fragment = new QueueFragment();
                    break;
                case R.id.settings:
                    fragment = new SettingsFragment();
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            return true;
        }
    };
}