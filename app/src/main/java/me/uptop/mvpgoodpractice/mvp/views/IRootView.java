package me.uptop.mvpgoodpractice.mvp.views;


import android.support.annotation.Nullable;

import me.uptop.mvpgoodpractice.data.storage.dto.UserInfoDto;

public interface IRootView extends IView {
    void showMessage (String message);
    void showError (Throwable e);

    void showLoad();
    void hideLoad();

    void showBasketCounter();

    @Nullable
    IView getCurrentScreen();

//    void pickAvatarFromGallery();
//    void pickAvatarFromCamera();
//    void setAvatarListener(RootActivity.AvatarListener avatarListener);
    void updateCartProductCounter();
    void initDrawer(UserInfoDto userInfoDto);
}
