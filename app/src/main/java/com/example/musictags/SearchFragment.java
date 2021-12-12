package com.example.musictags;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.protocol.types.Album;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchFragment extends Fragment implements View.OnClickListener {
    private ArrayList<DBTrackNode> searchResults;
    private EditText searchBox;
    private ListView listView;
    private static Map<String, String>  params = new HashMap<String, String>();


    // Interface required to obtain Token from Spotify for use in authorization of requests
    interface params {
        void add();
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_search, container, false);

        listView = (ListView) v.findViewById(R.id.listView);

        searchBox = (EditText) v.findViewById(R.id.searchBox);
        searchResults = new ArrayList<DBTrackNode>();

        Button b = (Button) v.findViewById(R.id.searchButton);
        b.setOnClickListener(this);

        return v;
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.searchButton:
                String query = String.valueOf(searchBox.getText());

                new Thread(){
                @Override
                    public void run() {
                        querySong(query);
                    }

                }.start();

                break;
        }

    }


    public void querySong(String searchItem) {
        // Initialize queue for sending requests to Spotify Web API
        RequestQueue queue = Volley.newRequestQueue(this.getContext());

        // URL for sending query for tracks. Search item is simply appended as a key word
        String url = "https://api.spotify.com/v1/search?type=track&q=" + searchItem.trim();

        // Send 'GET' request to Spotify Web API
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {

                        //Convert response to JSONObject and revise to format we need
                        JSONObject spotifyResponse = new JSONObject(response);
                        JSONObject tracks = spotifyResponse.optJSONObject("tracks");

                        //Create array of json "tracks"
                        JSONArray arr = new JSONArray(tracks.getString("items"));

                        //Go through array of tracks and obtain data we want
                        //TODO: decide what we need to use
                        searchResults = new ArrayList<DBTrackNode>();
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

                            DBTrackNode trackNode = new DBTrackNode(
                                    trackArtist,
                                    listOfArtists,
                                    trackAlbum,
                                    trackDuration,
                                    trackName,
                                    trackURI,
                                    imageUri,
                                    false,
                                    false,
                                    0,
                                    0,
                                    "",
                                    0,
                                    0,
                                    ""
                            );


                            searchResults.add(trackNode);


                        }

                        listView.setAdapter(new SearchCustomAdapter(searchResults, getContext()));

                    } catch (JSONException e) {

                    }

                },
                error -> {
                    Log.d("GET Request","Error in query to Spotify");


                }

        ) {
            // Authorization of GET request. Adds OAuth2 code.
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {


                // Client Credentials from Spotify Developer Dashboard (specific to Music Tags)
                String CLIENT_ID = "10ee2098620d4a0b8fde685d19d8a0ab";
                String CLIENT_SECRET = "f484de60c4d445f88ac37e899cb46e65";
                String tokenURL = "https://accounts.spotify.com/api/token";

                // POST Request for token using Client Credentials
                StringRequest stringRequestAuth = new StringRequest(Method.POST, tokenURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                String token;

                                try {
                                    JSONObject r = new JSONObject(response);
                                    token =  r.getString("access_token");

                                    // Place token in parameters required to authorize
                                    // 'GET' requests
                                    SearchFragment.params p = () ->{
                                        params.put("Authorization", "Bearer " + token);
                                    };

                                    p.add();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("TOKEN", "Failed to obtain token from Spotify");
                            }
                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<String, String>();
                        String clients = CLIENT_ID + ":" + CLIENT_SECRET;

                        // Encoded to base64, required for OAuth2 standard
                        String base64Credentials = Base64.encodeToString(clients.getBytes(), Base64.NO_WRAP);

                        headers.put("Authorization", "Basic " + base64Credentials);
                        headers.put("Content-Type", "application/x-www-form-urlencoded");

                        return headers;
                    }


                    @Override
                    protected Map<String,String> getParams() throws AuthFailureError{
                        Map<String, String> authParams = new HashMap<String, String>();

                        authParams.put("grant_type", "client_credentials");

                        return authParams;
                    }};

                queue.add(stringRequestAuth);



                //Line to hardcode if needed (emergency only)
                /*
                params.put("Authorization",
                "Bearer BQBXcTQcba-RMvbXB2jr1vmlfHS6TFQBSoX0Pr_Yjvu97HHP2IwkNq9R4JKoCbD6R-ZOM7sdv_X9Vy1fSoA");
                */

                return params;
            }
        };

        queue.add(getRequest);
        queue.add(getRequest);


    }

}

