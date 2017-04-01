package me.uptop.mvpgoodpractice.mvp.models;

import android.util.Log;

import com.birbit.android.jobqueue.JobManager;

import me.uptop.mvpgoodpractice.data.managers.DataManager;
import me.uptop.mvpgoodpractice.data.network.req.UserLoginReq;
import me.uptop.mvpgoodpractice.data.network.res.UserRes;
import rx.Observable;

public class AuthModel extends AbstractModel implements IAuthModel {
    public AuthModel() {
    }

    //for tests
    public AuthModel(DataManager dataManager, JobManager jobManager) {
        super(dataManager, jobManager);
    }

    public boolean isAuthUser() {
//        return mDataManager.getPreferencesManager().getAuthToken() != null;
//        Log.e("this", "isAuthUser: "+mDataManager.isAuthUser());
        return mDataManager.isAuthUser();
    }

    public void saveAuthToken(String authToken) {
        mDataManager.getPreferencesManager().saveAuthToken(authToken);
    }

    public Observable<UserRes> loginUser(String email, String password) {
        return mDataManager.loginUser(new UserLoginReq(email, password));
    }

}
