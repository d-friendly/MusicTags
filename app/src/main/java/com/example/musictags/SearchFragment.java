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
    private ArrayList<TrackNode> searchResults;

    private EditText searchBox;
    private ListView listView;
    private static Map<String, String>  params = new HashMap<String, String>();


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


                //searchResults = new ArrayList<TrackNode>();


                new Thread(){
                @Override
                    public void run() {
                        querySong(query);
                    }

                }.start();



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
                error -> Log.d("ERROR","Token not valid")
        ) {
            //Authorization of GET request. Adds OAuth2 code.
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {


                //TODO: Keep losing authorization, need to fix.
                String CLIENT_SECRET = "f484de60c4d445f88ac37e899cb46e65";
                String tokenURL = "https://accounts.spotify.com/api/token";

                StringRequest stringRequestAuth = new StringRequest(Method.POST, tokenURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                String token;
                                System.out.println(response.toString());
                                try {
                                    JSONObject r = new JSONObject(response);
                                    token =  r.getString("access_token");
                                    System.out.println(token);
                                    SearchFragment.params p = () ->
                                    {
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
                                System.out.println("error");
                            }
                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<String, String>();
                        String clients = "10ee2098620d4a0b8fde685d19d8a0ab" + ":" + CLIENT_SECRET;

                        String base64Credentials = Base64.encodeToString(clients.getBytes(), Base64.NO_WRAP);

                        //System.out.println(base64Credentials);

                        headers.put("Authorization", "Basic " + base64Credentials);
                        headers.put("Content-Type", "application/x-www-form-urlencoded");
                        return headers;
                    }


                    @Override
                    protected Map<String,String> getParams() throws AuthFailureError{
                        Map<String, String> authParams = new HashMap<String, String>();

                        authParams.put("grant_type", "client_credentials");
                        //authParams.put("Content-Type", "application/x-www-form-urlencoded");

                        return authParams;
                    }};

                queue.add(stringRequestAuth);

                //params.put("Authorization", "Bearer BQBXcTQcba-RMvbXB2jr1vmlfHS6TFQBSoX0Pr_Yjvu97HHP2IwkNq9R4JKoCbD6R-ZOM7sdv_X9Vy1fSoA");

                //System.out.println("PARAMS" + params.get("Authorization"));

                return params;
            }
        };

        queue.add(getRequest);

    }

}

