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
import android.telecom.Call;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.android.appremote.api.UserApi;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Result;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.Empty;
import com.spotify.protocol.types.ImageUri;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private NavigationBarView bottomNavigationView;

    /*
    JOSHES
     */
    LocationManager locationManager;
    LocationListener locationListener;
    /*
    JOSHES END
     */

    public static boolean isPaused;
    private static final String CLIENT_ID = "10ee2098620d4a0b8fde685d19d8a0ab";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    //private static final String REDIRECT_URI = "http://com.yourdomain.musictags/callback;

    private static SpotifyAppRemote mSpotifyAppRemote;
    private static GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient; //Save the instance
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 7;
    public static boolean playFromAppQueue=false;
    private static TrackNode currentTrack;

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentTrack=null;

        // Set the connection parameters
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {

                        //Spotify app remote created. Use throughout app
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.i("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        //connected();
                        mSpotifyAppRemote.getPlayerApi()
                                .subscribeToPlayerState()
                                .setEventCallback(playerState -> {
                                    final Track track = playerState.track;

                                    isPaused = playerState.isPaused ;
                                    if(playerState.playbackPosition == playerState.track.duration - 1000 && playFromAppQueue){

                                        //other option TODO delete playFromAppQueue

                                        //play song from uri and quickly stop it(or dont stop it and have it be the correct first song of queue
                                        //then add all songs to queue
                                        //Change if statement this is option we choose


                                        //TODO handling end up song queuing
                                        //get next tracknode from queue
                                        //TrackNode nextSong = ....

                                        //get updated arraylist representative of queue (might be redudent step)
                                        //play next tracknode
                                        //play(nextSong);
                                        //notify adpater that data has changed to update listview
                                        //           if this doesnt work we should try reattaching to customadapter(dont like this option, work around at best)
                                        //myAdapter.mySetNewContentMethod(someNewContent);
                                        //myAdapter.notifyDataSetChanged();

                                    }
                                    if (track != null) {
                                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                                        currentTrack = new TrackNode(track);
                                        //TODO Josh
                                        // put track.artist.name or track.name/ track.ablum etc
                                        // into fragment_home.xml (probably send info to HomeFragment.java)
                                        // all on spotify api or in examples
                                        //return track;
                                    } else {
                                        Log.d("MainActivity", "track was null");
                                        currentTrack=null;
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);
                        Log.i("fail","fails to connect to spotify");
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
        bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnItemSelectedListener(bottomnavFunction);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
        /*
        JOSHES
         */
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
        /*
        JOSHES END
         */
    }

    /*
        Pauses song if playing on separate thread.
     */
    public static boolean pause(){
        new Thread() {
            @Override
            public void run() {

                if(mSpotifyAppRemote!=null) {
                    CallResult<Empty> pauseCall = mSpotifyAppRemote.getPlayerApi().pause();
                    Result<Empty> pauseResult = pauseCall.await(10, TimeUnit.SECONDS);
                    if (pauseResult.isSuccessful()) {
                        Log.i("play","pause");
                        // have some fun with playerState
                    } else {
                        Throwable error = pauseResult.getError();
                        // try to have some fun with the error
                        Log.i("play","failPause") ;
                    }
                }
            }

        }.start();
        return true;
    }
    /*
        Resumes es song if playing on separate thread.
     */
    public static boolean resume(){
        new Thread() {
            @Override
            public void run() {


                if(mSpotifyAppRemote!=null) {
                    CallResult<Empty> pauseCall = mSpotifyAppRemote.getPlayerApi().resume();
                    Result<Empty> pauseResult = pauseCall.await(10, TimeUnit.SECONDS);
                    if (pauseResult.isSuccessful()) {
                        Log.i("play","resumed");
                        // have some fun with playerState
                    } else {
                        Throwable error = pauseResult.getError();
                        // try to have some fun with the error
                        Log.i("play","failResume") ;
                    }
                }
            }

        }.start();
        return true;
    }



    /*
    Runs skip operation in seperate thread.
     */
    public static boolean skip(){
        new Thread() {
            @Override
            public void run() {

                if(mSpotifyAppRemote!=null) {
                    CallResult<Empty> skipCall = mSpotifyAppRemote.getPlayerApi().skipNext();
                    Result<Empty> skipResult = skipCall.await(10, TimeUnit.SECONDS);
                    if (skipResult.isSuccessful()) {
                        Log.i("play","workingSkip");
                        // have some fun with playerState

                    } else {
                        Throwable error = skipResult.getError();
                        // try to have some fun with the error
                        Log.i("play","failSkip") ;
                    }
                }
            }

        }.start();
        return true;
    }
    /*
        Runs skipPrevious operation in seperate thread.
         */
    public static boolean skipPrevious(){
        new Thread() {
            @Override
            public void run() {

                if(mSpotifyAppRemote!=null) {
                    CallResult<Empty> skipPrevCall = mSpotifyAppRemote.getPlayerApi().skipPrevious();
                    Result<Empty> skipPrevResult = skipPrevCall.await(10, TimeUnit.SECONDS);
                    if (skipPrevResult.isSuccessful()) {
                        Log.i("play","workingSkipPrev");
                        // have some fun with playerState

                    } else {
                        Throwable error = skipPrevResult.getError();
                        // try to have some fun with the error
                        Log.i("play","failSkipPrev") ;
                    }
                }
            }

        }.start();
        return true;
    }
    /*
        Plays a track based on a track uri
        Parameters: TrackNode
        Return: true if successfully plays song and false if fails. can throw error if we like

     */
    public static boolean play(TrackNode track){
        new Thread() {
            @Override
            public void run() {
                String uri = track.uri;

                if(mSpotifyAppRemote!=null) {
                    CallResult<Empty> playCall = mSpotifyAppRemote.getPlayerApi().play(uri);
                    Result<Empty> playResult = playCall.await(10, TimeUnit.SECONDS);
                    if (playResult.isSuccessful()) {
                        Log.i("play","working");
                        // have some fun with playerState

                    } else {
                        Throwable error = playResult.getError();
                        // try to have some fun with the error
                        Log.i("play","fail") ;

                    }
                }else{
                    Log.i("play", "mSpotifyAppRemote is null");
                }
            }
        }.start();
        return true;
    }

    /*
        Gets PlayerApi
        Parameters:
        Return PlayerApi. null if fails
        TODO
     */
    public static PlayerApi getPlayerApi(){
         return mSpotifyAppRemote.getPlayerApi();
    }

    /*
        Gets UserAPI
        Parameters:
        Return PlayerApi. null if fails
        TODO
     */
    public static UserApi getUserApi(){
        return mSpotifyAppRemote.getUserApi();
    }

    /*
        Gets current song
        Parameters:
        Return current song. null if no song playing or fails
        NOT CURRENTLY BEING CALLEDD TODO
     */
    public static TrackNode getCurrentSong() {
        if(currentTrack!=null){
            return currentTrack;
        }else{
            return null;
        }
    }


    /*
        DisplayQueue from different thread.
     */
    private void displayQueue(){
        new Thread() {
            @Override
            public void run() {

            }

        }.start();
    }


    /*
     Creates a DBnode by getting current location. Called when pinning a song to cconvert nodes
     */
    public static DBTrackNode attachNodeToLocation(TrackNode tn){
        //TODO josh once he can longi and lati this where we will call that method.

        double longi = Math.random()* -89.40059803284569; //temp
        double lati = Math.random()* 43.07513050785626; //tem[
        int upvote = 0;
        int downvote = 0;
        DBTrackNode dbTN = new DBTrackNode(tn,longi,lati,upvote,downvote,"");

        return dbTN;

    }


    private NavigationBarView.OnItemSelectedListener bottomnavFunction = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //Log.println(Log.ASSERT,"fragments", "frags created");
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


    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

    }



    /*
    JOSHES
     */
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

        //TextView latitude = (TextView) findViewById(R.id.latitude);
        //TextView longitude = (TextView) findViewById(R.id.longitude);
        //TextView altitude = (TextView) findViewById(R.id.altitude);
        //TextView accuracy = (TextView) findViewById(R.id.accuracy);

        //latitude.setText("Latitude: " + location.getLatitude());
        //longitude.setText("Longitude: " + location.getLongitude());
        //altitude.setText("Altitude: " + location.getAltitude());
        //accuracy.setText("Accuracy: " + location.getAccuracy());


    }
    /*
    JOSHES END
     */

}