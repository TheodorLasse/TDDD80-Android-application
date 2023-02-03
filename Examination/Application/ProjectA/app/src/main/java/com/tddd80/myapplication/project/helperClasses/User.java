package com.tddd80.myapplication.project.helperClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User {
    Map<String, Map<String, String>> comments;
    List<String> liked_posts, posts, followed_users;

    public List<String> getComments(){
        List<String> commentList = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> entry : comments.entrySet()){
            commentList.add(entry.getValue().get("content"));
        }
        return commentList;
    }

    public List<String> getLikedPosts() {
        return liked_posts;
    }

    public List<String> getPosts() {
        return posts;
    }

    public String getCommentId(int pos){
        List<String> commentList = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> entry : comments.entrySet()){
            commentList.add(entry.getValue().get("post"));
        }
        return commentList.get(pos);
    }

    public String getLikedPostId(int pos){
        return liked_posts.get(pos);
    }

    public String getPostId(int pos){
        return posts.get(pos);
    }

    public List<String> getFollowedUsers() {
        return followed_users;
    }
}
