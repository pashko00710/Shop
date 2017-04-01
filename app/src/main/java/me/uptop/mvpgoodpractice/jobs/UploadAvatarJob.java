package me.uptop.mvpgoodpractice.jobs;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import java.io.File;

import id.zelory.compressor.Compressor;
import me.uptop.mvpgoodpractice.data.managers.DataManager;
import me.uptop.mvpgoodpractice.utils.ConstantManager;
import me.uptop.mvpgoodpractice.utils.LocalStorageAvatar;
import me.uptop.mvpgoodpractice.utils.MvpAuthApplication;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UploadAvatarJob extends Job {
    private static final String TAG = "UploadAvatarJob";
    private final String mImageUri;

    public UploadAvatarJob(String imageUri) {
        super(new Params(JobPriority.HIGH)
        .requireNetwork()
        .persist()
        );

        mImageUri = imageUri;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onAdded() {
        Log.e(TAG, "PRODUCT-AVATAR onAdded: ");
        File file = null;

        try {
            file = new File(LocalStorageAvatar.getPath(MvpAuthApplication.getContext(), Uri.parse(mImageUri)));
            file = Compressor.getDefault(MvpAuthApplication.getContext()).compressToFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(file != null) {
            RequestBody sendFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            MultipartBody.Part mBody = MultipartBody.Part.createFormData("avatar", file.getName(), sendFile);

            DataManager.getInstance()
                    .uploadUserPhoto(mBody)
                    .subscribe(avatarUrlRes -> {
                        DataManager.getInstance().getPreferencesManager().saveUserAvatar(avatarUrlRes.getAvatarUrl());
                    });
        } else {
            Observable.just("Повторите загрузку аватара, произошла ошибка")
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(message -> {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    });
        }
    }

    @Override
    public void onRun() throws Throwable {
        Log.e(TAG, "AVATAR onRun: ");
    }

    @Override
    protected void onCancel(int i, @Nullable Throwable throwable) {
        Log.e(TAG, "AVATAR onCancel: ");
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return RetryConstraint.createExponentialBackoff(runCount, ConstantManager
                .INITIAL_BACK_OFF_IN_MS);
    }
}
