package com.example.splendor.Models;

import java.util.List;

public class Comments {
    String  comment, publisherId, commentId;
    List<CommentReplies> repliesList;

    public Comments() {
    }

    public Comments( String comment, String publisherId, String commentId ) {

        this.comment = comment;
        this.publisherId = publisherId;
        this.commentId = commentId;

    }

    public Comments(String comment, String publisherId, String commentId, List<CommentReplies> repliesList) {
        this.comment = comment;
        this.publisherId = publisherId;
        this.commentId = commentId;
        this.repliesList = repliesList;
    }

    public Comments(List<CommentReplies> repliesList) {
        this.repliesList = repliesList;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<CommentReplies> getRepliesList() {
        return repliesList;
    }

    public void setRepliesList(List<CommentReplies> repliesList) {
        this.repliesList = repliesList;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
