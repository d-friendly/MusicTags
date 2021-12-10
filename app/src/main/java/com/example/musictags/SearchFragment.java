package com.example.musictags;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchFragment extends Fragment implements View.OnClickListener {
    private ArrayList<TrackNode> searchResults;
    private EditText searchBox;
    private ListView listView;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_search, container, false);

        listView = (ListView) v.findViewById(R.id.listView);

        //PLACEHOLDER DATA
        //searchResults = new ArrayList<String>(Arrays.asList("111,222,333,444,555,666".split(",")));


        searchBox = (EditText) v.findViewById(R.id.searchBox);
        searchResults = new ArrayList<TrackNode>();

        Button b = (Button) v.findViewById(R.id.searchButton);
        b.setOnClickListener(this);


        return v;
    }


    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.searchButton:
                String query = String.valueOf(searchBox.getText());

                querySong(query);
                //searchResults = new ArrayList<TrackNode>();

                /*
                new Thread(){
                @Override
                    public void run() {

                    }

                }.start();


                */
                //listView.setAdapter(new SearchCustomAdapter(searchResults, getContext()));

                break;
        }

    }


    public void querySong(String searchItem) {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());

        String url = "https://api.spotify.com/v1/search?type=track&q=" + searchItem.trim();


        //Send request to Spotify Web API
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {

                        //System.out.println(response.toString());

                        //Convert response to JSONObject and revise to format we need
                        JSONObject spotifyResponse = new JSONObject(response);
                        JSONObject tracks = spotifyResponse.optJSONObject("tracks");

                        //Create array of json "tracks"
                        JSONArray arr = new JSONArray(tracks.getString("items"));

                        //Go through array of tracks and obtain data we want
                        //TODO: decide what we need to use
                        searchResults = new ArrayList<TrackNode>();
                        for(int i = 0; i < arr.length(); i++){
                            JSONObject track = arr.getJSONObject(i);


                            //Parsing through JSON response to create TrackNode

                            //Track Info
                            String trackName = track.getString("name");
                            String trackURI = track.getString("uri");
                            String duration = track.getString("duration_ms");

                            //Album Info
                            JSONObject album = track.getJSONObject("album");
                            String albumName = album.getString("name");
                            String albumURI = album.getString("uri");

                            //Cover Art Info
                            JSONArray coverArt = album.optJSONArray("images");
                            JSONObject coverArtImage = coverArt.getJSONObject(1);
                            String coverArtURL = coverArtImage.getString("url");

                            //Artist Info
                            JSONArray artists = track.getJSONArray("artists");
                            JSONObject artist = artists.getJSONObject(0);
                            String artistName = artist.getString("name");
                            String artistURI = artist.getString("uri");

                            //Objects necessary for TrackNode
                            Artist trackArtist = new Artist(artistName, artistURI);
                            Album trackAlbum = new Album(albumName, albumURI);
                            ArrayList<Artist> listOfArtists = new ArrayList<Artist>();
                            listOfArtists.add(trackArtist);
                            long trackDuration = Long.parseLong(duration);
                            ImageUri imageUri = new ImageUri(coverArtURL);

                            TrackNode trackNode = new TrackNode(
                                    trackArtist,
                                    listOfArtists,
                                    trackAlbum,
                                    trackDuration,
                                    trackName,
                                    trackURI,
                                    imageUri,
                                    false,
                                    false
                            );


                            searchResults.add(trackNode);



                        }

                        listView.setAdapter(new SearchCustomAdapter(searchResults, getContext()));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                error -> Log.d("ERROR","Broken")
        ) {
            //Authorization of GET request. Adds OAuth2 code.
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();

                //TODO: Keep losing authorization, need to fix. Also need to make it specific to user
                String CLIENT_SECRET = "f484de60c4d445f88ac37e899cb46e65";
                String tokenURL = "https://accounts.spotify.com/api/token";

                params.put("Authorization", "Bearer BQCi6Kiru1Mb5UJ-dzBMCdtohTBcVg9rmtthuDsIsw5XeahGKlli9wJ9WAZ-UpADQpZ6G4Hy1InUHY16J7Lo7wJANPxUG32bFIchmGiyoSFaKqW9W2cKjj5ljhtPw-4107PvTwICRWFV0MQkoSV2");


                return params;
            }
        };

        queue.add(getRequest);

    }

}

