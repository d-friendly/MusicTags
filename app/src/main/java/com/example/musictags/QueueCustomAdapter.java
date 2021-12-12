package com.example.musictags;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.FieldValue;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;



public class QueueCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<DBTrackNode> tracks = new ArrayList<DBTrackNode>();
    private Context context;


    public QueueCustomAdapter(ArrayList<DBTrackNode> tracks, Context context) {
        this.tracks=tracks;
        //Log.println(Log.ASSERT, "TRACKS", tracks.toString());
        this.context=context;
    }



    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public DBTrackNode getItem(int pos) {

        return tracks.get(pos);
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


        TextView tvSong= (TextView) view.findViewById(R.id.songTitle);
        tvSong.setText(tracks.get(position).name);
        ImageView im = (ImageView) view.findViewById(R.id.coverArt) ;
        TextView tvArtist= (TextView) view.findViewById(R.id.artist);
        tvArtist.setText(tracks.get(position).artist.name);
        //Handle buttons and add onClickListeners
        Log.i("Tracks", tracks.get(position).imageUri.raw);
        Picasso.get().load("https://i.scdn.co/image/"+ tracks.get(position).imageUri.raw.substring(14))
                .resize(150,150)
                .centerCrop()
                .into(im);

        //Handle TextView and display string from your list
        LinearLayout queueItem = (LinearLayout) view.findViewById((R.id.queueItem));

        //Handle buttons and add onClickListeners
        ImageView upvotebtn= (ImageView)view.findViewById(R.id.upvote);
        ImageView downvotebtn = (ImageView) view.findViewById(R.id.downvote);
        queueItem.setVisibility(View.VISIBLE);
        upvotebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                MainActivity.upvote(tracks.get(position).docID);
            }
        });

        downvotebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                MainActivity.downvote((tracks.get(position).docID));
            }
        });

        return view;
    }
}