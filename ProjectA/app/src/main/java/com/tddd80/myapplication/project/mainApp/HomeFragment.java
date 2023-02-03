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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.tddd80.myapplication.project.R;
import com.tddd80.myapplication.project.VolleySingleton;
import com.tddd80.myapplication.project.helperClasses.LocalUser;
import com.tddd80.myapplication.project.helperClasses.URLs;
import com.tddd80.myapplication.project.helperClasses.User;
import com.tddd80.myapplication.project.listAdapters.FollowedUsersAdapter;
import com.tddd80.myapplication.project.login.LoginActivity;
import com.tddd80.myapplication.project.login.SharedPrefManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {
    private View rootView;
    private Activity A;
    private List<String> followedUsers;
    private User user;
    private LocalUser localUser;
    private Gson gson;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        localUser = SharedPrefManager.getInstance(rootView.getContext()).getUser();
        gson = new Gson();

        A = getActivity();
        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(rootView.getContext()).isLoggedIn()) {
            A.finish();
            startActivity(new Intent(A, LoginActivity.class));
        }

        getFollowing();

        return rootView;
    }

    //Gets the users the localUser is following
    private void getFollowing(){
        getUser();
        if (user == null){
            TextView helpText = rootView.findViewById(R.id.textViewHomePage);
            helpText.setText(R.string.homeHelpText);
            return;
        }

        List<String> userList= user.getFollowedUsers();

        if (userList.size() == 0){
            TextView helpText = rootView.findViewById(R.id.textViewHomePage);
            helpText.setText(R.string.homeHelpText);
        }
        else{
            ListView followedUsersListView = rootView.findViewById(R.id.listViewHome);
            FollowedUsersAdapter followedUsersAdapter = new FollowedUsersAdapter(getActivity(),
                    R.layout.followed_users_adapter, userList);
            followedUsersListView.setAdapter(followedUsersAdapter);
        }
    }

    private void getUser(){
        String URL = URLs.URL_VIEW_USER + localUser.getUsername();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        user = gson.fromJson(response, User.class);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String username = user.getFollowedUsers().get(position);
        ((MainAppActivity) A).switchPost(username);
    }
}
