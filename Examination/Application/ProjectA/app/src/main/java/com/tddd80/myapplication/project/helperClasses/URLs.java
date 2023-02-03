package com.tddd80.myapplication.project.helperClasses;

public class URLs {
    //private static final String ROOT_URL = "https://tddd80-theo-philip.herokuapp.com/";
    private static final String ROOT_URL = "http://10.0.2.2:5050/";


    public static final String URL_REGISTER = ROOT_URL + "user/new";
    public static final String URL_LOGIN= ROOT_URL + "user/login";
    public static final String URL_NEW_POST= ROOT_URL + "post/new";
    public static final String URL_POST= ROOT_URL + "post/view/";
    public static final String URL_NEW_COMMENT= ROOT_URL + "post/new_comment/";
    public static final String URL_LIKE= ROOT_URL + "post/like_post/";
    public static final String URL_UNLIKE= ROOT_URL + "post/unlike_post/";
    public static final String URL_VIEW_USER= ROOT_URL + "users/view/";
    public static final String URL_LOGOUT= ROOT_URL + "user/logout";
    public static final String URL_FOLLOW_USER= ROOT_URL + "users/follow_user/";
    public static final String URL_UNFOLLOW_USER= ROOT_URL + "users/unfollow_user/";
}
