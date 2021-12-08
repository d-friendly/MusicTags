package com.example.musictags;

import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.ImageUri;

import java.util.List;

public class DBTrackNode extends TrackNode {

    public int upvote;
    public int downvote;
    public double longitude;
    public double latitude;


    public DBTrackNode(Artist artist, List<Artist> artists, Album album, long duration, String name, String uri, ImageUri imageUri, boolean isEpisode, boolean isPodcast, double longitude, double latitude, int upvote, int downvote) {
        super(artist, artists, album, duration, name, uri, imageUri, isEpisode, isPodcast);
        this.upvote=upvote;
        this.downvote=downvote;
        this.longitude=longitude;
        this.latitude=latitude;
    }

    public DBTrackNode(TrackNode tn, double longitude, double latitude, int upvote, int downvote){
        super(tn);
        this.upvote=upvote;
        this.downvote=downvote;
        this.longitude=longitude;
        this.latitude=latitude;
    }

}
