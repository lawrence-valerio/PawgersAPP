package com.example.pawgersapp.POJO_Classes;

public class dogs {
    private String dogBreed;
    private String dogName;

    public dogs(String dogBreed, String dogName) {
        this.dogBreed = dogBreed;
        this.dogName = dogName;
    }

    public dogs(){

    }

    public String getDogBreed() {
        return dogBreed;
    }

    public void setDogBreed(String dogBreed) {
        this.dogBreed = dogBreed;
    }

    public String getDogName() {
        return dogName;
    }

    public void setDogName(String dogName) {
        this.dogName = dogName;
    }
}
