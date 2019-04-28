package com.myy.wenda.model;

public class User {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User(String name){
        this.name = name;
    }

    public String getDescription(){
        return "This is"+name;
    }

}
