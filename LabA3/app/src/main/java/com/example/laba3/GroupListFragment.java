package com.example.laba3;

import android.os.Bundle;

import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.Objects;

public class GroupListFragment extends ListFragment implements AdapterView.OnItemClickListener {

    Gson gson = new Gson();
    RequestQueue queue;

    private Groups group;

    public String getURL() {
        return "https://tddd80server.herokuapp.com/grupper";
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        String groupName = getGroupName(pos);
        ((MainActivity) getActivity()).setMembers(groupName);
    }

    public String getGroupName(int number) {
        return group.getName(number);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orig_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));

        doVolleyStuff(getURL(), Request.Method.GET);

        getListView().setOnItemClickListener(this);
    }


    public void doVolleyStuff(String url, int method) {
        StringRequest stringRequest = new StringRequest(method, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        group = gson.fromJson(response, Groups.class);
                        System.out.println(response);
                        ArrayAdapter < String > adapter = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_list_item_1, group.getNames());
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