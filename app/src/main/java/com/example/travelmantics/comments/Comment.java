package com.example.travelmantics.comments;

import java.io.Serializable;

public class Comment implements Serializable {

    private String review;
    private User user;

    public Comment() {
    }

    public Comment(String review, User user) {
        this.review = review;
        this.user = user;
    }

    public String getReview() {
        return review;
    }

    public User getUser() {
        return user;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "review='" + review + '\'' +
                ", user=" + user +
                '}';
    }
}
