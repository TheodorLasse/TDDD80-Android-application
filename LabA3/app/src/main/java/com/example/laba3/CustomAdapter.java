package com.example.laba3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Member> {

    private int layoutResource;

    public CustomAdapter(Context context, int layoutResource, List<Member> memberList) {
        super(context, layoutResource, memberList);
        this.layoutResource = layoutResource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }

        Member member = getItem(position);

        if (member != null) {
            TextView nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            TextView emailTextView = (TextView) view.findViewById(R.id.emailTextView);
            TextView responseTextView = (TextView) view.findViewById(R.id.responseTextView);

            if (nameTextView != null) {
                nameTextView.setText(member.getName());
            }
            if (emailTextView != null) {
                emailTextView.setText(member.getEmail());
            }
            if (responseTextView != null) {
                responseTextView.setText(member.getResponse());
            }
        }

        return view;
    }
}
