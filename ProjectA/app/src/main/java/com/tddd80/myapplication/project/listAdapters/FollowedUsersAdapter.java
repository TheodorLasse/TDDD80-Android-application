package com.tddd80.myapplication.project.listAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tddd80.myapplication.project.R;
import java.util.List;

public class FollowedUsersAdapter extends ArrayAdapter<String> {
    private final int layoutResource;
    private final List<String> userList;

    public FollowedUsersAdapter(Context context, int layoutResource, List<String> userList) {
        super(context, layoutResource, userList);
        this.layoutResource = layoutResource;
        this.userList = userList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }

        TextView textViewFollowedUsers = view.findViewById(R.id.textViewFollowedUsers);
        textViewFollowedUsers.setText(userList.get(position));


        return view;
    }
}
