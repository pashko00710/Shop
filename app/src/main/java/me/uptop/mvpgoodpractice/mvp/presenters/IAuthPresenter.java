package me.uptop.mvpgoodpractice.mvp.presenters;

public interface IAuthPresenter {
//    void takeView(IAuthView authView);
//    void dropView();
//    void initView();
//
//    @Nullable
//    IAuthView getView();

    void clickOnLogin();
    void clickOnFb();
    void clickOnVk();
    void clickOnTwitter();
    void clickOnShowCatalog();

    void onPasswordChanged();
    void onEmailChanged();

    boolean checkUserAuth();

    void onLoginSuccess();
    void onLoginError(String message);

    //social facade

    void initSocialSdk();
    void onSocialResult(Object res, SocialSdkType type);
    void onSocialError(Object res, SocialSdkType type);
    void onSocialCancel();

    enum SocialSdkType {
        VK,
        FB,
        TWITTER
    }
}
