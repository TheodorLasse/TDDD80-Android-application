package com.tddd80.myapplication.project.mainApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tddd80.myapplication.project.R;
import com.tddd80.myapplication.project.helperClasses.LocalUser;
import com.tddd80.myapplication.project.login.LoginActivity;
import com.tddd80.myapplication.project.login.SharedPrefManager;

public class ProfileFragment extends Fragment {
    View rootView;
    FragmentManager fm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        fm = getFragmentManager();

        Activity A = getActivity();
        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(rootView.getContext()).isLoggedIn()) {
            A.finish();
            startActivity(new Intent(A, LoginActivity.class));
        }

        //getting the current user
        LocalUser localUser = SharedPrefManager.getInstance(A).getUser();
        String username = localUser.getUsername();

        //Set the profile information
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        Fragment profileFragment = new UserProfileFragment();
        profileFragment.setArguments(bundle);

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setReorderingAllowed(true);

        transaction.replace(R.id.frameLayoutProfile, profileFragment);

        transaction.commit();


        //when the user presses logout button
        //calling the logout method
        rootView.findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                A.finish();
                SharedPrefManager.getInstance(A.getApplicationContext()).logout();
            }
        });
        return rootView;
    }
}
