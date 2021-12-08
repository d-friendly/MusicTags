package com.example.musictags;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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


        ImageView coverArt = (ImageView) view.findViewById(R.id.coverArt);



        TextView tvSong= (TextView) view.findViewById(R.id.songTitle);
        tvSong.setText(list.get(i).name);

        TextView tvArtist= (TextView) view.findViewById(R.id.artist);
        tvArtist.setText(list.get(i).artist.name);


        Picasso.get().load(list.get(i).imageUri.raw)
                .resize(150,150)
                .centerCrop()
                .into((ImageView) view.findViewById(R.id.coverArt));




        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initializing the popup menu and giving the reference as current context
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Toast message on menu item clicked


                        switch (menuItem.getTitle().toString()){
                            case "Play Song":
                                Toast.makeText(view.getContext(), "You Clicked play",   Toast.LENGTH_SHORT).show();
                                break;
                            case "Pin Song":
                                Toast.makeText(view.getContext(), "You Clicked pin", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(view.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                break;
                        }


                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });



        return view;
    }
}
