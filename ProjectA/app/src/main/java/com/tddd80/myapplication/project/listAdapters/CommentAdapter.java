package com.tddd80.myapplication.project.listAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import com.tddd80.myapplication.project.helperClasses.Comment;
import com.tddd80.myapplication.project.R;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<Comment> {

    private final int layoutResource;

    public CommentAdapter(Context context, int layoutResource, List<Comment> commentList) {
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

        Comment comment = getItem(position);

        if (comment != null) {
            TextView usernameCommentTextView = (TextView) view.findViewById(R.id.usernameCommentTextView);
            TextView contentCommentTextView = (TextView) view.findViewById(R.id.contentCommentTextView);

            if (usernameCommentTextView != null) {
                usernameCommentTextView.setText(comment.getUser());
            }
            if (contentCommentTextView != null) {
                contentCommentTextView.setText(comment.getContent());
            }
        }

        return view;
    }
}
