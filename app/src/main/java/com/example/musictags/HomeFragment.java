package com.example.musictags;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.location.Location;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.concurrent.Executor;


public class HomeFragment extends Fragment implements View.OnClickListener {



    public HomeFragment() {
        // Required empty public constructor
    }

    private static GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient; //Save the instance
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 7;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View homeV = inflater.inflate(R.layout.fragment_home, container, false);

        ImageButton upVote = (ImageButton) homeV.findViewById(R.id.upVoteButton);
        upVote.setOnClickListener(this);
        ImageButton downVote = (ImageButton) homeV.findViewById(R.id.downVoteButton);
        downVote.setOnClickListener(this);
        ImageButton nextSong = (ImageButton) homeV.findViewById(R.id.nextSongButton);
        nextSong.setOnClickListener(this);
        ImageButton lastSong = (ImageButton) homeV.findViewById(R.id.lastSongButton);
        lastSong.setOnClickListener(this);
        ImageButton play = (ImageButton) homeV.findViewById(R.id.playButton);
        play.setOnClickListener(this);
        ImageButton pin = (ImageButton) homeV.findViewById(R.id.pin);
        pin.setOnClickListener(this);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragment_map);

//        mapFragment.getMapAsync(googleMap -> {
//            displayMyLocation();
//        });

        //returns layout for this fragment
        return homeV;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upVoteButton:
                //TODO up vote code
                break;
            case R.id.downVoteButton:
                //TODO down vote code
                break;
            case R.id.playButton:
                if(MainActivity.isPaused){
                    MainActivity.resume();
                }else{
                    MainActivity.pause();
                }
                break;
            case R.id.lastSongButton:
                MainActivity.skipPrevious();
                break;

            case R.id.nextSongButton:
                MainActivity.skip();
                break;
            case R.id.pin:
                displayMyLocation();
            default:
                break;
        }
    }


    private void displayMyLocation() {
        //check if permission is granted
        int permission = ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        //If not, ask for it
        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        else {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(this.getActivity(), task -> {
                        Location mLastKnownLocation = task.getResult();
                        if (task.isSuccessful() && mLastKnownLocation != null) {

                            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
                            mapFragment.getMapAsync(googleMap -> {
                                mMap = googleMap;

                                mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude())).title("Current Location"));
                                displayMyLocation();
                            });
                        }
                    });
        }
    }



    /**
     * Handles the result of the request for location permissions.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            //if request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayMyLocation();
            }
        }

    }

}