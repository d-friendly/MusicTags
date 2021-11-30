package com.example.musictags;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class QueueCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;

    public QueueCustomAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
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
        coverArt.setImageResource(R.drawable.kanye);
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