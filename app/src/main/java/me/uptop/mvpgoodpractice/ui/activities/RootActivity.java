package me.uptop.mvpgoodpractice.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import flow.Flow;
import me.uptop.mvpgoodpractice.BuildConfig;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.dto.UserInfoDto;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.di.components.AppComponent;
import me.uptop.mvpgoodpractice.di.modules.PicassoCacheModule;
import me.uptop.mvpgoodpractice.di.modules.RootModule;
import me.uptop.mvpgoodpractice.di.scopes.RootScope;
import me.uptop.mvpgoodpractice.flow.TreeKeyDispatcher;
import me.uptop.mvpgoodpractice.mortar.ScreenScoper;
import me.uptop.mvpgoodpractice.mvp.models.AccountModel;
import me.uptop.mvpgoodpractice.mvp.models.RootModel;
import me.uptop.mvpgoodpractice.mvp.presenters.MenuItemHolder;
import me.uptop.mvpgoodpractice.mvp.presenters.RootPresenter;
import me.uptop.mvpgoodpractice.mvp.views.IActionBarView;
import me.uptop.mvpgoodpractice.mvp.views.IRootView;
import me.uptop.mvpgoodpractice.mvp.views.IView;
import me.uptop.mvpgoodpractice.ui.screens.account.AccountScreen;
import me.uptop.mvpgoodpractice.ui.screens.auth.AuthScreen;
import me.uptop.mvpgoodpractice.ui.screens.cart.CartScreen;
import me.uptop.mvpgoodpractice.ui.screens.catalog.CatalogScreen;
import me.uptop.mvpgoodpractice.ui.screens.favorite.FavoriteScreen;
import me.uptop.mvpgoodpractice.utils.CircularTransformation;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

public class RootActivity extends AppCompatActivity implements IRootView, NavigationView.OnNavigationItemSelectedListener, IActionBarView {

    private static final String TAG = "RootActivity";
    protected static ProgressDialog mProgressDialog;

    @Inject
    RootModel mRootModel;
    @Inject
    RootPresenter mRootPresenter;
    @Inject
    Picasso mPicasso;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.relative_container)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.root_frame)
    FrameLayout mFrameLayout;
    @BindView(R.id.appbar_rootactivity)
    AppBarLayout mAppBarLayout;

    ImageView mAvatar;
    ActionBarDrawerToggle mToggle;

    private ActionBar mActionBar;
    private List<MenuItemHolder> mActionBarMenuItem;
    TextView countBusketView;
    int count;


    //region ---------Lifecycle---------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        BundleServiceRunner.getBundleServiceRunner(this).onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mNavigationView.getMenu().getItem(1).setChecked(true);

        RootComponent rootComponent = DaggerService.getDaggerComponent(this);
        rootComponent.inject(this);
        initToolbar();

        mRootPresenter.takeView(this);
//        mRootPresenter.initView();

//        initBasketCounter();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BundleServiceRunner.getBundleServiceRunner(this).onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        mRootPresenter.takeView(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mRootModel.saveBasketCounter(mRootModel.getBasketCounter());
        mRootPresenter.dropView(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mRootPresenter.dropView(this);
        if (isFinishing()) {
            ScreenScoper.destroyScreenScope(CatalogScreen.class.getName());
            ScreenScoper.destroyScreenScope(AccountScreen.class.getName());
            ScreenScoper.destroyScreenScope(AuthScreen.class.getName());
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        if(getCurrentScreen() != null && !getCurrentScreen().viewOnBackPressed() && !Flow.get(this).goBack()) {
            Snackbar.make(mCoordinatorLayout, "Вы уверены, что хотите выйти из приложения?", Snackbar.LENGTH_LONG)
                    .setAction("Выйти", view -> {
                        RootActivity.super.onBackPressed();
                        finish();
                    })
                    .setActionTextColor(Color.parseColor("#F44336"))
                    .show();
//            super.onBackPressed();
        }
        overridePendingTransition(R.anim.enter_fade_in, R.anim.exit_push_out);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = Flow.configure(newBase, this)
                .defaultKey(new CatalogScreen())
                .dispatcher(new TreeKeyDispatcher(this))
                .install();
        super.attachBaseContext(newBase);
    }

    @Override
    public Object getSystemService(String name) {
        MortarScope rootActivityScope = MortarScope.findChild(getApplicationContext(), RootActivity.class.getName());
        return rootActivityScope.hasService(name) ? rootActivityScope.getService(name) : super.getSystemService(name);
    }

    private int initBasketCounter() {
        try {
            count = mRootModel.getBasketCounter();
        } catch (NullPointerException e) {
            mRootModel.saveBasketCounter(0);
            count = 0;
//            basketCounter.setText(String.valueOf(0));
        }
        return count;
    }


    @Override
    public void initDrawer(UserInfoDto userInfoDto) {
        View header = mNavigationView.getHeaderView(0);
        mAvatar = (ImageView) header.findViewById(R.id.drawer_avatar);
        TextView userName = (TextView) header.findViewById(R.id.drawer_username);
        mPicasso.load(userInfoDto.getAvatar())
                .transform(new CircularTransformation())
                .into(mAvatar);

        userName.setText(userInfoDto.getName());
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.setDrawerListener(mToggle);

        mActionBar = getSupportActionBar();

        mToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    // метод для изменения конфигурации(Ориентация экрана, язык или доступность клаввиатуры)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void showBasketCounter() {
        countBusketView.setText(String.valueOf(mRootModel.getBasketCounter()));
    }

    @Nullable
    @Override
    public IView getCurrentScreen() {
        return (IView) mFrameLayout.getChildAt(0);
    }

    @Override
    public void updateCartProductCounter() {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Object key = null;
        switch (item.getItemId()){
            case R.id.nav_account:
                key = new AccountScreen();
                break;
            case R.id.nav_catalog:
                key = new CatalogScreen();
                break;
            case R.id.nav_favorite:
                key = new FavoriteScreen();
                break;
            case R.id.nav_shopping_basket:
                key = new CartScreen();
                break;
            case R.id.nav_notification:
                break;
        }
        if(key != null) {
//            Flow.get(this).replaceHistory(key, Direction.REPLACE);
            Flow.get(this).set(key);
        }

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }

        return true;
    }


    //region ================== IView ======================

    @Override
    public void showMessage(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
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
        if (mProgressDialog == null) {
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
        mRootPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRootPresenter.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    //endregion


    //region =========================== IActionBarView =====================
    @Override
    public void setTitle(CharSequence title) {

    }

    @Override
    public void setBackArrow(boolean enabled) {
        if(mToggle != null && mActionBar != null) {
            if(enabled) {
                mToggle.setDrawerIndicatorEnabled(false); //скрываем индикатор toggle
                mActionBar.setDisplayHomeAsUpEnabled(true); // устанавливаем индикатор тулбара
                if(mToggle.getToolbarNavigationClickListener() == null) {
                    mToggle.setToolbarNavigationClickListener(v -> onBackPressed()); //вешаем обработчик
                }
            } else {
                mActionBar.setDisplayHomeAsUpEnabled(false);// скрываем индикатор тулбара
                mToggle.setDrawerIndicatorEnabled(true); //активируем индикатор toggle
                mToggle.setToolbarNavigationClickListener(null); //зануляем обработчик на toggle
            }

            //если есть возможность вернуться назад(стрелка назад в Action bar) то блокируем раскрытие NavDrawer
            mDrawerLayout.setDrawerLockMode(
                    enabled ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
            mToggle.syncState();
        }
    }

    @Override
    public void setMenuItem(List<MenuItemHolder> items) {
        mActionBarMenuItem = items;
        supportInvalidateOptionsMenu();
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(mActionBarMenuItem != null && !mActionBarMenuItem.isEmpty()) {
            for(MenuItemHolder menuItem: mActionBarMenuItem) {
                MenuItem item = menu.add(menuItem.getTitle());
                item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                        .setActionView(menuItem.getIconResId())
                        .setOnMenuItemClickListener(menuItem.getMenuItemListener());
            }
        } else {
            menu.clear();
        }

        try {
            MenuItem m = menu.getItem(0);
            View v = m.getActionView();
            countBusketView = (TextView) v.findViewById(R.id.busket_count);

            initBasketCounter();
            showBasketCounter();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTabLayout(ViewPager pager) {
        View view = mAppBarLayout.getChildAt(1);
        TabLayout tabView;
        if(view == null) {
            tabView = new TabLayout(this); //создаем TabLayout
            tabView.setupWithViewPager(pager); //связываем его с ViewPager
            mAppBarLayout.addView(tabView); //добавляем табы в Appbar
            pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabView)); // регистрируем обработчик переключения по табам для ViewPager
        } else {
            tabView = (TabLayout) view;
            tabView.setupWithViewPager(pager); //связываем его с ViewPager
            pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabView));
        }
    }

    @Override
    public void removeTabLayout() {
        View tabView = mAppBarLayout.getChildAt(1);
        if(tabView != null && tabView instanceof TabLayout) { //проверяем если у аппбара есть дочерняя View являющаяся TabLayout
            mAppBarLayout.removeView(tabView); //то удаляем ее
        }
    }

    @Override
    public void setVisible(boolean visible) {

    }
    //endregion


    //region --------------------DI--------------------------------

    @dagger.Component(dependencies = AppComponent.class, modules = { RootModule.class, PicassoCacheModule.class })
    @RootScope
    public interface RootComponent {
        void inject(RootActivity rootActivity);
        void inject(SplashActivity splashActivity);
        void inject(RootPresenter rootPresenter);
        AccountModel getAccountModel();
        RootPresenter getRootPresenter();
        Picasso getPicasso();
    }
    //endregion

}
