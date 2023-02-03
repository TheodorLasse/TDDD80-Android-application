package com.tddd80.myapplication.project.listAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.tddd80.myapplication.project.helperClasses.Post;
import com.tddd80.myapplication.project.R;
import com.tddd80.myapplication.project.helperClasses.URLs;
import com.tddd80.myapplication.project.helperClasses.LocalUser;
import com.tddd80.myapplication.project.VolleySingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPostAdapter extends ArrayAdapter<String> {
    private final int layoutResource;
    private List<String> postList;
    private Gson gson;
    private LocalUser localUser;

    public UserPostAdapter(Context context, int layoutResource, List<String> postList, LocalUser localUser) {
        super(context, layoutResource, postList);
        this.layoutResource = layoutResource;
        this.postList = new ArrayList<>();
        this.gson = new Gson();
        this.localUser = localUser;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }

        String post_id = getItem(position);

        getPost(post_id, view);

        return view;
    }

    private void getPost(String post_id, View view){
        String URL = URLs.URL_POST + post_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Post post = gson.fromJson(response, Post.class);
                        if (post != null) {
                            TextView titleTextView = (TextView) view.findViewById(R.id.textViewUserPostTitle);

                            if (titleTextView != null) {
                                titleTextView.setText(post.getTitle());
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("shit");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + localUser.getJwtToken());
                return headers;
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }
}
