package com.example.splendor.Models;

public class MessageModel {
    String message, senderId,content, contentType;
    long timeStamp;

    public MessageModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public MessageModel(String message, String senderId, String content, long timeStamp, String contentType) {
        this.message = message;
        this.senderId = senderId;
        this.content = content;
        this.timeStamp = timeStamp;
        this.contentType = contentType;
    }

}
