package me.uptop.mvpgoodpractice.jobs;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import io.realm.Realm;
import me.uptop.mvpgoodpractice.data.managers.DataManager;
import me.uptop.mvpgoodpractice.data.network.res.models.AddCommentRes;
import me.uptop.mvpgoodpractice.data.storage.realm.CommentRealm;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;
import me.uptop.mvpgoodpractice.utils.ConstantManager;

public class SendMessageJob extends Job {
    private final CommentRealm mComment;
    private final String mProductId;
    private static final String TAG = "SendMessageJob";

    public SendMessageJob(String productId, CommentRealm comment) {
        super(new Params(JobPriority.MID) //средний приоритет задачи
        .requireNetwork() // необходимо соединение с сетью
        //.persist() //задача персистента(должна быть выполнена вне зависимости от состояния сети)
        .groupBy("Comments")); //группы задач - выполняются поочередно

        mComment = comment;
        mProductId = productId;
    }

    @Override
    public void onAdded() {
        //задача была добавлена
        Log.e(TAG, "onAdded: ");
        Realm realm = Realm.getDefaultInstance();
        ProductRealm product = realm.where(ProductRealm.class)
                .equalTo("id", mProductId)
                .findFirst();

        realm.executeTransaction(realm1 -> product.getCommentRealm().add(mComment));
        realm.close();
    }

    @Override
    public void onRun() throws Throwable {
        //задача начала выполнение
        Log.e(TAG, "onRun: ");
        // TODO: 23.01.17 send comment to server


        AddCommentRes comment = new AddCommentRes(mComment);
        DataManager.getInstance().sendComment(mProductId, comment)
                .subscribe(comments -> {
                    // TODO: 23.01.17 send to server
                    Realm realm = Realm.getDefaultInstance();
                    CommentRealm localComment = realm.where(CommentRealm.class)
                            .equalTo("id", mComment.getId())
                            .findFirst();
                    ProductRealm product = realm.where(ProductRealm.class)
                            .equalTo("id", mProductId)
                            .findFirst();

                    CommentRealm serverComment = new CommentRealm(comments);

                    realm.executeTransaction(realm1 -> {
                        if(localComment != null && localComment.isValid()) {
                            localComment.deleteFromRealm(); //удаляем комментарий из локальной базы
                        }
                        product.addComment(serverComment); // добавляем комментарий с сервера в базу
                    });

                    realm.close();
                });
    }

    @Override
    protected void onCancel(int i, @Nullable Throwable throwable) {
        //задача завершена
        Log.e(TAG, "onCancel: ");
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxCount) {
        //при ошибке, повторяем запросы
        return RetryConstraint.createExponentialBackoff(runCount, ConstantManager
                .INITIAL_BACK_OFF_IN_MS);
    }
}
