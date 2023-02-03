package com.tddd80.myapplication.project.helperClasses;

import com.tddd80.myapplication.project.helperClasses.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Post {
    private String author, title, description, likes;
    private Map<String, Comment> comments;

    public Post(){}

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLikes() {
        return likes;
    }

    public List<Comment> getComments(){
        List<Comment> commentList = new ArrayList<>();
        for (Map.Entry<String, Comment> entry : comments.entrySet()){
            commentList.add(entry.getValue());
        }
        return commentList;
    }

    public void setLikes(String likes){
        this.likes = likes;
    }
}
