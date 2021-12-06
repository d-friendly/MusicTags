package com.example.musictags;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class SearchFragment extends Fragment {
    private ArrayList<String> data;
    public String jsonString;



    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_search, container, false);
        //add list to list view
        ListView listView = (ListView) v.findViewById(R.id.listView);
        //PLACEHOLDER DATA
        data = new ArrayList<String>(Arrays.asList("111,222,333,444,555,666".split(",")));
        listView.setAdapter(new SearchCustomAdapter(data, getContext()) );

        EditText searchText = (EditText) v.findViewById(R.id.searchBox);


        String searchItem = "IGORS THEME";
        querySong(searchItem);

        //System.out.println(jsonString);



        return v;
    }



    //TODO: Change String Request to JSON

    public void addToSearch(String name, String uri) {

    }


    public void querySong(String searchItem) {
        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String tempJSONString;
        String url = "https://api.spotify.com/v1/search?type=track&q=" + searchItem.trim();
        String name;
        String uri;

        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                response -> {

                    try {

                        //System.out.println(response.toString());

                        JSONObject obj = new JSONObject(response);
                        JSONObject a = obj.optJSONObject("tracks");
                        JSONArray arr = new JSONArray(a.getString("items"));

                        for(int i = 0; i < arr.length(); i++){
                            JSONObject temp = arr.getJSONObject(i);
                            System.out.println(temp.getString("name"));
                            System.out.println(temp.getString("uri"));
                            addToSearch(temp.getString("name"), temp.getString("uri"));

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                error -> Log.d("ERROR","Broken")
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("Authorization", "Bearer BQBbzzk_Qw7Qw4HdPv93-82niuGGz6kuGA0XW4PXYpkt1mpH42X5kMkMXYrgBhxn2YjsY4PtPS-Yeqvs8AOq-0OlJjFu7CEYYjFx_jEZB33EgRbLOGyNHqn9ZYUtUmX8585-I210D1R7p5np2O1DlUSNGzXgPuFIExQ");


                return params;
            }
        };
        queue.add(getRequest);

    }
}

