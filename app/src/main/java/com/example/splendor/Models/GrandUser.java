package com.example.splendor.Models;

public class GrandUser {
    private String normUserId, grandId, grandBio, grandProfileImage, grandCoverImage, grandUserName, grandPassword;

    public GrandUser(String normUserId, String grandId, String grandBio, String grandProfileImage, String grandCoverImage, String grandUserName, String grandPassword) {
        this.normUserId = normUserId;
        this.grandId = grandId;
        this.grandBio = grandBio;
        this.grandProfileImage = grandProfileImage;
        this.grandCoverImage = grandCoverImage;
        this.grandUserName = grandUserName;
        this.grandPassword = grandPassword;
    }

    public GrandUser() {
    }

    public String getNormUserId() {
        return normUserId;
    }

    public void setNormUserId(String normUserId) {
        this.normUserId = normUserId;
    }

    public String getGrandId() {
        return grandId;
    }

    public void setGrandId(String grandId) {
        this.grandId = grandId;
    }

    public String getGrandBio() {
        return grandBio;
    }

    public void setGrandBio(String grandBio) {
        this.grandBio = grandBio;
    }

    public String getGrandProfileImage() {
        return grandProfileImage;
    }

    public void setGrandProfileImage(String grandProfileImage) {
        this.grandProfileImage = grandProfileImage;
    }

    public String getGrandCoverImage() {
        return grandCoverImage;
    }

    public void setGrandCoverImage(String grandCoverImage) {
        this.grandCoverImage = grandCoverImage;
    }

    public String getGrandUserName() {
        return grandUserName;
    }

    public void setGrandUserName(String grandUserName) {
        this.grandUserName = grandUserName;
    }

    public String getGrandPassword() {
        return grandPassword;
    }

    public void setGrandPassword(String grandPassword) {
        this.grandPassword = grandPassword;
    }
}
