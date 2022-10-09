package com.example.splendor.Models;

public class Users {
    private String fullname, username, password, profileImage, email, bio, grandpage, id, dateOfBirth, phoneNumber, lastSeen;
    private int yearOfBirth;

    public Users() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Users(String fullname, String username, String password, String profileImage, String email , String id, String dateOfBirth, String phoneNumber, int yearOfBirth) {
        this.fullname = fullname;
        this.username = username;
        this.password = password;
        this.profileImage = profileImage;
        this.email = email;
        this.id = id;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.yearOfBirth = yearOfBirth;
    }

    public Users(String password, String email) {
        this.password = password;
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGrandpage() {
        return grandpage;
    }

    public void setGrandpage(String grandpage) {
        this.grandpage = grandpage;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }
}
