package me.uptop.mvpgoodpractice.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import flow.Flow;
import me.uptop.mvpgoodpractice.BuildConfig;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.dto.UserInfoDto;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.flow.TreeKeyDispatcher;
import me.uptop.mvpgoodpractice.mortar.ScreenScoper;
import me.uptop.mvpgoodpractice.mvp.views.IRootView;
import me.uptop.mvpgoodpractice.mvp.presenters.RootPresenter;
import me.uptop.mvpgoodpractice.mvp.views.IView;
import me.uptop.mvpgoodpractice.ui.screens.auth.AuthScreen;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

public class SplashActivity extends AppCompatActivity implements IRootView {
    public static final String TAG = "SplashActivity";

    @Inject
    RootPresenter mRootPresenter;

    protected static ProgressDialog mProgressDialog;

    private int mEmailTextColor= Color.BLACK;
    private int mPasswordTextColor= Color.BLACK;

    @BindView(R.id.relative_container)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.root_frame)
    FrameLayout mRootFrame;
//
//    //region ---------Lifecycle---------------
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (savedInstanceState != null) {
            BundleServiceRunner.getBundleServiceRunner(this).onSaveInstanceState(savedInstanceState);
        }
        ButterKnife.bind(this);

        setTitle("Авторизация");

        DaggerService.<RootActivity.RootComponent>getDaggerComponent(this).inject(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BundleServiceRunner.getBundleServiceRunner(this).onSaveInstanceState(outState);
    }

    @Override
    public Object getSystemService(String name) {
        Log.e(TAG, "getSystemService: "+name);
        MortarScope splashActivityScope = MortarScope.findChild(getApplicationContext(), RootActivity.class.getName());
        return splashActivityScope.hasService(name) ? splashActivityScope.getService(name) : super.getSystemService(name);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = Flow.configure(newBase, this)
                .defaultKey(new AuthScreen(""))
                .dispatcher(new TreeKeyDispatcher(this))
                .install();
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onDestroy() {
        if(isFinishing()) {
            ScreenScoper.destroyScreenScope(AuthScreen.class.getName());
        }
        super.onDestroy();
    }

    //endregion

    //region ---------IAuthView---------------

    @Override
    public void showMessage(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        mRootPresenter.takeView(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mRootPresenter.dropView(this);
        super.onPause();
    }


    @Override
    public void showError(Throwable e) {
        if (BuildConfig.DEBUG) {
            showMessage(e.getMessage());
            e.printStackTrace();
        } else {
            showMessage(getString(R.string.unknown_error));
            //todo:send error stacktrace to crashlytics
        }

    }

    @Override
    public void showLoad() {
        if (mProgressDialog==null) {
            mProgressDialog=new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.show();
            mProgressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mProgressDialog.setContentView(R.layout.progress_root);
        } else {
            mProgressDialog.show();
            mProgressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mProgressDialog.setContentView(R.layout.progress_root);
        }

    }

    @Override
    public void hideLoad() {
        if (mProgressDialog!=null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.hide();
            }
        }
    }

    @Override
    public void showBasketCounter() {
        //
    }

    @Nullable
    @Override
    public IView getCurrentScreen() {
        return (IView) mRootFrame.getChildAt(0);
    }

//    @Override
//    public void pickAvatarFromGallery() {
//
//    }
//
//    @Override
//    public void pickAvatarFromCamera() {
//
//    }

    @Override
    public void updateCartProductCounter() {

    }

    @Override
    public void initDrawer(UserInfoDto userInfoDto) {

    }

//    @Override
//    public void setAvatarListener(RootActivity.AvatarListener avatarListener) {
//
//    }

    @Override
    public void onBackPressed() {
        if(getCurrentScreen() != null && !getCurrentScreen().viewOnBackPressed() && !Flow.get(this).goBack()) {
            super.onBackPressed();
        }
        overridePendingTransition(R.anim.enter_fade_in, R.anim.exit_push_out);
    }

    public void startRootActivity() {
        Intent intent = new Intent(this, RootActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    @Override
    public Context getContext() {
        return getContext();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mRootPresenter.onActivityResultHandler(requestCode, resultCode, data);
    }

}
