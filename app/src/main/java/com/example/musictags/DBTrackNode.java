package com.example.musictags;

import android.location.Location;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.core.GeoHash;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;
import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.ImageUri;

import java.util.Date;
import java.util.List;

public class DBTrackNode { //extends TrackNode {


    public Artist artist;
    public List<Artist> artists;
    public Album album;
    public long duration;
    public String name;
    public String uri;
    public ImageUri imageUri;
    public boolean isEpisode;
    public boolean isPodcast;
    public int upvote;
    public int downvote;
    public double longitude;
    public double latitude;
    public String docID;
    public String geoHash;

    //TODO Firebase needs empty public constructor per
    // https://firebase.google.com/docs/firestore/manage-data/add-data
    public DBTrackNode() {
        upvote=0;
        downvote=0;
        longitude=0;
        latitude=0;
        docID="";
        geoHash="";
    }


    //TODO add date field to DBTrackNode
    // so that server can update it when sent to cloud firestore (if we want to)
    @ServerTimestamp
    public Date date;

//    public Timestamp dateStamp;

    public DBTrackNode(Artist artist, List<Artist> artists
                        , Album album, long duration, String name, String uri, ImageUri imageUri, boolean isEpisode, boolean isPodcast, double longitude, double latitude, String geoHash, int upvote, int downvote, String docID) {
        this.artist = artist;
        this.artists = artists;
        this.album = album;
        this.duration = duration;
        this.name = name;
        this.uri = uri;
        this.imageUri = imageUri;
        this.isEpisode = isEpisode;
        this.isPodcast = isPodcast;
        this.upvote=upvote;
        this.downvote=downvote;
        this.geoHash=geoHash;
        this.longitude=longitude;
        this.latitude=latitude;
        this.docID = docID;
    }



//    public DBTrackNode(TrackNode tn, double longitude, double latitude, String geoHash, int upvote, int downvote, String docID){
//        this.upvote=upvote;
//        this.downvote=downvote;
//        this.longitude=longitude;
//        this.latitude=latitude;
//        this.geoHash=geoHash;
//        this.docID = docID;
//
//    }



    public Artist getartist(){
        return artist;
    }

    //public List<Artist> getartists(){
        //return artists;
    //}
    public Album getalbum(){
        return album;
    }
    public long getduration(){
        return duration;
    }
    public String getname(){
        return name;
    }
    public String geturi(){
        return uri;
    }
    public ImageUri getimageUri(){
        return imageUri;
    }

    public boolean getisEpisode(){
        return isEpisode;
    }
    public boolean getisPodcast(){
        return isPodcast;
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

    public String getgeoHash(){
        return geoHash;
    }

    public Date getdate() {
        return  date;
    }

    public void setlongitde(double longitude) {this.longitude=longitude;}

    public void setlatiude(double latitude) {this.latitude=latitude;}

    public void setgeoHash(String geoHash){this.geoHash=geoHash;}
//    public Timestamp getdateStamp() {
//        return dateStamp;
//    }
}


//TODO IN CLASS: method that keeps track of only URI's plus GEORADIUS paired with the up and down votes..., receives a URI and a location + RADIUS and finds how many upvotes downvoets tags with same URI ahve and appends?
