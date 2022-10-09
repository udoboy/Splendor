package com.example.splendor.Models;

public class CommentReplies {
    String comment, commentReplyPublisher, parentCommentId, commentId, postId;

    public CommentReplies() {
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public CommentReplies(String comment, String commentReplyPublisher, String parentCommentId, String commentId, String postId) {
        this.comment = comment;
        this.commentReplyPublisher = commentReplyPublisher;
        this.parentCommentId = parentCommentId;
        this.commentId = commentId;
        this.postId = postId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentReplyPublisher() {
        return commentReplyPublisher;
    }

    public void setCommentReplyPublisher(String commentReplyPublisher) {
        this.commentReplyPublisher = commentReplyPublisher;
    }

    public String getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
}
