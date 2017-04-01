package me.uptop.mvpgoodpractice.mvp.models;

import android.util.Log;

import me.uptop.mvpgoodpractice.data.network.res.models.AddCommentRes;
import me.uptop.mvpgoodpractice.data.storage.realm.CommentRealm;
import me.uptop.mvpgoodpractice.jobs.SendMessageJob;

public class DetailModel extends AbstractModel {
    public void sendToServer(String id, AddCommentRes comments) {
//        mDataManager.sendCommentToServer(id, comments);
    }

    public void sendComment(String id, CommentRealm commentRealm) {
        Log.e("DetailModel", "sendComment: this");
        SendMessageJob job = new SendMessageJob(id, commentRealm);
        mJobManager.addJobInBackground(job);
    }
}
