package com.example.pawgersapp;

import com.google.firebase.database.Exclude;

public class Users {
    @Exclude
    public String key;
    private String name;
    private String image;
    private String status;
    private dogs dogs;

    public Users(String name, String image, String status) {
        this.name = name;
        this.image = image;
        this.status = status;
    }

    public Users(){

    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public com.example.pawgersapp.dogs getDogs() {
        return dogs;
    }

    public void setDogs(com.example.pawgersapp.dogs dogs) {
        this.dogs = dogs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
