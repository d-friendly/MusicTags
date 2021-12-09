package com.example.musictags;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.location.Location;
import android.os.Bundle;

import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;


public class HomeFragment extends Fragment implements View.OnClickListener {



    public HomeFragment() {
        // Required empty public constructor
    }

    private static GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient; //Save the instance
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 7;

    //CHECK WITH DYLAN ABOUT THIS HERE VS MAIN
    //FirebaseFirestore db = FirebaseFirestore.getInstance();


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

        mapFragment.getMapAsync(googleMap -> {
           // displayMyLocation();
        });

        //Log.println(Log.ASSERT, "permission", isLocationEnabled());



        //returns layout for this fragment
        return homeV;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upVoteButton:
                //TODO up vote code
                updateVote();
                break;
            case R.id.downVoteButton:
                //TODO down vote code
                DBTrackNode dbTN;
                //get current DBTrackNode
                //updateVote(dbTN);
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
                sendTag("Test");
                //SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                //        .findFragmentById(R.id.fragment_map);
                //mapFragment.getMapAsync(googleMap -> {
                //todo put all map api stuff on own thread
                    displayMyLocation();
                //});

            default:
                break;
        }
    }




    //TODO
    //decide if this is displaying only the 'playing queue of songs'/ close ones
    // or renders based on screen location*
    private void displayMyLocation() {
        //Log.println(Log.ASSERT, "are we here", "in displayMyLocation()");
        //check if permission is granted
        int permission = ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        //If not, ask for it
        if (permission == PackageManager.PERMISSION_DENIED) {
            Log.println(Log.ASSERT, "Permission was denied", "denied");
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        else {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(this.getActivity(), task -> {
                        Location mLastKnownLocation = task.getResult();
                        //Log.println(Log.ASSERT, "else statement", mLastKnownLocation.toString());
                        if (task.isSuccessful() && mLastKnownLocation != null) {

                            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
                            mapFragment.getMapAsync(googleMap -> {
                                mMap = googleMap;

                                mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude())).title("Listening Here"));
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

    // method to check
    // if location is enabled
//    private String isLocationEnabled() {
//        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
//            return "True";
//        }
//        return "False";
//    }




    //sends DBTrackNodes to cloud firestore
    public void sendTag(String msg) {

    //updates Tags vote count


        //if per tag
            // DocumentReference name_of_reference = db.collection("tags").document("doc name");
            //name_of_reference.update("upvote", true)
            //.addOnSucess


        Artist artist = new Artist("D Smoke", "spotify:track:1icmxr6OxT03H4dHGOiLFX");
        List<Artist> artists = new ArrayList<Artist>();
        artists.add(artist);
        Album album = new Album("D Smoke", "spotify:track:1icmxr6OxT03H4dHGOiLFX");
        long duration = 239000;
        String name = "D Smoke";
        String uri = "spotify:track:1icmxr6OxT03H4dHGOiLFX";
        ImageUri iURI = new ImageUri("https://images.complex.com/complex/images/c_fill,dpr_auto,f_auto,q_auto,w_1400/fl_lossy,pg_1/hcjrqlvc6dfhpjxob9nt/cudi?fimg-ssr-default");
        boolean isEpisode = false;
        boolean isPodcast = false;
        Track track = new Track(artist,artists,album,duration,name,uri,iURI,isEpisode,isPodcast);
        TrackNode tn = new TrackNode(track);
        DBTrackNode dbTN = new DBTrackNode(tn,49.0,30.4,0,0,"");




        //Adds DBTrackNode to Firestore (adapted from https://firebase.google.com/docs/firestore/quickstart)
        MainActivity.db.collection("Tags")
                .add(dbTN)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                       Log.println(Log.ASSERT, "Sending Doc", "DocumentSnapshot added with ID: " + documentReference.getId());
                       dbTN.docID = documentReference.getId();
                       updateTag(dbTN);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.println(Log.ASSERT, "Sending Doc Failed", "Error adding document" );
                    }
                });
    }

    public void updateTag(DBTrackNode node){
        String ref = node.getdocID();
        DocumentReference toUpdate = MainActivity.db.collection("Tags").document(ref);
        toUpdate.update("docID", ref)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.println(Log.ASSERT, "Update Success", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.println(Log.ASSERT, "Update failed", "Error updating document");
                    }
                });


    }

    private void updateVote() {
        //get DBTrackNode playing

    }



}