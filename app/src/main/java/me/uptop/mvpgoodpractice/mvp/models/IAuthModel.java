package me.uptop.mvpgoodpractice.mvp.models;

import me.uptop.mvpgoodpractice.data.network.res.UserRes;
import rx.Observable;

public interface IAuthModel {
    boolean isAuthUser();
    void saveAuthToken(String authToken);

    Observable<UserRes> loginUser(String email, String password);
}
