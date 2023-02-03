package com.tddd80.myapplication.project.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tddd80.myapplication.project.VolleySingleton;
import com.tddd80.myapplication.project.helperClasses.URLs;
import com.tddd80.myapplication.project.helperClasses.LocalUser;

import java.util.HashMap;
import java.util.Map;


public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_TOKEN = "keytoken";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(LocalUser localUser) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, localUser.getUsername());
        editor.putString(KEY_TOKEN, localUser.getJwtToken());
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null && sharedPreferences.getString(KEY_TOKEN, null) != null;
    }

    //this method will give the logged in user
    public LocalUser getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new LocalUser(
                sharedPreferences.getString(KEY_USERNAME, null), sharedPreferences.getString(KEY_TOKEN, null)
        );
    }

    //this method will logout the user
    public void logout() {
        String URL = URLs.URL_LOGOUT;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Should only happen when client doesnt reach server, if it does
                        //a valid token will be unused which isnt great, but dont know what to do
                        //about it
                        error.printStackTrace();

                        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getUser().getJwtToken());
                return headers;
            }
        };

        VolleySingleton.getInstance(mCtx).addToRequestQueue(stringRequest);
    }
}

