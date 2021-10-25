package com.example.musictags;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


public class QueueFragment extends Fragment {



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
        data = new ArrayList<String>(Arrays.asList("111,222,333,444,555,666".split(",")));
        listView.setAdapter(new QueueCustomAdapter(data, getContext()) );

        // Inflate the layout for this fragment
        return v;
    }
}