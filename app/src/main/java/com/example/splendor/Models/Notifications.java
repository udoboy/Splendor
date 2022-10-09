package com.example.splendor.Models;

public class Notifications {
    String publisherId, postId, description, ntype, notificationId;

    public Notifications() {

    }

    public Notifications(String publisherId, String postId, String description, String ntype, String notificationId) {
        this.publisherId = publisherId;
        this.postId = postId;
        this.description = description;
        this.ntype = ntype;
        this.notificationId = notificationId;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNtype() {
        return ntype;
    }

    public void setNtype(String ntype) {
        this.ntype = ntype;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
}
