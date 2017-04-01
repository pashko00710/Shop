package me.uptop.mvpgoodpractice.data.storage.realm;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import me.uptop.mvpgoodpractice.data.managers.DataManager;
import me.uptop.mvpgoodpractice.data.managers.PreferencesManager;
import me.uptop.mvpgoodpractice.data.network.res.models.AddCommentRes;
import me.uptop.mvpgoodpractice.data.network.res.models.Comments;
import me.uptop.mvpgoodpractice.utils.RandomIdGenerator;

public class CommentRealm extends RealmObject implements Serializable {
    @PrimaryKey
    private String id;
    private String userName;
    private String avatar;
    private float rating;
    private Date commentDate;
    private String comment;

    //Необходимо для Realm
    public CommentRealm() {
    }

    public CommentRealm(AddCommentRes comments) {
        this.id = RandomIdGenerator.generateId();
        this.userName = comments.getUserName();
        this.avatar = comments.getAvatar();
        this.rating = comments.getRating();
        this.commentDate = new Date();
        this.comment = comments.getComment();
    }

    public CommentRealm(Comments comments) {
        this.id = comments.getId();
        this.userName = comments.getUserName();
        this.avatar = comments.getAvatar();
        this.rating = comments.getRating();
        this.commentDate = comments.getCommentDate();
        this.comment = comments.getComment();
    }

    public CommentRealm(float rating, String addComment) {
        this.id = String.valueOf(this.hashCode());
        final PreferencesManager pm = DataManager.getInstance()
                .getPreferencesManager();
        this.userName = pm.getUserName();
        this.avatar = pm.getUserAvatar();
        this.rating = rating;
        this.commentDate = new Date();
        this.comment = addComment;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public float getRating() {
        return rating;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public String getComment() {
        return comment;
    }
}
