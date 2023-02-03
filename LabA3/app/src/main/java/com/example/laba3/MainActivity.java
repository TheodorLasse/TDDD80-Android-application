package com.example.laba3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    FragmentManager fm = getSupportFragmentManager();

    DisplayMetrics displayMetrics = new DisplayMetrics();

    Fragment menuFragment;
    FrameLayout infoFrame;
    Button menuButton;

    float width;
    float dpWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get width of screen
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        dpWidth = width / getResources().getDisplayMetrics().density;

        menuButton = (Button) findViewById(R.id.menuButton);
        menuFragment = fm.findFragmentById(R.id.menuFragment);
        infoFrame = findViewById(R.id.infoFrame);
    }

    public void toggleMenu(View view){
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setReorderingAllowed(true);

        if (menuFragment.isVisible() && displayIsSmall()){
            transaction.hide(menuFragment);
            toggleButtonInfoVisibility();
        }
        else if (!menuFragment.isVisible() && displayIsSmall()){
            transaction.show(menuFragment);
            toggleButtonInfoVisibility();
        }

        transaction.commit();
    }

    private void toggleButtonInfoVisibility(){
        //Checks that there even exists a button to toggle
        if (!displayIsSmall()) return;

        //Toggles infoframe and menubuttons visibility
        if (menuButton.getVisibility() == View.VISIBLE){
            infoFrame.setVisibility(View.INVISIBLE);
            menuButton.setVisibility(View.INVISIBLE);
        }
        else {
            infoFrame.setVisibility(View.VISIBLE);
            menuButton.setVisibility(View.VISIBLE);
        }
    }

    public void setMembers(String groupName){
        toggleMenu(new View(this));

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setReorderingAllowed(true);

        Bundle bundle = new Bundle();
        bundle.putString("name", groupName);
        MemberListFragment newFragment = new MemberListFragment();
        newFragment.setArguments(bundle);

        transaction.replace(R.id.infoFrame, newFragment);

        transaction.commit();
    }

    public boolean displayIsSmall() {
        return dpWidth <= 600;
    }

}