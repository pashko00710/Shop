package me.uptop.mvpgoodpractice.mvp.models;

import me.uptop.mvpgoodpractice.data.network.res.UserRes;
import rx.Observable;

public interface IAuthModel {
    boolean isAuthUser();
    void saveAuthToken(String authToken);

    Observable<UserRes> signInUser(String email, String password);

    String getUserFullName();

    Observable<UserRes> signInVk(String accessToken, String userId, String email);

    Observable<UserRes> signInFb(String token, String userId);

    Observable<UserRes> signInTwitter(String token, String userId);
}
