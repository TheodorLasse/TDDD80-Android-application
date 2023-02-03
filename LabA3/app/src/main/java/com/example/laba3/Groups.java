package com.example.laba3;

public class Groups{

    private String[] grupper;

    public String[] getNames() {
        return grupper;
    }

    public String getName(int i) { return grupper[i]; }

    public void setNames(String[] names) {
        this.grupper = names;
    }

}
