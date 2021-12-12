package com.example.musictags;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.Track;
import com.squareup.picasso.Picasso;

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
        ImageView trackImage = (ImageView) homeV.findViewById(R.id.trackImageHome);
        TextView multiLine = (TextView) homeV.findViewById(R.id.editTextTextMultiLine);
        TextView upvotes = (TextView) homeV.findViewById(R.id.Upvotes);
        TextView downvotes = (TextView) homeV.findViewById(R.id.Downvotes);
        LinearLayout linearLayout3 = (LinearLayout) homeV.findViewById(R.id.linearLayout3);

        //Change to use the DBNode from online so that we can update upvotes and downvotes
        if (MainActivity.currentTrack != null){
            linearLayout3.setVisibility(View.VISIBLE);
            Log.i("hello","https://i.scdn.co/image/" + MainActivity.currentTrack.imageUri.raw.substring(14));
            Picasso.get().load("https://i.scdn.co/image/"+ MainActivity.currentTrack.imageUri.raw.substring(14)).resize(150,150).centerCrop().into(trackImage);

            multiLine.setText(MainActivity.currentTrack.name + "  " + MainActivity.currentTrack.artist.name);
            upvotes.setText("Upvotes: " + MainActivity.currentTrack.upvote);
            downvotes.setText("Downvotes: " + MainActivity.currentTrack.downvote);
        }
        //sample working  https://i.scdn.co/image/ab67616d00001e027005885df706891a3c182a57
//        ImageView trackImage = (ImageView) homeV.findViewById(R.id.trackImage);
//        Picasso.get().load("https://i.scdn.co/image/"+ MainActivity.currentTrack.imageUri.raw.substring(14));
//        Picasso.get().load("https://i.scdn.co/image/ab67616d00001e027005885df706891a3c182a57").into(trackImage);



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
                MainActivity.upvote(MainActivity.currentTrack.docID);
                break;
            case R.id.downVoteButton:
                //TODO down vote code
                MainActivity.downvote(MainActivity.currentTrack.docID);
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
                if(MainActivity.currentTrack == null){
                    Toast.makeText(getActivity().getBaseContext()
                            , "No currently playing spotify tracks detected.", Toast.LENGTH_SHORT).show();
                    break;
                }
                DBTrackNode tn = MainActivity.getCurrentSong();
                DBTrackNode nodeToPin = MainActivity.attachNodeToLocation(tn);
                sendTag(nodeToPin);


                //DBTrackNode current = DBTracknode of song playing
                //then send tag sendTag(current)
                //SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                //        .findFragmentById(R.id.fragment_map);
                //mapFragment.getMapAsync(googleMap -> {
                //todo put all map api stuff on own thread
                pinTag(nodeToPin);
                //});
                break;
            default:
                break;
        }
    }

    /**
     * pins location and associated DBTrackNode of current song playing upon user request
     */
    //TODO add DBTrackNode as a parameter and attach to pin
    private void pinTag(DBTrackNode nodeToPin) {
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
            Log.println(Log.ASSERT, "permission not denied: ", "getting last location");
            mFusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(this.getActivity(), task -> {
                        Location mLastKnownLocation = task.getResult();
                        //Log.println(Log.ASSERT, "else statement", mLastKnownLocation.toString());
                        if (task.isSuccessful() && mLastKnownLocation != null) {

                            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
                            mapFragment.getMapAsync(googleMap -> {
                                mMap = googleMap;

                                //TODO: instead of title appending 'Listening Here', want to append the DBTrackNode or document reference id
                                mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude())).title(nodeToPin.docID));
                            });
                        }
                    });
        }
    }





    /**
     * Uploads user pinned DBTrackNode as a document object in firestore 'Tags' collection
     * @param dbTrackNode is value to be sent to firestore
     */
    public static void sendTag(DBTrackNode dbTrackNode) {

        /**
         * constructs DBTrackNode for testing purposes
         */

        /*
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
        */



        //Adds DBTrackNode to Firestore (adapted from https://firebase.google.com/docs/firestore/quickstart)
        MainActivity.db.collection("Tags")
                .add(dbTrackNode)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                       Log.println(Log.ASSERT, "Sending Doc", "DocumentSnapshot added with ID: " + documentReference.getId());

                       //fills docID field of DBTrackNode with document reference ID autogenerated by uploading to firestore
                       dbTrackNode.docID = documentReference.getId();
                       //adds document reference ID to document in firestore
                       updateTag(dbTrackNode);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.println(Log.ASSERT, "Sending Doc Failed", "Error adding document" );
                    }
                });
    }

    /**
     * Fills DBTrackNode documents in firestore with their autoGenerated reference IDs
     * @param node DBTrackNode to be updated
     */
    public static void updateTag(DBTrackNode node){
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

    /**
     * Finds and updates corresponding firebase document (DBTrackNode object)
     * with new up votes and down votes
     * @param vote up vote or down vote
     * @param dbTN DBTrackNode to be updated
     */
    private static void updateVote(String vote, DBTrackNode dbTN) {
        String ref = dbTN.getdocID();
        DocumentReference toUpdate = MainActivity.db.collection("Tags").document(ref);

        if (vote == "up"){
            //TODO: Query upvote count and add one
            int newCount = 0;
            toUpdate.update("upvote", newCount)
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


        if (vote == "down"){
            //TODO: Get downvote count and add one

           int newCount = 0;

            toUpdate.update("downvote", newCount)
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

    }







}