package com.example.musictags;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<TrackNode> list;
    private Context context;

    public SearchCustomAdapter(ArrayList<TrackNode> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public TrackNode getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        //TODO
        return 0;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_search_layout, null);
        }

        //Handle TextView and display string from your list
        ImageView coverArt = (ImageView) view.findViewById(R.id.coverArt);
        //TODO
        //temporary data to show UI
        coverArt.setImageResource(R.drawable.ic_home);
        TextView tvSong= (TextView) view.findViewById(R.id.songTitle);
        tvSong.setText(list.get(i).name);

        TextView tvArtist= (TextView) view.findViewById(R.id.artist);
        tvArtist.setText(list.get(i).artist.name);

        //Handle buttons and add onClickListeners
        Picasso.get().load(list.get(i).imageUri.raw)
                .resize(150,150)
                .centerCrop()
                .into((ImageView) view.findViewById(R.id.coverArt));
        ImageView menuButton= (ImageView)view.findViewById(R.id.ellipsisMenu);

        menuButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //open options for pinning, adding to queue, etc.
                //TODO
            }
        });
        return view;
    }
}
