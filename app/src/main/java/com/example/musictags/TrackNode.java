package com.example.musictags;

import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.Track;

import java.util.List;


public class TrackNode extends Track {
    //need name, uri, artist, duration in milliseconds, imageUri, album
    //isEpisode and isPodcast always false

    //TODO still per https://firebase.google.com/docs/firestore/manage-data/add-data
    // need an empty public constructor
//    public TrackNode() {
//        super();
//    }

    //add any relevant information
//
    public TrackNode(Artist artist, List<Artist> artists, Album album, long duration, String name, String uri, ImageUri imageUri, boolean isEpisode, boolean isPodcast) {
        super(artist, artists, album, duration, name, uri, imageUri, isEpisode, isPodcast);
    }

    public TrackNode(Track track){

        super(track.artist,track.artists,track.album,track.duration,track.name,track.uri,track.imageUri, track.isEpisode, track.isPodcast);
    }

}
