package com.example.laba3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.Objects;

public class MemberListFragment extends ListFragment{

    public String groupName;

    private MemberHandler memberHandler;

    Gson gson = new Gson();
    RequestQueue queue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupName = getArguments().getString("name");
        return inflater.inflate(R.layout.fragment_orig_list, container, false);
    }

    public String getURL() {
        String URL = "https://tddd80server.herokuapp.com/medlemmar/";
        String addition = groupName;
        URL = URL + addition;
        return URL;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));

        doVolleyStuff(getURL(), Request.Method.GET);
    }


    public void doVolleyStuff(String url, int method) {
        StringRequest stringRequest = new StringRequest(method, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        memberHandler = gson.fromJson(response, MemberHandler.class);
                        memberHandler.init();
                        CustomAdapter adapter = new CustomAdapter(getActivity(),
                                R.layout.custom_adapter_layout, memberHandler.getMembers());
                        setListAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Nope");
                    }
                });
        queue.add(stringRequest);
    }
}
