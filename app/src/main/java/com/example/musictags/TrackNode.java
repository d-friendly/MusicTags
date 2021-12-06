package com.example.musictags;

import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.Track;

import java.util.List;

public class TrackNode extends Track {

    //add any relevant information

    public TrackNode(Artist artist, List<Artist> artists, Album album, long duration, String name, String uri, ImageUri imageUri, boolean isEpisode, boolean isPodcast) {
        super(artist, artists, album, duration, name, uri, imageUri, isEpisode, isPodcast);
    }

    public TrackNode(Track track){
        super(track.artist,track.artists,track.album,track.duration,track.name,track.uri,track.imageUri, track.isEpisode, track.isPodcast);
    }
}
