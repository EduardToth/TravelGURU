package com.example.travelmantics.comments;

import java.io.Serializable;

public class User implements Serializable {
    private  String profile_picture_url;
    private  String user_name;
    private String userId;

    public User() {
        this.profile_picture_url = "";
        this.user_name = "";

    }

    public User(String pictureUrl, String name) {
        this.profile_picture_url = pictureUrl;
        this.user_name = name;
    }

    public String getProfile_picture_url() {
        return profile_picture_url;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setProfile_picture_url(String profile_picture_url) {
        this.profile_picture_url = profile_picture_url;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "User{" +
                "pictureUrl='" + profile_picture_url + '\'' +
                ", name='" + user_name + '\'' +
                '}';
    }
}
