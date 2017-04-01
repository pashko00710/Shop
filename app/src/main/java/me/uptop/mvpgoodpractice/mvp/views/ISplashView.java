package me.uptop.mvpgoodpractice.mvp.views;

import android.support.annotation.Nullable;

public interface ISplashView extends IView {
    void showMessage (String message);
    void showError (Throwable e);

    void showLoad();
    void hideLoad();

    @Nullable
    IView getCurrentScreen();
}
