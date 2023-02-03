package com.tddd80.myapplication.project.helperClasses;

//this is very simple class and it only contains the user attributes, a constructor and the getters
// you can easily do this by right click -> generate -> constructor and getters
public class LocalUser {

    private String username;

    private String jwtToken;

    public LocalUser(String username, String token) {
        this.username = username;
        this.jwtToken = token;
    }

    public String getUsername() {
        return username;
    }

    public String getJwtToken(){return jwtToken;}
}
