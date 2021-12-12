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
    public ArrayList<DBTrackNode> tracks;
    private ArrayList<String> data;
    public boolean gotQueue = false;


//    public static ArrayList<TrackNode> trackList = SearchFragment.searchResults;
    public QueueFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_queue, container, false);
        //add list to list view
        ListView listView = (ListView) v.findViewById(R.id.listView);
        //PLACEHOLDER DATA
        //TODO gets an arraylist of tracks from another 'backend'?
        //tracks =


        //TODO this is giving me errors but spotify will connect with it commented out
//        if(MainActivity.getPlayerState()==null){
//            MainActivity.getPlayerApi().skipNext();
//            Log.i("hellothere","iiii");
//        }
        //tracks=getQueue();



//
        Button yourButton = (Button) v.findViewById(R.id.button);
        //set onclicklistener for your button
        yourButton.setOnClickListener(new View.OnClickListener() {

            //TODO ALGO potential
            @Override
            public void onClick(View v) {
                //calls method that does the below
                //query firebase for songs based on parameters
                //  if firebase allows us to sort said info
                //      call algorithm for creating queue / might not need a algo just convert to arraylist
                //tell adapter that underlying list has changed / reset adapter.
                getQueue();
                if (tracks == null){
                    getQueue();
                }
                else{
                    gotQueue = false;
                    Log.i("after click", tracks.toString());
                }

            }
        });



//TODO delete this
//        tracks = new ArrayList<DBTrackNode>();
//        tracks.add(tn);
        //Get Queue



//        data = new ArrayList<String>(Arrays.asList("111,222,333,444,555,666,dfdfd,dfsdfdsf,dfsdfsdf,dfdfsdga,fdfafds,dsfdsfs,dfsdfs,dfs,df,sdf,sd,dfdf,sdf,sdf,d,fd,fd,f,d,fd,f,df,df".split(",")));
        //  listView.setAdapter(new QueueCustomAdapter(tracks, getContext()));
//        listView.setAdapter(new QueueCustomAdapter(trackList, getContext()));
        // Inflate the layout for this fragment
        return v;
    }



    /*
        retrieves queue from FireStore

     */
    private void getQueue() {
        Log.println(Log.ASSERT, "Getting Queue", "Getting Queue");
        final ArrayList<DBTrackNode> queue = new ArrayList<>();
        //Log.println(Log.ASSERT, "Is tracks null?", "tracks is: " + tracks.toString());

        if (gotQueue == false) {

            gotQueue = true;
            final GeoLocation center = new GeoLocation(MainActivity.current.getLatitude(), MainActivity.current.getLongitude());
            final double radiusInM = 50 * 1000;

            List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM);
            final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
            for (GeoQueryBounds b : bounds) {
                Query q = MainActivity.db.collection("Tags")
                        .orderBy("geohash")
                        .startAt(b.startHash)
                        .endAt(b.endHash);
                tasks.add(q.get());
                Log.println(Log.ASSERT, "tasks adding q.get():", q.get().toString());
            }
            Tasks.whenAllComplete(tasks)
                    .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                                               @Override
                                               public void onComplete(@NonNull Task<List<Task<?>>> t) {
                                                   DBTrackNode dbNode;
                                                   List<DBTrackNode> DBNodeList;
                                                   for (Task<QuerySnapshot> task : tasks) {
                                                       QuerySnapshot snap = task.getResult();
                                                       Log.println(Log.ASSERT, "tasks are", tasks.toString());
                                                       Log.println(Log.ASSERT, "snap is", snap.toString());
                                                      DBNodeList = (snap.toObjects(DBTrackNode.class));
                                                      Log.println(Log.ASSERT, "snap.getDocs is", snap.getDocuments().toString());
                                                      for (DBTrackNode node : DBNodeList) {
                                                          Log.println(Log.ASSERT, "Looping: Node is", node.toString() + node.uri);
                                                      }


                                                       for (DocumentSnapshot doc : snap.getDocuments()) {
                                                           Log.println(Log.ASSERT, "doc is", doc.toString());
                                                           dbNode = doc.toObject(DBTrackNode.class);
                                                           Log.println(Log.ASSERT, "dbNodes are", dbNode.toString());
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
    }


                //DocumentReference docRef = MainActivity.db.collection("Tags").document("XxYdY6uwwegTMflUhkNK");
            //docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    queue.add(documentSnapshot.toObject(DBTrackNode.class));
                    //Log.println(Log.ASSERT, "queue is", queue.toString());
                    //queue.add(pulledDBTN);
                   // setQueue(matchingDocs);
                //}
            //});
//            docRef.get().addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.println(Log.ASSERT, "Failed", "Could not pull DBTrackNode");
//                }
//            });
//        }


        //Log.println(Log.ASSERT, "If tracks not null", "tracks must be? " + tracks.toString());







//        UIThread.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                DocumentReference docRef = MainActivity.db.collection("Tags").document("XxYdY6uwwegTMflUhkNK");
//                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        queue.add(documentSnapshot.toObject(DBTrackNode.class));
//                        Log.println(Log.ASSERT, "queue is", queue.toString());
//                        //queue.add(pulledDBTN);
//                        setQueue(queue);
//                    }
//                });
//                docRef.get().addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.println(Log.ASSERT, "Failed", "Could not pull DBTrackNode");
//                    }
//                });
//            }
//        });
//        new Thread(new Runnable(){
//            public void run() {
//                DocumentReference docRef = MainActivity.db.collection("Tags").document("XxYdY6uwwegTMflUhkNK");
//                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        queue.add(documentSnapshot.toObject(DBTrackNode.class));
//                        Log.println(Log.ASSERT, "queue is", queue.toString());
//                        //queue.add(pulledDBTN);
//                        setQueue(queue);
//                    }
//                });
//                docRef.get().addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.println(Log.ASSERT, "Failed", "Could not pull DBTrackNode");
//                    }
//                });
//
//
//                //todo get current location
////                Location currentLocation = MainActivity.current;
////                double longi= currentLocation.getLongitude();
////                double lati= currentLocation.getLatitude();
////                final GeoLocation center = new GeoLocation(lati, longi);
////                final double radiusInM = 5100000;
////                Log.println(Log.ASSERT, "geolocation: ", center.toString());
////
////                List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM);
////                final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
////                Log.println(Log.ASSERT, "bounds are: ", bounds.toString());
////                for (GeoQueryBounds b : bounds) {
////                    Query q = MainActivity.db.collection("Tags")
////                            .orderBy("geohash")
////                            .startAt(b.startHash)
////                            .endAt(b.endHash);
////
////                    Log.println(Log.ASSERT, "document pulled: ", q.toString());
////                    tasks.add(q.get());
////                    Log.println(Log.ASSERT, "tasks list is: ", tasks.toString());
////                }
////
////                Tasks.whenAllComplete(tasks)
////                        .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
////                            @Override
////                            public void onComplete(@NonNull Task<List<Task<?>>> t) {
////                                List<DocumentSnapshot> matchingDocs = new ArrayList<>();
////                                Log.i("outside loop",tasks.toString());
////                                for (Task<QuerySnapshot> task : tasks) {
////                                    QuerySnapshot snap = task.getResult();
////                                    Log.i("outer",snap.toString());
////                                    Log.i("outer",snap.getDocuments().toString());
////
////
////
////                                    for (DocumentSnapshot doc : snap.getDocuments()) {
////                                        Log.i("inner","inner for loop");
////
////                                        double lat = doc.getDouble("latitude");
////                                        double lng = doc.getDouble("longitude");
////                                        Log.i("inner", ""+lat+", " + lng);
////                                        // We have to filter out a few false positives due to GeoHash
////                                        // accuracy, but most will match
////                                        GeoLocation docLocation = new GeoLocation(lat, lng);
////                                        double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
////                                        Log.i("inner",""+distanceInM);
////                                        if (distanceInM <= radiusInM) {
////                                            Log.i("innerIf",doc.toString());
////                                            matchingDocs.add(doc);
////                                        }
////                                    }
////                                }
////
////                                // matchingDocs contains the results
////                                // ...
////                                Log.i("queue", matchingDocs.toString());
////                                Log.i("queue", matchingDocs.toString());
////                            }
////                        });
//
//                //todo
//                //pull (up to) 15 closest Track nodes from firestore ordered by location
//                //add to queue variable
////                final List<DocumentSnapshot> list1 = null;
////                MainActivity.db.collection("tags")
////                        .whereLessThanOrEqualTo("location.longitude", longi+.0045)
////                        .whereGreaterThanOrEqualTo("location.longitude", longi - .0045)
////                        .limit(15)
////                        .get()
////                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////
////                            List<DocumentSnapshot> list = null;
////
////                            @Override
////                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                                if (task.isSuccessful()) {
////
////                                    filterResultsByLatitude(task);
//////                                    for (QueryDocumentSnapshot document : task.getResult()) {
//////                                        Log.d("database", document.getId() + " => " + document.getData());
//////
//////
//////                                    }
////                                } else {
////                                    Log.d("database", "Error getting documents: ", task.getException());
////                                }
////                            }
////
////                        });
//
//            }
//
//
//        }).start();
//        Log.println(Log.ASSERT, "nonrunnable queue is ", queue.toString());
//    }

    private void setQueue(ArrayList<DBTrackNode> queue){
        Log.println(Log.ASSERT, "In setQueue", "queue is" + queue.toString());
        tracks = queue;
        Log.println(Log.ASSERT, "In setQueue", "tracks list is" + queue.toString());
       // listView.setAdapter(new QueueCustomAdapter(tracks, getContext()));
    }
}

//    private ArrayList<DBTrackNode> setTrack() {
//        getQueue();
//        if (gQueue !=null) {
//            Log.println(Log.ASSERT, "In setTrack", "queue is" + gQueue.toString());
//        } else {
//            Log.println(Log.ASSERT, "In setTrack", "queue is null");
//        }
//
//        return gQueue;
//    }

//    public static void filterResultsByLatitude(Task<QuerySnapshot> task){
////        QuerySnapshot qs = task.getResult();
//
//
//        List<DocumentSnapshot> documentSnapshots = qs.getDocuments();
//        ArrayList<DBTrackNode> queue = new ArrayList<DBTrackNode>(documentSnapshots.size());
//
//        for (DocumentSnapshot ds: documentSnapshots){
//            queue.add(ds.toObject());
//        }
        //DocumentSnapshot qSnap = task.getResult();
        //List<DocumentSnapshot> documents  ;

//        List<Double> latitudes = ds.get()
//        Predicate<Double> byLatitude = latitude1 ->


    //}



//}


