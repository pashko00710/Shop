package me.uptop.mvpgoodpractice.mvp.models;

import com.birbit.android.jobqueue.JobManager;

import me.uptop.mvpgoodpractice.data.managers.DataManager;
import me.uptop.mvpgoodpractice.data.network.req.UserLoginReq;
import me.uptop.mvpgoodpractice.data.network.res.UserRes;
import me.uptop.mvpgoodpractice.data.storage.dto.FbDataDto;
import me.uptop.mvpgoodpractice.data.storage.dto.TwDataDto;
import me.uptop.mvpgoodpractice.data.storage.dto.VKDataDto;
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

    public Observable<UserRes> signInUser(String email, String password) {
        return mDataManager.loginUser(new UserLoginReq(email, password));
    }

    public Observable<UserRes> signInVk(String accessToken, String userId, String email) {
        return mDataManager.signInVk(new VKDataDto(accessToken, userId, email));
    }

    public String getUserFullName() {
        return mDataManager.getUserFullName();
    }

    public Observable<UserRes> signInFb(String accsessToken, String userId) {
        return mDataManager.signInFb(new FbDataDto(accsessToken, userId));
    }

    @Override
    public Observable<UserRes> signInTwitter(String token, String userId) {
        return mDataManager.signInTw(new TwDataDto(token, userId));
    }

}
