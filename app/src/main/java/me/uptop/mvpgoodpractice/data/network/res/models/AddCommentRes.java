package me.uptop.mvpgoodpractice.data.network.res.models;

import com.squareup.moshi.Json;

import me.uptop.mvpgoodpractice.data.storage.realm.CommentRealm;
import me.uptop.mvpgoodpractice.utils.RandomIdGenerator;

public class AddCommentRes {
    @Json(name ="remoteId")
    private int remoteId;
    private String avatar;
    @Json(name = "raiting")
    private float rating;
    private String comment;
    private String userName;
    private boolean active;


    public AddCommentRes(int remoteId, String avatar, float rating, String comment, String userName, boolean active) {
        this.remoteId = remoteId;
        this.avatar = avatar;
        this.rating = rating;
        this.comment = comment;
        this.userName = userName;
        this.active = active;
    }

    public AddCommentRes(CommentRealm mComment) {
        this.remoteId = RandomIdGenerator.generateRemoteId();
        this.avatar = mComment.getAvatar();
        this.rating = mComment.getRating();
        this.comment = mComment.getComment();
        this.userName = mComment.getUserName();
        this.active = true;
    }

//    AddCommentRes comment = new AddCommentRes(RandomIdGenerator.generateRemoteId(),
//                    "http://skill-branch.ru/img/app/avatar-1.png",
//                    rating, addComment,
//                    ConstantManager.ADD_COMMENT_USERNAME,
//                    true);


    public int getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(int remoteId) {
        this.remoteId = remoteId;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
