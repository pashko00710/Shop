package me.uptop.mvpgoodpractice.data.network.res.models;

import com.squareup.moshi.Json;

import java.util.Date;

public class Comments {
    @Json(name = "_id")
    private String id;
    @Json(name ="remoteId")
    private int remoteId;
    private String avatar;
    @Json(name = "raiting")
    private float rating;
    private Date commentDate;
    private String comment;
    private String userName;
    private boolean active;

    public Comments(String id, String avatar, String userName, float rating, Date date, String comment, boolean active) {
        this.id = id;
        this.avatar = avatar;
        this.userName = userName;
        this.rating = rating;
        this.commentDate = date;
        this.comment = comment;
        this.active = active;
    }


    public Comments(String id, int remoteId, String avatar, String userName, float rating, Date date, String comment, boolean active) {
        this.id = id;
        this.remoteId = remoteId;
        this.avatar = avatar;
        this.rating = rating;
        this.commentDate = date;
        this.comment = comment;
        this.userName = userName;
        this.active = active;
    }

    public Comments() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(int remoteId) {
        this.remoteId = remoteId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
