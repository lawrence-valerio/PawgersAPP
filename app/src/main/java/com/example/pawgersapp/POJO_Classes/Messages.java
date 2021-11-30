package com.example.pawgersapp.POJO_Classes;

public class Messages {
    private String message, from;
    private boolean seen;
    private String time;

    public Messages(String message, String from, boolean seen, String time) {
        this.message = message;
        this.from = from;
        this.seen = seen;
        this.time = time;
    }

    public Messages(){

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
