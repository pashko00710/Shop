package me.uptop.mvpgoodpractice.data.storage.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import me.uptop.mvpgoodpractice.data.network.res.models.AddCommentRes;
import me.uptop.mvpgoodpractice.data.storage.realm.CommentRealm;

public class CommentDto implements Parcelable {

        private String avatar;
        private String userName;
        private float rating;
        private String comment;
        private String date;

        //region ============================== Constructors ==============================

        public CommentDto() {
        }

        public CommentDto(String avatar, String userName, float rating, String comment, String date) {
            this.avatar = avatar;
            this.userName = userName;
            this.rating = rating;
            this.comment = comment;
            this.date = date;
        }

        protected CommentDto(Parcel in) {
            avatar = in.readString();
            userName = in.readString();
            rating = in.readFloat();
            comment = in.readString();
            date = in.readString();
        }

    public CommentDto(CommentRealm commentRealm) {
        this.avatar = commentRealm.getAvatar();
        this.userName = commentRealm.getUserName();
        this.rating = commentRealm.getRating();
        this.comment = commentRealm.getComment();
        this.date = commentRealm.getCommentDate().toString();
    }

    //endregion

        //region ============================== Parcelable ==============================

        public static final Creator<CommentDto> CREATOR = new Creator<CommentDto>() {
            @Override
            public CommentDto createFromParcel(Parcel in) {
                return new CommentDto(in);
            }

            @Override
            public CommentDto[] newArray(int size) {
                return new CommentDto[size];
            }
        };

    public CommentDto(AddCommentRes addCommentRes) {
        this.avatar = addCommentRes.getAvatar();
        this.userName = addCommentRes.getUserName();
        this.rating = addCommentRes.getRating();
        this.comment = addCommentRes.getComment();
        this.date = new Date().toString();
    }

    @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(avatar);
            parcel.writeString(userName);
            parcel.writeFloat(rating);
            parcel.writeString(comment);
            parcel.writeString(date);
        }

        //endregion

        //region ============================== Getters and Setters ==============================

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
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

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        //endregion
}

