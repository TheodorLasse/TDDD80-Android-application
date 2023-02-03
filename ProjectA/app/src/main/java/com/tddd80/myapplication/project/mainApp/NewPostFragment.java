package com.tddd80.myapplication.project.mainApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tddd80.myapplication.project.R;
import com.tddd80.myapplication.project.helperClasses.URLs;
import com.tddd80.myapplication.project.helperClasses.LocalUser;
import com.tddd80.myapplication.project.VolleySingleton;
import com.tddd80.myapplication.project.login.LoginActivity;
import com.tddd80.myapplication.project.login.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewPostFragment extends Fragment {
    private EditText editTextDescription, editTextTitle;
    private View rootView;
    private Activity A;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_new_post, container, false);

        A = getActivity();
        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(rootView.getContext()).isLoggedIn()) {
            A.finish();
            startActivity(new Intent(A, LoginActivity.class));
        }

        editTextDescription = rootView.findViewById(R.id.editTextDescription);
        editTextTitle = rootView.findViewById(R.id.editTextTitle);

        //when the user presses post button
        //calling the post method
        rootView.findViewById(R.id.buttonPost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post();
            }
        });

        return rootView;
    }

    private void post(){
        //first getting the values

        LocalUser localUser = SharedPrefManager.getInstance(rootView.getContext()).getUser();

        final String username = localUser.getUsername();
        final String description = editTextDescription.getText().toString();
        final String title = editTextTitle.getText().toString();

        //validating inputs
        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("Please enter a title for the post");
            editTextTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            editTextDescription.setError("Please enter a description for the post");
            editTextDescription.requestFocus();
            return;
        }


        //if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_NEW_POST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            ((MainAppActivity) A).switchPost(json.getString("post_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();

                            //Something went quite wrong, might be fixed if the user logs in again
                            A.finish();
                            SharedPrefManager.getInstance(A.getApplicationContext()).logout();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        editTextTitle.setError("Something went wrong");
                        editTextTitle.requestFocus();
                    }
                }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject json = new JSONObject();
                try {
                    json.put("username", username);
                    json.put("title", title);
                    json.put("description", description);
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
}
