package com.example.laba3;

public class Member{

    String email;
    String name;
    String response;

    public Member(String email, String name, String response) {
        this.email = email;
        this.name = name;
        this.response = response;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getResponse() {
        return response;
    }
}
