package me.uptop.mvpgoodpractice.ui.screens.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKServiceActivity;
import com.vk.sdk.api.VKError;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import dagger.Provides;
import flow.Flow;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.di.scopes.DaggerScope;
import me.uptop.mvpgoodpractice.flow.AbstractScreen;
import me.uptop.mvpgoodpractice.flow.Screen;
import me.uptop.mvpgoodpractice.mvp.models.AuthModel;
import me.uptop.mvpgoodpractice.mvp.models.IAuthModel;
import me.uptop.mvpgoodpractice.mvp.presenters.IAuthPresenter;
import me.uptop.mvpgoodpractice.mvp.presenters.RootPresenter;
import me.uptop.mvpgoodpractice.mvp.views.IRootView;
import me.uptop.mvpgoodpractice.ui.activities.RootActivity;
import me.uptop.mvpgoodpractice.ui.activities.SplashActivity;
import me.uptop.mvpgoodpractice.utils.ConstantManager;
import mortar.MortarScope;
import mortar.ViewPresenter;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.support.annotation.VisibleForTesting.NONE;

@Screen(R.layout.screen_auth)
public class AuthScreen extends AbstractScreen<RootActivity.RootComponent> {
    private int mCustomState = 1;
    private static String screen;

    public AuthScreen(String screen) {
        this.screen = screen;
    }

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
//        return null;
        return DaggerAuthScreen_Component.builder()
                .rootComponent(parentComponent)
                .module(new Module())
                .build();
    }

    public int getCustomState() {
        return mCustomState;
    }

    public void setCustomState(int mCustomState) {
        this.mCustomState = mCustomState;
    }

    //region ============================= DI =============================
    @dagger.Module
    public static class Module {
        @Provides
        @DaggerScope(AuthScreen.class)
        AuthScreen.AuthPresenter provideAuthPresenter() {
            return new AuthScreen.AuthPresenter(screen);
        }

        @Provides
        @DaggerScope(AuthScreen.class)
        IAuthModel provideAuthModel() {
            return new AuthModel();
        }
    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(AuthScreen.class)
    public interface Component {
        void inject(AuthPresenter authPresenter);
        void inject(AuthView authView);
    }

    //endregion

    //region ============================= Presenter =============================

    public static class AuthPresenter extends ViewPresenter<AuthView> implements IAuthPresenter {
        private boolean emailOk = false;
        private boolean passwordOk = false;
        private boolean mIsCatalogLoading=false;
        private boolean mTestMode;
        private CompositeSubscription mCompositeSubscription;
        private LoginManager mLoginManager;
        private CallbackManager mCallbackManager;
        private TwitterAuthClient client = new TwitterAuthClient();

        @Inject
        RootPresenter mRootPresenter;
        @Inject
        IAuthModel mAuthModel;

        private String mScreen;


        //for test
        public AuthPresenter(RootPresenter mRootPresenter, IAuthModel mAuthModel, String screen) {
            this.mRootPresenter = mRootPresenter;
            this.mAuthModel = mAuthModel;
            this.mScreen = screen;
        }



        public AuthPresenter(String screen) {
            mScreen = screen;
        }



        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            mCompositeSubscription = new CompositeSubscription();

            if(mScreen.contains("Catalog")) {
                mRootPresenter.newActionBarBuilder()
                        .setTitle("Авторизация")
                        .setBackArrow(true)
                        .build();
            }


            if(getView() != null) {
                if(checkUserAuth() && getRootView() != null) {
                    getView().hideLoginBtn();
                } else {
                    getView().showLoginBtn();
                }
            } else {
                getRootView().showError(new NullPointerException("Что то пошло не так"));
            }

            if (!mTestMode) {
                initSocialSdk();
            }
        }

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            ((Component)scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        //for tests
        public boolean getEmailOk() {
            return emailOk;
        }

        //for tests
        public boolean getPasswordOk() {
            return passwordOk;
        }

        //        @Override
//        protected void initActionBar() {
//
//        }
//
//        @Override
//        protected void initDagger(MortarScope scope) {
//            ((Component)scope.getService(DaggerService.SERVICE_NAME)).inject(this);
//        }

        @Nullable
        private IRootView getRootView() {
            return mRootPresenter.getRootView();
        }

        @Override
        public void clickOnLogin() {
            if (getView()!=null && getRootView() != null) {
                if (getView().isIdle()) {
                    getView().showLoginWithAnim();
                } else {
                    emailOk = getView().getEmail().matches(ConstantManager.PATTERN_EMAIL);
                    passwordOk = getView().getPassword().matches(ConstantManager.PATTERN_PASSWORD);
                    if (emailOk && passwordOk) {
                        getRootView().showMessage(getView().getContext().getString(R.string.user_authenticating_message));
                        loginUser(getView().getEmail(), getView().getPassword());
//                    mAuthModel.signInUser(getView().getAuthPanel().getUserEmail(), getView().getAuthPanel().getUserPassword());
                    } else {
                        getView().errorAuthForm();
                        if (!emailOk) getView().setWrongEmailError();
                        if (!passwordOk) getView().setWrongPasswordError();
                        getRootView().showMessage(getView().getContext().getString(R.string.email_or_password_wrong_format));
                    }
                }
            }
        }

        private void loginUser(String userEmail, String userPassword) {
//            if (NetworkStatusChecker.isNetworkAvailable()) {
//                Observable<UserRes> code = mAuthModel.signInUser(userEmail, userPassword);
//                code.map(UserRes::getFullName)
//                        .subscribe(new Subscriber<String>() {
//                            @Override
//                            public void onCompleted() {
//                                Log.e("lul", "onError: success");
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                Log.e("lul", "onError: "+e.getMessage());
//                            }
//
//                            @Override
//                            public void onNext(String s) {
//
//                            }
//                        });

            //for unit tests
            mAuthModel.signInUser(userEmail, userPassword)
                    .subscribe(userRes -> {},
                            throwable -> {
                                getRootView().showError(throwable);
                            }, this::onLoginSuccess);

                mAuthModel.saveAuthToken("authenticated");
                onLoginSuccess();
//            } else {
//                onLoginError(String.valueOf(String.valueOf(R.string.error_network_failure)));
//            }
        }

        @Override
        public void clickOnFb() {
            if (!mAuthModel.isAuthUser()) {
                if (mLoginManager !=null) {
                    mLoginManager.logInWithReadPermissions(((Activity) getRootView()), Arrays.asList("email"));
                }
            } else {
                if (getRootView() != null) {
                    getRootView().showMessage("Вы ужe авторизованы как " + mAuthModel.getUserFullName());
                }
            }
        }

        @Override
        public void clickOnVk() {
            if (!mAuthModel.isAuthUser()) {
                Activity runningActivity = (Activity) getRootView();
                VKSdk.login(runningActivity, "email");
            } else {
                if (getRootView() != null) {
                    getRootView().showMessage("Вы ужe авторизованы как " + mAuthModel.getUserFullName());
                }
            }
        }

        @Override
        public void clickOnTwitter() {
            if (!mAuthModel.isAuthUser()) {
                Activity runningActivity = (Activity) getRootView();
                TwitterAuthConfig config = new TwitterAuthConfig("znfyj7ZC01x4HPvmZTAap7hEy", "3ZSKY9YRwdguJ9Q7HMIREYatYr6B7XYFyBB25DgOVFl7iGTqxz");
                TwitterCore core = new TwitterCore(config);
                core.logIn(runningActivity, callback);
            } else {
                if (getRootView() != null) {
                    getRootView().showMessage("Вы ужe авторизованы как " + mAuthModel.getUserFullName());
                }
            }
        }

        Callback<TwitterSession> callback = new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                onSocialResult(result, SocialSdkType.TWITTER);
            }

            @Override
            public void failure(TwitterException exception) {
                onSocialError(exception, SocialSdkType.TWITTER);
            }
        };

        @Override
        public void clickOnShowCatalog() {
            mIsCatalogLoading = true;
            if (getView() != null && getRootView() != null) {
                getRootView().showMessage(getView().getContext().getString(R.string.catalog_loading_message));
//                getRootView().showLoad();
//                class WaitSplash extends AsyncTask<Void, Void, Void> {
//                    protected Void doInBackground(Void... params) {
//                        try {
//                            Thread.currentThread();
//                            Thread.sleep(3000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        return null;
//                    }
//                    protected void onPostExecute(Void result) {
//                        super.onPostExecute(result);
//                        mIsCatalogLoading = false;
//                        if (getView()!=null) {
//                            getRootView().hideLoad();
//                            getRootView().showMessage(getView().getContext().getString(R.string.catalog_loaded_message));
//                            if(getRootView() instanceof RootActivity) {
//                                ((RootActivity) getRootView()).onBackPressed();
//                            }
//                        }
//                    }
//                }
//                WaitSplash waitSplash = new WaitSplash();
//                waitSplash.execute();

                if(getRootView() instanceof SplashActivity) {
                    ((SplashActivity) getRootView()).startRootActivity();
                    ((SplashActivity) getRootView()).overridePendingTransition(R.anim.enter_pull_in, R.anim.exit_fade_out);
                } else {
                    //noinspection CheckResult
                    Flow.get(getView()).goBack();
                }
            }



        }

        @Override
        public void onPasswordChanged() {
//            Log.d(TAG, "onPasswordChanged: "+getView() + getView().getPassword().toString());
            if (getView()!=null) {
                if (getView().getPassword().matches(ConstantManager.PATTERN_PASSWORD)) {
                    getView().setAcceptablePassword();
                }
                else {
                    getView().setNonAcceptablePassword();
                }

            }
        }

        @Override
        public void onEmailChanged() {
            if (getView()!=null) {
                if (getView().getEmail().matches(ConstantManager.PATTERN_EMAIL))
                    getView().setAcceptableEmail();
                else
                    getView().setNonAcceptableEmail();
            }
        }

        @Override
        public boolean checkUserAuth() {
            return mAuthModel.isAuthUser();
        }

        @Override
        public void onLoginSuccess() {
            if (getView()!=null && getRootView()!=null) {
                getRootView().showMessage(getView().getContext().getString(R.string.authentificate_successful));
                getView().hideLoginBtn();
                getView().showIdleWithAnim();
            }
        }

        @Override
        public void onLoginError(String message) {
            if (getRootView()!=null) {
                getRootView ().showMessage(message);
            }
        }

        final FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                onSocialResult(loginResult, SocialSdkType.FB);
            }

            @Override
            public void onCancel() {
                onSocialCancel();
            }

            @Override
            public void onError(FacebookException error) {
                onSocialError(error, SocialSdkType.FB);
            }
        };

        @Override
        public void initSocialSdk() {
            if (mLoginManager == null) {
                mLoginManager  = LoginManager.getInstance();
            }
            if (mCallbackManager == null) {
                mCallbackManager = CallbackManager.Factory.create();
                mLoginManager.registerCallback(mCallbackManager, facebookCallback);
            }
            mCompositeSubscription.add(subscribeOnActivityResult());
        }

        private Subscription subscribeOnActivityResult() {
            return mRootPresenter.getActivityResultSubject()
                    .subscribe(activityResultDto -> {
                                Log.e("ACTIVITYRESULT", "subscribeOnActivityResult: ");
                                final int requestCode = activityResultDto.getRequestCode();
                                Log.e("ACTIVITYRESULT", "subscribeOnActivityResult: "+requestCode);
                                final int resultCode = activityResultDto.getResultCode();
                                final Intent intent = activityResultDto.getIntent();
                                if (requestCode == VKServiceActivity.VKServiceType.Authorization.getOuterCode()) {
                                    VKSdk.onActivityResult(requestCode, resultCode, intent, vkCallback); // передаем результат авторизации в VK SDK
                                }

                                if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
                                    mCallbackManager.onActivityResult(requestCode, resultCode, intent);
                                }

                                if(requestCode == client.getRequestCode()) {
                                    Log.e("ACTIVITYRESULT", "subscribeOnActivityResult: norm");
                                    client.onActivityResult(requestCode, resultCode, intent);
                                }

                                if (resultCode == Activity.RESULT_CANCELED &&
                                        (requestCode == VKServiceActivity.VKServiceType.Authorization.getOuterCode()
                                                || requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()
                                                || requestCode == client.getRequestCode())) {
                                    onSocialCancel();
                                }
                            },
                            throwable -> {
                                getRootView().showError(throwable);
                            });

        }

        final VKCallback<VKAccessToken> vkCallback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                onSocialResult(res, SocialSdkType.VK);
            }

            @Override
            public void onError(VKError error) {
                onSocialError(error, SocialSdkType.VK);
            }
        };

        @VisibleForTesting(otherwise = NONE)
        public void testSocial() {
            mTestMode = true;
        }

        @Override
        public void onSocialResult(Object res, SocialSdkType type) {
            switch (type) {

                case VK:
                    VKAccessToken vkRes = ((VKAccessToken) res);
                    final String accessToken = vkRes.accessToken;
                    final String userId = vkRes.userId;
                    final String email = vkRes.email;
                    mAuthModel.signInVk(accessToken, userId, email )
                            .subscribe(userRes -> {
                                        if (getRootView() != null) {
                                            getRootView().showMessage("Вы успешно авторизованы как " + userRes.getFullName());
                                        }
                                    },
                                    throwable -> {
                                        if (getRootView() != null) {
                                            getRootView().showError(throwable);
                                        }
                                    },
                                    () -> {
                                        mRootPresenter.updateUserInfo();
                                    });
                    break;
                case FB:
                    LoginResult fbRes = ((LoginResult) res);
                    mAuthModel.signInFb(fbRes.getAccessToken().getToken(), fbRes.getAccessToken().getUserId()).subscribe(userRes -> {
                                if (getRootView() != null) {
                                    Log.e("FB", "onSocialResult: ");
                                    getRootView().showMessage("Вы успешно авторизованы как " + userRes.getFullName());
                                }
                            },
                            throwable -> {
                                if (getRootView() != null) {
                                    getRootView().showError(throwable);
                                }
                            },
                            () -> {
                                mRootPresenter.updateUserInfo();
                            });

                    break;
                case TWITTER:
//                    Activity activity = (Activity) getRootView();
//                    Handler mainHandler = new Handler(getView().getContext().getMainLooper());
//
//                    Runnable myRunnable = () -> {
//                        client.authorize(activity, callback);
//                    };
//                    mainHandler.post(myRunnable);
//                    client.authorize(activity, callback);
//                    TwitterSession twToken = ((TwitterSession) res);
                    Result<TwitterSession> twToken = ((Result) res);
                    mAuthModel.signInTwitter(twToken.data.getAuthToken().toString(), String.valueOf(twToken.data.getUserId())).subscribe(userRes -> {
                                if (getRootView() != null) {
                                    Log.e("TW", "onSocialResult: ");
                                    getRootView().showMessage("Вы успешно авторизованы как " + userRes.getFullName());
                                }
                            },
                            throwable -> {
                                if (getRootView() != null) {
                                    getRootView().showError(throwable);
                                }
                            },
                            () -> {
                                mRootPresenter.updateUserInfo();
                            });
                    break;
            }
        }

        @Override
        public void onSocialError(Object res, SocialSdkType type) {

        }

        @Override
        public void onSocialCancel() {
            if (getRootView() != null) {
                getRootView().showMessage("Авторизация отменена");
            }
        }

        //for tests
        public boolean isValidEmail(CharSequence target) {
            Pattern pattern = Pattern.compile(ConstantManager.PATTERN_EMAIL);
            Matcher matcher = pattern.matcher(target);
            return matcher.matches() ;
        }

    }

    //endregion
}
