package me.uptop.mvpgoodpractice.mvp.presenters;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.fernandocejas.frodo.annotation.RxLogSubscriber;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.uptop.mvpgoodpractice.data.storage.dto.ActivityPermissionsResultDto;
import me.uptop.mvpgoodpractice.data.storage.dto.ActivityResultDto;
import me.uptop.mvpgoodpractice.data.storage.dto.UserInfoDto;
import me.uptop.mvpgoodpractice.mvp.models.AccountModel;
import me.uptop.mvpgoodpractice.mvp.models.RootModel;
import me.uptop.mvpgoodpractice.mvp.views.IRootView;
import me.uptop.mvpgoodpractice.ui.activities.RootActivity;
import me.uptop.mvpgoodpractice.ui.activities.SplashActivity;
import me.uptop.mvpgoodpractice.utils.MvpAuthApplication;
import mortar.Presenter;
import mortar.bundler.BundleService;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class RootPresenter extends Presenter<IRootView> {
    private static final String TAG = "RootPresenter";
    @Inject
    RootModel mRootModel;
    @Inject
    AccountModel mAccountModel;

    private Subscription userInfoSub;

    private static int DEFAULT_MODE = 0;
    private static int TAB_MODE = 1;


    public RootPresenter() {
        MvpAuthApplication.getRootActivityRootComponent().inject(this);
    }

    @Override
    protected BundleService extractBundleService(IRootView view) {
        return (view instanceof RootActivity) ?
                BundleService.getBundleService((RootActivity) view) :  //привязываем RootPresenter к RootActivity
                BundleService.getBundleService((SplashActivity) view); //привязываем RootPresenter к SplashActivity
    }

    private BehaviorSubject<ActivityResultDto> mActivityResultSubject = BehaviorSubject.create();
    private BehaviorSubject<ActivityPermissionsResultDto> mActivityPermissionsResultSubject = BehaviorSubject.create();
    private PublishSubject<ActivityResultDto> mActivityResultDtoObs = PublishSubject.create();

//    public BehaviorSubject<ActivityResultDto> getActivityResultSubject() {
//        return mActivityResultSubject;
//    }

    public BehaviorSubject<ActivityResultDto> getActivityResultSubject() {
        return mActivityResultSubject;
    }

    public BehaviorSubject<ActivityPermissionsResultDto> getActivityPermissionsResultSubject() {
        return mActivityPermissionsResultSubject;
    }

    public void onActivityResultHandler(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResultHandler requestCode=" + requestCode + " resultCode=" + resultCode);
        mActivityResultSubject.onNext(new ActivityResultDto(requestCode, resultCode, data));
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);

        if(getView() instanceof RootActivity) {
            userInfoSub = subscribeOnUserInfoObs();
        }
    }

    @Override
    public void dropView(IRootView view) {
        if(userInfoSub != null) {
            userInfoSub.unsubscribe();
        }
        super.dropView(view);
    }

    private Subscription subscribeOnUserInfoObs() {
        return mAccountModel.getUserInfoObs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UserInfoSubscriber());
    }

//    public int getCartProductCount() {
//        return mRootModel.loadCartProductCounter();
//    }
//
//    public void setCartProductCount(int cartProductCount) {
//        mRootModel.saveCartProductCounter(cartProductCount);
//    }

    @Nullable
    public IRootView getRootView() {
        return getView();
    }

    public ActionBarBuilder newActionBarBuilder() {
        return this.new ActionBarBuilder();
    }

    public void updateUserInfo() {
        mAccountModel.updateUserInfo();
    }

    @RxLogSubscriber
    private class UserInfoSubscriber extends Subscriber<UserInfoDto> {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            if (getView() != null) {
                getView().showError(e);
            }
        }

        @Override
        public void onNext(UserInfoDto userInfoDto) {
            if (getView() != null) {
                getView().initDrawer(userInfoDto);
            }
        }
    }

    public boolean checkPermissionsAndRequestIfNotGranted(@NonNull String[] permissions, int requestCode) {
        boolean allGranted = true;
        for (String permission : permissions) {
            int selfPermission = ContextCompat.checkSelfPermission(((RootActivity) getView()), permission);
            if (selfPermission != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((RootActivity) getView()).requestPermissions(permissions, requestCode);
            }
            return false;
        }
        return allGranted;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        mActivityResultDtoObs.onNext(new ActivityResultDto(requestCode, resultCode, intent));
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        // TODO: 05.12.2016 implement me
    }

    public class ActionBarBuilder {
        private boolean isGoBack = false;
        private boolean isVisible = true;
        private CharSequence title;
        private List<MenuItemHolder> items = new ArrayList<>();
        private ViewPager pager;
        private int toolbarMode = DEFAULT_MODE;

        public ActionBarBuilder setBackArrow(boolean enable) {
            this.isGoBack = enable;
            return this;
        }

        public ActionBarBuilder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public ActionBarBuilder addAction(MenuItemHolder menuItem) {
            this.items.add(menuItem);
            return this;
        }

        public ActionBarBuilder setTab(ViewPager pager) {
            this.toolbarMode = TAB_MODE;
            this.pager = pager;
            return this;
        }

        public void build() {
            if(getView() != null) {
                RootActivity activity = (RootActivity) getView();
                activity.setVisible(isVisible);
                activity.setTitle(title);
                activity.setBackArrow(isGoBack);
                activity.setMenuItem(items);
                if(toolbarMode == TAB_MODE) {
                    activity.setTabLayout(pager);
                } else {
                    activity.removeTabLayout();
                }
            }
        }
    }
}
