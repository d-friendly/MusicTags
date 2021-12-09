package com.example.musictags;

import com.google.firebase.firestore.ServerTimestamp;
import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.ImageUri;

import java.util.Date;
import java.util.List;

public class DBTrackNode extends TrackNode {

    public int upvote;
    public int downvote;
    public double longitude;
    public double latitude;
    public String docID;

    //TODO Firebase needs empty public constructor per
    // https://firebase.google.com/docs/firestore/manage-data/add-data
//    public DBTrackNode() {
//        super();
//    }


    //TODO add date field to DBTrackNode
    // so that server can update it when sent to cloud firestore (if we want to)
    @ServerTimestamp
    public Date date;

    public DBTrackNode(Artist artist, List<Artist> artists, Album album, long duration, String name, String uri, ImageUri imageUri, boolean isEpisode, boolean isPodcast, double longitude, double latitude, int upvote, int downvote, String docID) {
        super(artist, artists, album, duration, name, uri, imageUri, isEpisode, isPodcast);
        this.upvote=upvote;
        this.downvote=downvote;
        this.longitude=longitude;
        this.latitude=latitude;
        this.docID = docID;
    }

    public DBTrackNode(TrackNode tn, double longitude, double latitude, int upvote, int downvote, String docID){
        super(tn);
        this.upvote=upvote;
        this.downvote=downvote;
        this.longitude=longitude;
        this.latitude=latitude;
        this.docID = docID;
    }


    public int getupvote(){
        return upvote;
    }

    public int getdownvote(){
        return downvote;
    }

    public double getlongitude(){
        return longitude;
    }

    public double getlatitude(){
        return latitude;
    }

    public String getdocID(){
        return docID;
    }

}