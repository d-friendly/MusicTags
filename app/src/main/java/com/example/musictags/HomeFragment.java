package com.example.musictags;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


public class HomeFragment extends Fragment implements View.OnClickListener {

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View homeV = inflater.inflate(R.layout.fragment_home, container, false);

        ImageButton upVote = (ImageButton) homeV.findViewById(R.id.upVoteButton);
        upVote.setOnClickListener(this);
        ImageButton downVote = (ImageButton) homeV.findViewById(R.id.downVoteButton);
        downVote.setOnClickListener(this);
        ImageButton nextSong = (ImageButton) homeV.findViewById(R.id.nextSongButton);
        nextSong.setOnClickListener(this);
        ImageButton lastSong = (ImageButton) homeV.findViewById(R.id.lastSongButton);
        lastSong.setOnClickListener(this);
        ImageButton play = (ImageButton) homeV.findViewById(R.id.playButton);
        play.setOnClickListener(this);

        //returns layout for this fragment
        return homeV;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upVoteButton:
                //TODO up vote code
                break;
            case R.id.downVoteButton:
                //TODO down vote code
                break;
            case R.id.playButton:
                //TODO play button code
                break;
            case R.id.lastSongButton:
                //TODO last song button code
                break;
            case R.id.nextSongButton:
                //TODO next song button code
                break;
            default:
                break;
        }
    }
}