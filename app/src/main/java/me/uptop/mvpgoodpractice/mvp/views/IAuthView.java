package me.uptop.mvpgoodpractice.mvp.views;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public interface IAuthView extends IView {
    void errorAuthForm();

    void btnAnimation(View view, Button button);
    void btnAnimation(View view, ImageButton imageButton);

//    IAuthPresenter getPresenter();

    void showLoginBtn();
    void hideLoginBtn();

    String getEmail();
    String getPassword();

    void setNonAcceptableEmail();
    void setNonAcceptablePassword();

    void setAcceptableEmail();
    void setAcceptablePassword();

    void setWrongEmailError();
    void setWrongPasswordError();

    void removeWrongEmailError();
    void removeWrongPasswordError();

    Context getContext();

//    @Nullable
//    AuthPanel getAuthPanel();

    void showCatalogScreen();

    String getUserEmail();
    String getUserPassword();

    boolean isIdle();

    void showLoginWithAnim();
//    void setCustomState(int state);
}
