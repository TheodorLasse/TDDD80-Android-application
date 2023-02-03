package com.tddd80.myapplication.project.mainApp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tddd80.myapplication.project.R;

public class MainAppActivity extends AppCompatActivity {
    FragmentManager fm = getSupportFragmentManager();

    FrameLayout infoFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);

        infoFrame = findViewById(R.id.infoFrame);
        switchHome();
    }

    //SWITCH FUNCTIONS, SWAPS TO A DIFFERENT PAGE

    //to local user's profile page
    public void switchProfile(){
        Fragment newFragment = new ProfileFragment();

        switchFragment(newFragment);
    }

    //to create new post page
    public void switchNewPost(){
        Fragment newFragment = new NewPostFragment();

        switchFragment(newFragment);
    }

    //to a post's page
    public void switchPost(String post_id){
        Bundle bundle = new Bundle();
        bundle.putString("post_id", post_id);
        Fragment newFragment = new PostFragment();
        newFragment.setArguments(bundle);

        switchFragment(newFragment);
    }

    //to a given user's public profile page
    public void switchUserProfile(String username){
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        Fragment newFragment = new UserProfileFragment();
        newFragment.setArguments(bundle);

        switchFragment(newFragment);
    }

    //to the search page
    public void switchSearch(){
        Fragment newFragment = new SearchFragment();

        switchFragment(newFragment);
    }

    //the to the home page
    public void switchHome(){
        Fragment newFragment = new HomeFragment();

        switchFragment(newFragment);
    }

    //handles the FragmentTransaction of all switches
    private void switchFragment(Fragment fragment){
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setReorderingAllowed(true);

        transaction.replace(R.id.infoFrame, fragment);

        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.home_page:
                switchHome();
                return true;

            case R.id.profile:
                switchProfile();
                return true;

            case R.id.new_post:
                switchNewPost();
                return true;

            case R.id.search:
                switchSearch();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
