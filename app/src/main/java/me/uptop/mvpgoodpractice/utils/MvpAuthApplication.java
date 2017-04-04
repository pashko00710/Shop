package me.uptop.mvpgoodpractice.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.vk.sdk.VKSdk;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.di.components.AppComponent;
import me.uptop.mvpgoodpractice.di.components.DaggerAppComponent;
import me.uptop.mvpgoodpractice.di.modules.AppModule;
import me.uptop.mvpgoodpractice.di.modules.PicassoCacheModule;
import me.uptop.mvpgoodpractice.di.modules.RootModule;
import me.uptop.mvpgoodpractice.mortar.ScreenScoper;
import me.uptop.mvpgoodpractice.ui.activities.DaggerRootActivity_RootComponent;
import me.uptop.mvpgoodpractice.ui.activities.RootActivity;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

public class MvpAuthApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "znfyj7ZC01x4HPvmZTAap7hEy";
    private static final String TWITTER_SECRET = "3ZSKY9YRwdguJ9Q7HMIREYatYr6B7XYFyBB25DgOVFl7iGTqxz";

    public static SharedPreferences sSharedPreferences;
    private static Context sContext;
    private static AppComponent appComponent;
    private MortarScope mMortarScope;
    private MortarScope mRootActivityScope;
    private static RootActivity.RootComponent mRootComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        Realm.init(this);
        VKSdk.initialize(this);

        sContext = getApplicationContext();
        createDaggerComponent();
        sSharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        createRootActivityComponent();

        mMortarScope = MortarScope.buildRootScope()
                .withService(DaggerService.SERVICE_NAME, appComponent)
                .build("Root");

        mRootActivityScope = mMortarScope.buildChild()
                .withService(DaggerService.SERVICE_NAME, mRootComponent)
                .withService(BundleServiceRunner.SERVICE_NAME, new BundleServiceRunner())
                .build(RootActivity.class.getName());

        ScreenScoper.registerScope(mMortarScope);
        ScreenScoper.registerScope(mRootActivityScope);
    }

    private void createRootActivityComponent() {
        mRootComponent = DaggerRootActivity_RootComponent.builder()
                .appComponent(appComponent)
                .rootModule(new RootModule())
                .picassoCacheModule(new PicassoCacheModule())
                .build();
    }

    @Override
    public Object getSystemService(String name) {
        if(mRootActivityScope != null) {
            return (mMortarScope != null && mMortarScope.hasService(name)) ? mMortarScope.getService(name) : super.getSystemService(name);
        } else {
            return super.getSystemService(name);
        }
    }

    public static RootActivity.RootComponent getRootActivityRootComponent() {
        return mRootComponent;
    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    public static Context getContext() {return sContext;}

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    private void createDaggerComponent() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(sContext))
                .build();
    }
}
