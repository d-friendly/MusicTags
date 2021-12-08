package com.example.musictags;

import android.media.MediaParser;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import com.spotify.android.appremote.api.UserApi;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Result;
import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.Capabilities;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

public class QueueFragment extends Fragment {

    public ArrayList<TrackNode> tracks;
    public TrackNode tn;
    private ArrayList<String> data;
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
        //PLACHOLDER DATA
    //

        //TODO gets an arraylist of tracks from another 'backend'?
        //tracks =


//

        //TODO this is giving me errors but spotify will connect with it commented out
//        if(MainActivity.getPlayerState()==null){
//            MainActivity.getPlayerApi().skipNext();
//            Log.i("hellothere","iiii");
//        }

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
        tn = new TrackNode(track);
        Button yourButton = (Button) v.findViewById(R.id.button);
        //set onclicklistener for your button
        yourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.play(tn);
            }
        });


        tracks = new ArrayList<TrackNode>();
        tracks.add(tn);
//        data = new ArrayList<String>(Arrays.asList("111,222,333,444,555,666,dfdfd,dfsdfdsf,dfsdfsdf,dfdfsdga,fdfafds,dsfdsfs,dfsdfs,dfs,df,sdf,sd,dfdf,sdf,sdf,d,fd,fd,f,d,fd,f,df,df".split(",")));
        listView.setAdapter(new QueueCustomAdapter(tracks, getContext()));
//        listView.setAdapter(new QueueCustomAdapter(trackList, getContext()));
        // Inflate the layout for this fragment
        return v;
    }
}


