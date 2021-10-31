package com.example.musictags;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;

    public SearchCustomAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
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
        TextView tvSong= (TextView)view.findViewById(R.id.songTitle);
        tvSong.setText("Song Title");
        TextView tvArtist= (TextView)view.findViewById(R.id.artist);
        tvArtist.setText("Artist");

        //Handle buttons and add onClickListeners
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
