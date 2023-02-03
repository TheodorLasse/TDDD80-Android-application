package com.tddd80.myapplication.project.mainApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.tddd80.myapplication.project.R;
import com.tddd80.myapplication.project.helperClasses.URLs;
import com.tddd80.myapplication.project.helperClasses.LocalUser;
import com.tddd80.myapplication.project.helperClasses.User;
import com.tddd80.myapplication.project.VolleySingleton;
import com.tddd80.myapplication.project.listAdapters.UserCommentAdapter;
import com.tddd80.myapplication.project.listAdapters.UserPostAdapter;
import com.tddd80.myapplication.project.login.LoginActivity;
import com.tddd80.myapplication.project.login.SharedPrefManager;

import java.util.HashMap;
import java.util.Map;

public class UserProfileFragment extends Fragment implements AdapterView.OnItemClickListener {
    private View rootView;
    private Activity A;
    private String username;
    private LocalUser localUser;
    private Gson gson;
    private ListView posts, comments, likedPosts;
    private User user;
    private boolean isFollowing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        assert getArguments() != null;
        username = getArguments().getString("username");
        localUser = SharedPrefManager.getInstance(rootView.getContext()).getUser();
        gson = new Gson();
        A = getActivity();

        getUser();

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(rootView.getContext()).isLoggedIn()) {
            A.finish();
            startActivity(new Intent(A, LoginActivity.class));
        }

        TextView textViewUsername = rootView.findViewById(R.id.textViewUserProfileUsername);
        textViewUsername.setText(username);

        //when the user presses follow button
        //calling the folllowUser method
        rootView.findViewById(R.id.buttonUserProfileFollow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followUser(isFollowing);
                isFollowing = !isFollowing;
            }
        });

        return rootView;
    }

    private void followUser(Boolean isFollowing) {
        //This makes followUser function a toggle for following
        String URL;
        if (isFollowing){
            URL = URLs.URL_UNFOLLOW_USER + username;
        }
        else {URL = URLs.URL_FOLLOW_USER + username; }

        //if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

    private void getUser(){
        String URL = URLs.URL_VIEW_USER + username;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        user = gson.fromJson(response, User.class);
                        findAndSetViews();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ((MainAppActivity) A).switchSearch();
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

    private void findAndSetViews(){

        //Find the ListViews
        posts = rootView.findViewById(R.id.ListViewUserPosts);
        comments = rootView.findViewById(R.id.ListViewUserComments);
        likedPosts = rootView.findViewById(R.id.ListViewUserLikes);

        //Set the listeners
        posts.setOnItemClickListener(this);
        comments.setOnItemClickListener(this);
        likedPosts.setOnItemClickListener(this);


        //Set the adapter for ListViews
        UserCommentAdapter userCommentAdapter = new UserCommentAdapter(
                getActivity(), R.layout.comment_adapter, user.getComments());
        comments.setAdapter(userCommentAdapter);

        UserPostAdapter userPostAdapter = new UserPostAdapter(getActivity(),
                R.layout.user_post_adapter, user.getPosts(), localUser);
        posts.setAdapter(userPostAdapter);

        UserPostAdapter userLikedPostAdapter = new UserPostAdapter(getActivity(),
                R.layout.user_post_adapter, user.getLikedPosts(), localUser);
        likedPosts.setAdapter(userLikedPostAdapter);
    }

    public void onItemClick(AdapterView<?> adv, View v, int pos, long id) {
        String post_id = "";
        switch(adv.getId()) {
            case R.id.ListViewUserPosts:
                post_id = user.getPostId(pos);
                break;
            case R.id.ListViewUserComments:
                post_id = user.getCommentId(pos);
                break;
            case R.id.ListViewUserLikes:
                post_id = user.getLikedPostId(pos);
                break;
            default:
                Toast.makeText(A, "ERROR IN onItemClick UserProfileFragment", Toast.LENGTH_LONG).show();
        }
        ((MainAppActivity) A).switchPost(post_id);
    }
}
