package me.uptop.mvpgoodpractice.ui.screens.auth;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import flow.Flow;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.di.components.AppComponent;
import me.uptop.mvpgoodpractice.di.components.DaggerAppComponent;
import me.uptop.mvpgoodpractice.di.modules.AppModule;
import me.uptop.mvpgoodpractice.di.modules.RootModule;
import me.uptop.mvpgoodpractice.mvp.models.AccountModel;
import me.uptop.mvpgoodpractice.mvp.models.AuthModel;
import me.uptop.mvpgoodpractice.mvp.models.RootModel;
import me.uptop.mvpgoodpractice.mvp.presenters.RootPresenter;
import me.uptop.mvpgoodpractice.ui.activities.DaggerRootActivity_RootComponent;
import me.uptop.mvpgoodpractice.ui.activities.RootActivity;
import me.uptop.mvpgoodpractice.ui.activities.SplashActivity;
import me.uptop.mvpgoodpractice.utils.ConstantManager;
import me.uptop.mvpgoodpractice.utils.NetworkStatusChecker;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;
import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AuthPresenterTest {
    @Mock
    AuthView mockView;
    @Mock
    AccountModel mockAccountModel;
    @Mock
    RootModel mockRootModel;
    @Mock
    Context mockContext;
    @Mock
    AuthModel mockModel;
    @Mock
    RootPresenter mockRootPresenter;
    @Mock
    SplashActivity mockRootView;
    @Mock
    ConstantManager mConstantManager;
    @Mock
    NetworkStatusChecker mNetworkStatusChecker;
    @Mock
    Flow mockFlow;
    @Mock(answer = Answers.RETURNS_SELF)
    RootPresenter.ActionBarBuilder mockActionBarBuilder;
//    @Rule
//    public RxJavaResetRule mRxJavaResetRule = new RxJavaResetRule();

    private BundleServiceRunner mockBundleServiceRunner;
    private MortarScope mockMortarScope;
    private AuthScreen.Component mTestAuthComponent;

    String mScreen = "Main";

    private AuthScreen.AuthPresenter mPresenter;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        prepareScope();
        prepareDependency();
        prepareStubs();
        prepareRxSchedulers();
        given(mockContext.getSystemService(BundleServiceRunner.SERVICE_NAME)).willReturn(mockBundleServiceRunner);//когда у контекста запрашивает системный сервис с названием SERVICE_NAME возвратить мокированный BundleServiceRunner
        given(mockContext.getSystemService(MortarScope.class.getName()))
                .willReturn(mockMortarScope); //когда запрашивается системный сервис с именем MortarScope вернуть замокированный скоп
        given(mockView.getContext()).willReturn(mockContext);
        given(mockRootPresenter.getRootView()).willReturn(mockRootView);


        RxJavaPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().reset();

        mPresenter = new AuthScreen.AuthPresenter(mockRootPresenter,mockModel, mScreen);
    }

    private void prepareRxSchedulers() {
        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate(); //без этого AndroidScheduler.mainThread -> NPE
            }
        });
    }

    private void prepareScope() {
        mockBundleServiceRunner = new BundleServiceRunner();
        mockMortarScope = MortarScope.buildRootScope()
                .withService(BundleServiceRunner.SERVICE_NAME, mockBundleServiceRunner)
                .withService(DaggerService.SERVICE_NAME, mock(AuthScreen.Component.class))
                .build("MockScope");
    }


    private void prepareDependency() {
        AppComponent testAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(mockContext))
                .build();


        RootActivity.RootComponent testRootComponent = DaggerRootActivity_RootComponent.builder()
                .appComponent(testAppComponent)
                .rootModule(new RootModule() {
//                    @Override
                    public RootModel provideRootModel() {
                        return mockRootModel;
                    }

//                    @Override
                    public AccountModel provideAccountModel() {
                        return mockAccountModel; //мок модель для RootPresenter можно переопределить инъекции даггера исспользуя новые testComponent и TestModule наследуемые это production Component/Modules
                    }

//                    @Override
                    public RootPresenter provideRootPresenter() {
                        return mockRootPresenter; //переопределяем презентер для вью не несет ключевого значения поэтому мока
                    }
                })
                .build();

        mTestAuthComponent = DaggerAuthScreen_Component.builder()
                .rootComponent(testRootComponent)
                .module(new AuthScreen.Module() {
                    @Override
                    AuthScreen.AuthPresenter provideAuthPresenter() {
                        return mock(AuthScreen.AuthPresenter.class);
                    }

                    @Override
                    AuthModel provideAuthModel() {
                        return mockModel;
                    }
                })
                .build();
    }

    //    private void prepareRxSchedulers() {
//        RxAndroidPlugins.getInstance().reset();
//        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
//            @Override
//            public Scheduler getMainThreadScheduler() {
//                return Schedulers.immediate(); //без этого AndroidScheduler.mainThread -> NPE
//            }
//        });
//    }

        private void prepareStubs() {
        //noinspection WrongConstant
        given(mockContext.getSystemService(BundleServiceRunner.SERVICE_NAME)).willReturn(mockBundleServiceRunner);
        //noinspection WrongConstant
        given(mockContext.getSystemService(MortarScope.class.getName())).willReturn(mockMortarScope);
        //noinspection WrongConstant
        given(mockContext.getSystemService("flow.InternalContextWrapper.FLOW_SERVICE")).willReturn(mockFlow);

        given(mockRootPresenter.getRootView()).willReturn(mockRootView);
        given(mockView.getContext()).willReturn(mockContext);
        given(mockModel.isAuthUser()).willReturn(false);

        given(mockRootPresenter.newActionBarBuilder()).willReturn(mockActionBarBuilder);
    }

    @Test
    public void onLoad_never_SHOW_ERROR() throws Exception {
        mPresenter.takeView(mockView);
        verify(mockRootView, never()).showError(any(Throwable.class));
    }

    @Test
    public void onLoad_isAuthUser_HIDE_LOGIN_BTN() throws Exception {
        given(mockModel.isAuthUser()).willReturn(true);
        mPresenter.takeView(mockView);
        verify(mockRootView, never()).showError(any(Throwable.class));
        verify(mockView, atMost(1)).hideLoginBtn();
    }
    @Test
    public void onLoad_notAuthUser_SHOW_LOGIN_BTN() throws Exception {
        given(mockModel.isAuthUser()).willReturn(false);
        mPresenter.takeView(mockView);
        verify(mockRootView, never()).showError(any(Throwable.class));
        verify(mockView, atMost(1)).showLoginBtn();
    }

    @Test
    public void clickOnLogin_isIdle_LOGIN_USER() throws Exception {
        mPresenter.takeView(mockView);
        given(mockView.isIdle()).willReturn(true);
        mPresenter.clickOnLogin();
        verify(mockView).showLoginWithAnim();
    }

    @Test
    public void clickOnShowCatalog() throws Exception {
        mPresenter.takeView(mockView);
        mPresenter.clickOnShowCatalog();
        verify(mockRootView).startRootActivity();
    }

    @Test
    public void checkUserAuth() throws Exception {
        mPresenter.checkUserAuth();
        verify(mockModel).isAuthUser();
    }

    @Test
    public void isValidEmail_true_TRUE() {
        assertTrue(mPresenter.isValidEmail("sask@mail.ru"));
    }

    @Test
    public void isValidEmail_false_FALSE() {
        assertFalse(mPresenter.isValidEmail("sa@mail"));
    }

    @Test
    public void clickOnLogin_notIdle_SIGN_IN_USER_REQUEST() throws Exception {
        //given
        String expectedEmail = "anys@email.ru";
        String expectedPassword = "anyPassword";
        mPresenter.takeView(mockView);
        given(mockView.isIdle()).willReturn(false);
        given(mockView.getEmail()).willReturn(expectedEmail);
        given(mockView.getPassword()).willReturn(expectedPassword);
        given(mockView.getContext().getString(anyInt())).willReturn("^[a-zA-Z_0-9]{3,}@[a-zA-Z_0-9.]{2,}\\.[a-zA-Z0-9]{2,}$");
        given(mockModel.signInUser(expectedEmail, expectedPassword)).willReturn(Observable.empty());

        //when
        mPresenter.clickOnLogin();

        //then
        then(mockModel).should(times(1)).signInUser(expectedEmail, expectedPassword);
        then(mockView).should(atLeast(1)).hideLoginBtn();
        then(mockView).should(atLeast(1)).showIdleWithAnim();
    }

    @Test
    public void clickOnLogin_isIdle_SHOW_LOGIN_WIH_ANIM() throws Exception {
        //given
        given(mockView.isIdle()).willReturn(true);
        mPresenter.takeView(mockView);

        //when
        mPresenter.clickOnLogin();

        //then
        then(mockView).should(times(1)).showLoginWithAnim();
    }


    @Test
    public void clickOnShowCatalog_anyAuthUser_RETURN_ON_CATALOG_SCREEN() throws Exception {
        //given
        assertNotNull(mockRootView); //проверка на null
        assertNotNull(mockView); //проверка на null
        mPresenter.takeView(mockView); //привязка к View

        //when
        mPresenter.clickOnShowCatalog(); //в зависимости от instanceof либо запускается анимация перехода, либо вызывается Flow.goBack()

        //then
        then(mockFlow).should(times(0)).goBack(); // так как у нас SplashActivity то у нас не вызывается этот метод, то есть вызывается 0 раз
    }





















//    @Mock
//    AccountModel mockAccountModel;
//    @Mock
//    RootPresenter mockRootPresenter;
//
//    @Mock
//    AuthView mockView;
//    @Mock
//    Context mockContext;
//    @Mock
//    AuthModel mockModel;
//
//    @Mock
//    RootActivity mockRootView;
//    @Mock
//    Flow mockFlow;
//
//    private AuthScreen.AuthPresenter mPresenter;
//    private BundleServiceRunner mBundleServiceRunner;
//    private MortarScope mMortarScope;
//    private AuthScreen.Component mTestAuthComponent;
//
//
//    @Before
//    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
//        prepareDependency(); //подготавливаем Dependency
//        prepareScope(); //подготавливаем Scope
//        prepareRxSchedulers(); //подготавливаем Shedulers
//        prepareStubs(); // подготавливаем заглушки
//
//        mPresenter = new AuthScreen.AuthPresenter("");
//    }

////
////    @Test
////    public void clickOnShowCatalog_anyAuthUser_RETURN_ON_CATALOG_SCREEN() throws Exception {
////        //given
////        mPresenter.takeView(mockView);
////
////        //when
////        mPresenter.clickOnShowCatalog();
////
////        //then
////        then(mockFlow).should(times(1)).goBack();
////    }
//
//    @Test
//    public void isValidEmail_true() throws Exception {
//        //given
//        String expectedTarget = "sas@mail.ru";
//        mPresenter.takeView(mockView);
//        given(mockView.getContext().getString(anyInt())).willReturn("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
//
//        //when
//        boolean actualResult = mPresenter.isValidEmail(expectedTarget);
//
//        //then
//        assertTrue(actualResult);
//    }
//
//    @Test
//    public void isValidEmail_false() throws Exception {
//        //given
//        String expectedTarget = "sas@mail";
//        mPresenter.takeView(mockView);
//        given(mockView.getContext().getString(anyInt())).willReturn("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
//
//        //when
//        boolean actualResult = mPresenter.isValidEmail(expectedTarget);
//
//        //then
//        assertFalse(actualResult);
//    }
//
//    @Test
//    public void onLoad_notAuthUser_SHOW_LOGIN_BYN() throws Exception {
//        //given
//        given(mockModel.isAuthUser()).willReturn(false);
//
//        //when
//        mPresenter.takeView(mockView);
//
//        //then
//        then(mockView).should().showLoginBtn();
//    }
//
//    @Test
//    public void onLoad_isAuthUser_HIDE_LOGIN_BTN() throws Exception {
//        //given
//        given(mockModel.isAuthUser()).willReturn(true);
//
//        //when
//        mPresenter.takeView(mockView);
//
//        //then
//        then(mockView).should().hideLoginBtn();
//    }







}