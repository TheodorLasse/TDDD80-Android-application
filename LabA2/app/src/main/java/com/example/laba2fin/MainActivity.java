package com.example.laba2fin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FragmentManager fm = getSupportFragmentManager();
    DisplayMetrics displayMetrics = new DisplayMetrics();

    FrameLayout mainFrame;
    Fragment listFragment;
    Button backBtn;

    float width;
    float dpWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFrame = (FrameLayout)findViewById(R.id.mainFrame);
        listFragment = fm.findFragmentById(R.id.listFragment);
        backBtn = (Button)findViewById(R.id.backButton);

        // Get width of screen
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        dpWidth = width / getResources().getDisplayMetrics().density;

        assert listFragment != null;

        // Hide menu if less than 600 dp width
        if (displayIsSmall() && mainFrame.getVisibility() == View.VISIBLE) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(listFragment);
            ft.commit();
        }
        else if (displayIsSmall()) {
            backBtn.setVisibility(View.INVISIBLE);
        }
    }

    public void changePage(int pageId) {
        FragmentTransaction ft = fm.beginTransaction();

        int mainFrameId = R.id.mainFrame;

        switch (pageId) {
            case 0:
                ft.replace(mainFrameId, new Home());
                break;
            case 1:
                ft.replace(mainFrameId, new Store());
                break;
            case 2:
                ft.replace(mainFrameId, new Info());
                break;
            case 3:
                ft.replace(mainFrameId, new Contact());
                break;
        }

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Hide menu if less than 600px width and page was changed
        // if the display is larger, hide the back button
        System.out.println(dpWidth + " dp width");
        if (displayIsSmall()) {
            ft.hide(listFragment);
            backBtn.setVisibility(View.VISIBLE);
        }

        mainFrame.setVisibility(View.VISIBLE);

        ft.commit();
    }

    public void backToMenu(View v) {
        FragmentTransaction ft = fm.beginTransaction();

        ft.show(listFragment);

        mainFrame.setVisibility(View.INVISIBLE);

        if (displayIsSmall()) {
            backBtn.setVisibility(View.INVISIBLE);
        }

        ft.commit();
    }

    public boolean displayIsSmall() {
        return dpWidth <= 600;
    }

}