package com.tddd80.myapplication.project.mainApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tddd80.myapplication.project.R;
import com.tddd80.myapplication.project.login.LoginActivity;
import com.tddd80.myapplication.project.login.SharedPrefManager;

public class SearchFragment extends Fragment {
    EditText editTextSearchUsername;
    View rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        Activity A = getActivity();
        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(rootView.getContext()).isLoggedIn()) {
            A.finish();
            startActivity(new Intent(A, LoginActivity.class));
        }

        editTextSearchUsername = (EditText) rootView.findViewById(R.id.editTextSearchUser);


        //when the user presses logout button
        //calling the logout method
        rootView.findViewById(R.id.buttonSearchUsername).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchFor = editTextSearchUsername.getText().toString();
                ((MainAppActivity) A).switchUserProfile(searchFor);
            }
        });
        return rootView;
    }
}
