package com.tddd80.myapplication.project.listAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tddd80.myapplication.project.R;

import java.util.List;

public class UserCommentAdapter extends ArrayAdapter<String> {
    private final int layoutResource;

    public UserCommentAdapter(Context context, int layoutResource, List<String> commentList) {
        super(context, layoutResource, commentList);
        this.layoutResource = layoutResource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }

        String comment = getItem(position);

        if (comment != null) {
            TextView contentCommentTextView = (TextView) view.findViewById(R.id.contentCommentTextView);

            if (contentCommentTextView != null) {
                contentCommentTextView.setText(comment);
            }
        }

        return view;
    }
}
