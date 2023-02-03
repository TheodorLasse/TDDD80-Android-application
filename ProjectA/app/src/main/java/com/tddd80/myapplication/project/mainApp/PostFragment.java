package com.tddd80.myapplication.project.mainApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.tddd80.myapplication.project.listAdapters.CommentAdapter;
import com.tddd80.myapplication.project.login.LoginActivity;
import com.tddd80.myapplication.project.login.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PostFragment extends Fragment {
    private View rootView;
    private String post_id;
    private Post post;
    private Gson gson;
    private LocalUser localUser;
    private boolean isLiked = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        assert getArguments() != null;
        post_id = getArguments().getString("post_id");
        rootView = inflater.inflate(R.layout.fragment_post, container, false);
        localUser = SharedPrefManager.getInstance(rootView.getContext()).getUser();

        gson = new Gson();
        Activity A = getActivity();
        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(rootView.getContext()).isLoggedIn()) {
            A.finish();
            startActivity(new Intent(A, LoginActivity.class));
        }

        getPost();

        //when the user presses comment button
        //calling the publishComment method
        rootView.findViewById(R.id.buttonPublishComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishComment();
            }
        });

        //when the user presses like button
        //calling the likePost method
        rootView.findViewById(R.id.buttonLikePost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If post is not liked, it likes it and vice versa then flips isLiked boolean
                likePost(!isLiked);
                isLiked = !isLiked;
            }
        });

        return rootView;
    }

    private void likePost(boolean likeIt){
        //true = like the post, false = unlike the post

        //first getting the values
        final String username = localUser.getUsername();

        String baseURL;
        if (likeIt){
            baseURL = URLs.URL_LIKE;
        }
        else {
            baseURL = URLs.URL_UNLIKE;
        }

        String URL = baseURL + post_id;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getPost();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        getPost();
                    }
                }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject json = new JSONObject();
                try {
                    json.put("username", username);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return json.toString().getBytes();
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization", "Bearer " + localUser.getJwtToken());
                return headers;
            }
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void publishComment(){

        //first getting the values
        final String username = localUser.getUsername();
        final EditText editTextPostComment = rootView.findViewById(R.id.editTextPostComment);
        final String content = editTextPostComment.getText().toString();

        //validating inputs
        if (TextUtils.isEmpty(content)) {
            editTextPostComment.setError("Please write a comment first");
            editTextPostComment.requestFocus();
            return;
        }

        String URL = URLs.URL_NEW_COMMENT + post_id;
        //if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getPost();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        editTextPostComment.setError("Something went wrong");
                        editTextPostComment.requestFocus();
                    }
                }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject json = new JSONObject();
                try {
                    json.put("username", username);
                    json.put("content", content);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return json.toString().getBytes();
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization", "Bearer " + localUser.getJwtToken());
                return headers;
            }
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }


    private void getPost(){
        String URL = URLs.URL_POST + post_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        post = gson.fromJson(response, Post.class);
                        findAndSetViews();
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

    private void findAndSetViews(){
        //Find the TextViews
        TextView postUsername = rootView.findViewById(R.id.textViewPostAuthor);
        TextView postTitle = rootView.findViewById(R.id.textViewPostTitle);
        TextView postDescription = rootView.findViewById(R.id.textViewPostDescription);
        TextView postLikes = rootView.findViewById(R.id.textViewPostLikes);
        ListView commentField = rootView.findViewById(R.id.commentFieldListView);

        //Set the adapter for commentlist
        CommentAdapter commentAdapter = new CommentAdapter(
                getActivity(), R.layout.comment_adapter, post.getComments());
        commentField.setAdapter(commentAdapter);

        //Set the TextViews
        postUsername.setText(post.getAuthor());
        postTitle.setText(post.getTitle());
        postDescription.setText(post.getDescription());
        postLikes.setText(post.getLikes());

    }
}
