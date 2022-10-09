package com.example.splendor.Models;

public class Posts {
    private String description, imageUrl,postId, publisherId, type, time, backgroundColor;


    public Posts() {
    }



    public Posts(String description, String imageUrl, String postId, String publisherId, String type, String time, String backgroundColor) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.postId = postId;
        this.publisherId = publisherId;
        this.type = type;
        this.time = time;
        this.backgroundColor = backgroundColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {

        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
