package com.example.musictags;

import android.location.Location;
import android.media.MediaParser;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Predicate;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.spotify.android.appremote.api.UserApi;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Result;
import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.Capabilities;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import org.w3c.dom.Document;

public class QueueFragment extends Fragment {
    private ArrayList<String> data;
    public boolean gotQueue = false;


//    public static ArrayList<TrackNode> trackList = SearchFragment.searchResults;
    public QueueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_queue, container, false);
        //add list to list view
        MainActivity.listView = (ListView) v.findViewById(R.id.listViewQueue);







        Button yourButton = (Button) v.findViewById(R.id.button);
        //set onclicklistener for your button
        yourButton.setOnClickListener(new View.OnClickListener() {

            //TODO ALGO potential
            @Override
            public void onClick(View v) {
                MainActivity.tracks = null;
                //calls method that does the below
                //query firebase for songs based on parameters
                //  if firebase allows us to sort said info
                //      call algorithm for creating queue / might not need a algo just convert to arraylist
                //tell adapter that underlying list has changed / reset adapter.
                getQueue();
                //testQuery();

                if (MainActivity.tracks != null) {
                    MainActivity.listView.setAdapter(new QueueCustomAdapter(MainActivity.tracks, getContext()));
                    gotQueue = false;
                }

            }
        });



        //Get Queue


        if(MainActivity.tracks == null) {
            MainActivity.tracks = new ArrayList<DBTrackNode>();
        }


        if(MainActivity.listView.getAdapter()==null){
            MainActivity.listView.setAdapter(new QueueCustomAdapter(MainActivity.tracks, getContext()));
        }


        // Inflate the layout for this fragment
        return v;
    }


    /**
     * Get's queue based on geolocation data from Firestore database
     */
    private void getQueue() {
        Log.println(Log.ASSERT, "Getting Queue", "Getting Queue");
        final ArrayList<DBTrackNode> queue = new ArrayList<>();
            final GeoLocation center = new GeoLocation(MainActivity.current.getLatitude(), MainActivity.current.getLongitude());
            final double radiusInM = 50 * 100000;

            List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM);
            final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
            for (GeoQueryBounds b : bounds) {
                Query q = MainActivity.db.collection("Tags")
                        .orderBy("geoHash")
                        .startAt(b.startHash)
                        .endAt(b.endHash);
                tasks.add(q.get());
            }

            Tasks.whenAllComplete(tasks)
                    .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                                               @Override
                                               public void onComplete(@NonNull Task<List<Task<?>>> t) {
                                                   Log.i("completed", "yup, completed");
                                                   DBTrackNode dbNode;
                                                   List<DBTrackNode> DBNodeList;
                                                   for (Task<QuerySnapshot> task : tasks) {
                                                       QuerySnapshot snap = task.getResult();

                                                       for (DocumentSnapshot doc : snap.getDocuments()) {
                                                           dbNode = doc.toObject(DBTrackNode.class);
                                                           double lat = dbNode.latitude;
                                                           double lng = dbNode.longitude;

                                                           GeoLocation nodeLocation = new GeoLocation(lat, lng);
                                                           double distanceInM = GeoFireUtils.getDistanceBetween(nodeLocation, center);
                                                           if (distanceInM <= radiusInM) {
                                                               queue.add(dbNode);
                                                           }


                                                       }
                                                   }
                                                   setQueue(queue);
                                               }
           });

    }


    private void testQuery() {
        Log.println(Log.ASSERT, "Entering testQuery()", "testing query");
        final ArrayList<DBTrackNode> queue = new ArrayList<>();
        Query query = MainActivity.db.collection("Tags").whereEqualTo("name", "Welcome To Japan");
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.println(Log.ASSERT, "doc is", document.getId() + " => " + document.getData());
                                queue.add(document.toObject(DBTrackNode.class));
                            }
                            setQueue(queue);
                        } else {
                            Log.println(Log.ASSERT, "Error getting documents", task.getException().toString());
                        }
                    }
                });
    }

    /**
//                DocumentReference docRef = MainActivity.db.collection("Tags").document("mJnrHlF0Emn4U1elaUnd");
//                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    queue.add(documentSnapshot.toObject(DBTrackNode.class));
//                    Log.println(Log.ASSERT, "queue is", queue.toString());
//                    //queue.add(pulledDBTN);
//                    setQueue(queue);
//                }
//            });
//            docRef.get().addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.println(Log.ASSERT, "Failed", "Could not pull DBTrackNode");
//                }
//            });
//        }
*/


    private void setQueue(ArrayList<DBTrackNode> queue){
        Log.println(Log.ASSERT, "In setQueue", "queue is" + queue.toString());
        MainActivity.tracks = queue;
        Log.i("tracks", MainActivity.tracks.get(0).artist.toString());
        Log.println(Log.ASSERT, "In setQueue", "tracks list is" + queue.toString());
        MainActivity.makeQueue();
        gotQueue = true;
//        if(MainActivity.listView.getAdapter()==null){
//            MainActivity.listView.setAdapter(new QueueCustomAdapter(MainActivity.tracks, getContext()));
//        }else{
//            ((BaseAdapter) MainActivity.listView.getAdapter()).notifyDataSetChanged();
//        }
        MainActivity.listView.setAdapter(new QueueCustomAdapter(MainActivity.tracks, getContext()));
    }



}


