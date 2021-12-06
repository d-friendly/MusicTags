package com.example.musictags;

import android.media.MediaParser;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

public class QueueFragment extends Fragment {

    public ArrayList<TrackNode> tracks;

    private ArrayList<String> data;
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
//        TrackNode tn = new TrackNode(MainActivity.getPlayerState().track);
//        tracks = new ArrayList<TrackNode>();
//        tracks.add(tn);
//        data = new ArrayList<String>(Arrays.asList("111,222,333,444,555,666,dfdfd,dfsdfdsf,dfsdfsdf,dfdfsdga,fdfafds,dsfdsfs,dfsdfs,dfs,df,sdf,sd,dfdf,sdf,sdf,d,fd,fd,f,d,fd,f,df,df".split(",")));
//        listView.setAdapter(new QueueCustomAdapter(tracks, getContext()));
        // Inflate the layout for this fragment
        return v;
    }

    public void playLocalQueue(){

    }








}


