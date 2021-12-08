package com.example.musictags;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class QueueCustomAdapter extends ArrayAdapter<TrackNode> implements ListAdapter {
    private Queue<TrackNode> tracks = new LinkedList<TrackNode>();
    private Context context;


    public QueueCustomAdapter(ArrayList<TrackNode> tracks, Context context) {
        super(context,0,tracks);
    }



    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public TrackNode getItem(int pos) {

        return tracks.peek();
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //return list.get(pos).getId();
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_queue_layout, null);
        }

        //Handle TextView and display string from your list
        LinearLayout queueItem = (LinearLayout) view.findViewById((R.id.queueItem));


        ImageView coverArt = (ImageView) view.findViewById(R.id.coverArt);
        //TODO
        //temporary data to show UI
        coverArt.setImageResource(R.drawable.ic_home);
        TextView tvSong= (TextView)view.findViewById(R.id.songTitle);
        tvSong.setText("Song Title");
        TextView tvArtist= (TextView)view.findViewById(R.id.artist);
        tvArtist.setText("Artist");

        //Handle buttons and add onClickListeners
        ImageView upvotebtn= (ImageView)view.findViewById(R.id.upvote);
        ImageView downvotebtn = (ImageView) view.findViewById(R.id.downvote);
        upvotebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //upvote song
                //TODO
            }
        });

        downvotebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //downvote song and remove from queue
                //TODO
            }

        });

        return view;
    }
}