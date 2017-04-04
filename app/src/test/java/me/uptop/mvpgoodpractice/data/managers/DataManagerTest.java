package me.uptop.mvpgoodpractice.data.managers;

import com.squareup.moshi.Moshi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import me.uptop.mvpgoodpractice.data.network.RestService;
import me.uptop.mvpgoodpractice.data.network.error.ApiError;
import me.uptop.mvpgoodpractice.data.network.error.ForbiddenApiError;
import me.uptop.mvpgoodpractice.data.network.req.UserLoginReq;
import me.uptop.mvpgoodpractice.data.network.res.CommentJsonAdapter;
import me.uptop.mvpgoodpractice.data.network.res.ProductRes;
import me.uptop.mvpgoodpractice.data.network.res.UserRes;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;
import me.uptop.mvpgoodpractice.resources.StubEntityFactory;
import me.uptop.mvpgoodpractice.resources.TestResponses;
import me.uptop.mvpgoodpractice.utils.ConstantManager;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static me.uptop.mvpgoodpractice.data.managers.PreferencesManager.DEFAULT_LAST_UPDATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class DataManagerTest {
//    private Retrofit testRetrofit;
//    private RestService testRestService;
//    private MockWebServer testMockWebServer;
//    private DataManager testDataManager;
//    private TestSubscriber<UserRes> testSubscriber;
//
//    @Before
//    public void setUp() throws Exception {
//        testMockWebServer = new MockWebServer();
//        testRetrofit = new Retrofit.Builder()
//                .baseUrl(testMockWebServer.url("").toString())
//                .addConverterFactory(MoshiConverterFactory.create(new Moshi.Builder().build()))
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//для поддержки Rx
//                .client(new OkHttpClient.Builder().build()) //конфигурируем тестовый клиент
//                .build();
//
//        testRestService = testRetrofit.create(RestService.class);
////        testDataManager = new DataManager(testRestService);
//        testSubscriber = new TestSubscriber<>();
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        testMockWebServer.shutdown();
//        testSubscriber.unsubscribe();
//    }
//
//    @Test(timeout = 800)
//    public void loginUser_200_OK() throws Exception {
//        MockResponse mockResponse = new MockResponse()
//                .setHeader(ConstantManager.LAST_MODIFIED_HEADER, "Wed, 15 Nov 1995 04:58:08 GMT")
//                .setBody(MockResponses.USER_RES_200)
//                .throttleBody(512, 100, TimeUnit.MILLISECONDS);
//
//        testMockWebServer.enqueue(mockResponse);
//
//        testDataManager.signInUser(new UserLoginReq("anyemail@ya.ru", "password"))
//                .subscribe(userRes -> {
//                    assertNotNull(userRes);
//                    assertEquals("Вася", userRes.getFullName());
//                }, throwable -> Assert.fail());
//
//    }
//
//    @Test
//    public void loginUser_200_RX_COMPLETED() throws Exception {
//        MockResponse mockResponse = new MockResponse()
//                .setHeader(ConstantManager.LAST_MODIFIED_HEADER, "Wed, 15 Nov 1995 04:58:08 GMT")
//                .setBody(MockResponses.USER_RES_200);
//
//        testMockWebServer.enqueue(mockResponse);
//
//        testDataManager.signInUser(new UserLoginReq("anyemail@ya.ru", "password"))
//                .subscribe(testSubscriber);
//
//        testSubscriber.assertCompleted();
//        testSubscriber.assertValueCount(1);
//        testSubscriber.assertNoErrors();
//    }
//
//    @Test
//    public void loginUser_403_FORBIDDEN() throws Exception {
//        MockResponse mockResponse = new MockResponse().setResponseCode(403);
//
//        testMockWebServer.enqueue(mockResponse);
//
//        testDataManager.signInUser(new UserLoginReq("anyemail@ya.ru", "password"))
//                .subscribe(userRes -> Assert.fail(), throwable -> {
//                    assertNotNull(throwable);
//                    assertEquals("Неверный логин или пароль", throwable.getMessage());
//                });
//    }
//
//    @Test
//    public void loginUser_403_RX_THROWABLE() throws Exception {
//        MockResponse mockResponse = new MockResponse().setResponseCode(403);
//
//        testMockWebServer.enqueue(mockResponse);
//
//        testDataManager.signInUser(new UserLoginReq("anyemail@ya.ru", "password"))
//                .subscribe(testSubscriber);
//
//        testSubscriber.assertError(AccessError.class);
//    }
//
//
//    @Test
//    public void loginUser_500_UNKNOWN_API_EXCEPTION() throws Exception {
//        MockResponse mockResponse = new MockResponse().setResponseCode(500);
//
//        testMockWebServer.enqueue(mockResponse);
//
//        testDataManager.signInUser(new UserLoginReq("anyemail@ya.ru", "password"))
//                .subscribe(userRes -> Assert.fail(), throwable -> {
//                    assertNotNull(throwable);
//                    assertEquals("Неизвестная ошибка 500", throwable.getMessage());
//                });
//    }
//
//    @Test
//    public void loginUser_500_RX_THROWABLE() throws Exception {
//        MockResponse mockResponse = new MockResponse().setResponseCode(500);
//
//        testMockWebServer.enqueue(mockResponse);
//
//        testDataManager.signInUser(new UserLoginReq("anyemail@ya.ru", "password"))
//                .subscribe(testSubscriber);
//
//        testSubscriber.assertError(ApiError.class);
//    }










    private MockWebServer mMockWebServer;
    private RestService mRestService;
    private DataManager mDataManager;
    private Retrofit mRetrofit;

    @Before
    public void setUp() throws Exception {
        prepareMockServer();
        mRestService = mRetrofit.create(RestService.class);
        prepareRxSchedulers(); // для переопределения Schedulers Rx (при subscribeOn/observeOn)
    }

    //region ======================== prepare ========================
    private void prepareRxSchedulers() {
        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate(); //без этого AndroidScheduler.mainThread -> NPE
            }
        });
    }

    private void prepareMockServer() {
        mMockWebServer = new MockWebServer();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(mMockWebServer.url("").toString())
                .addConverterFactory(MoshiConverterFactory.create(new Moshi.Builder()
                        .add(new CommentJsonAdapter())
                        .build()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build();
    }

    private void prepareDispatcher_200() {

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String path = request.getPath(); // получаем path запрса
                switch (path) {
                    case "/login":   //RestService "/" + path  !!!!
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody(TestResponses.SUCSESS_USER_RES_WITH_ADDRESS);

                    case "/products":
                        return new MockResponse()
                                .setResponseCode(200)
                                .setHeader(ConstantManager.LAST_MODIFIED_HEADER, DEFAULT_LAST_UPDATE)
                                .setBody(TestResponses.SUCCSESS_GET_PRODUCTS);
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        };

        mMockWebServer.setDispatcher(dispatcher);
    }
    //endregion


    @After
    public void tearDown() throws Exception {
        mMockWebServer.shutdown();
    }

    @Test
    public void signInUser_200_SUCCSESS_USER_RES() throws Exception {
        //given
        prepareDispatcher_200(); // устанавливаем диспетчер запросов
        PreferencesManager mockPrefManager = mock(PreferencesManager.class); //мокируем преференс менеджер (туда сохраняется дата последнего обновления сущности (пригодится при тестировании товаров))
        mDataManager = new DataManager(mRestService, mockPrefManager, null); // создаем DataManager
        UserLoginReq stubUserLogin = StubEntityFactory.makeStub(UserLoginReq.class); //создаем заглушку с тестовыми данными на авторизацию пользователя
        UserRes expectedUserRes = StubEntityFactory.makeStub(UserRes.class); // ожидаемый объект из запрса
        TestSubscriber<UserRes> subscriber = new TestSubscriber<>();

        //when
        mDataManager.loginUser(stubUserLogin)
                .subscribe(subscriber); // подписываемся тестовым сабскрайбером
        subscriber.awaitTerminalEvent(); //дожидаемся окончания последовательности
        UserRes actualRes = subscriber.getOnNextEvents().get(0); // получаем первый и единственный элемент последовательности

        //then
        subscriber.assertNoErrors(); //не должен вернуть ошибок
        assertEquals(expectedUserRes.getFullName(), actualRes.getFullName()); // проверяем значения полей ожидаемого объекта и фактического
        assertEquals(expectedUserRes.getId(), actualRes.getId()); // проверяем значения полей ожидаемого объекта и фактического
        assertEquals(expectedUserRes.getToken(), actualRes.getToken()); // проверяем значения полей ожидаемого объекта и фактического
        assertEquals(expectedUserRes.getPhone(), actualRes.getPhone()); // проверяем значения полей ожидаемого объекта и фактического
        assertEquals(expectedUserRes.getAvatarUrl(), actualRes.getAvatarUrl()); // проверяем значения полей ожидаемого объекта и фактического
        assertFalse(actualRes.getAddresses().isEmpty()); // проверяем что адреса не пустые
        then(mockPrefManager).should(times(1)).saveProfileInfo(actualRes);
    }

    @Test
    public void sigInUser_403_FORBIDDEN() throws Exception {
        //given
        mMockWebServer.enqueue(new MockResponse().setResponseCode(403));
        mDataManager = new DataManager(mRestService, null, null); // создаем DataManager
        UserLoginReq stubUserLogin = StubEntityFactory.makeStub(UserLoginReq.class); //создаем заглушку с тестовыми данными на авторизацию пользователя
        TestSubscriber<UserRes> subscriber = new TestSubscriber<>();

        //when
        mDataManager.loginUser(stubUserLogin)
                .subscribe(subscriber); // подписываемся тестовым сабскрайбером
        subscriber.awaitTerminalEvent(); //дожидаемся окончания последовательности
        Throwable actualThrow = subscriber.getOnErrorEvents().get(0); // получаем Ошибку

        //then
        subscriber.assertError(ForbiddenApiError.class);
        assertEquals("Неверный логин или пароль", actualThrow.getMessage());
    }

    @Test
    public void sigInUser_500_API_ERROR() throws Exception {
        //given
        mMockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mDataManager = new DataManager(mRestService, null, null); // создаем DataManager
        UserLoginReq stubUserLogin = StubEntityFactory.makeStub(UserLoginReq.class); //создаем заглушку с тестовыми данными на авторизацию пользователя
        TestSubscriber<UserRes> subscriber = new TestSubscriber<>();

        //when
        mDataManager.loginUser(stubUserLogin)
                .subscribe(subscriber); // подписываемся тестовым сабскрайбером
        subscriber.awaitTerminalEvent(); //дожидаемся окончания последовательности

        //then
        subscriber.assertError(ApiError.class);
    }

    @Test
    public void getProductFromNetwork_200_RECORD_RESPONSE_TO_REALM_MANAGER() throws Exception {
        //given
        prepareDispatcher_200();
        PreferencesManager mockPrefManager = mock(PreferencesManager.class); //мокируем преференс менеджер (туда сохраняется дата последнего обновления сущности)
        given(mockPrefManager.getLastProductUpdate()).willReturn(DEFAULT_LAST_UPDATE);
        RealmManager mockRealmManager = mock(RealmManager.class); //мокируем реалм менеджер
        mDataManager = new DataManager(mRestService, mockPrefManager, mockRealmManager); // создаем DataManager
        TestSubscriber<ProductRealm> subscriber = new TestSubscriber<>();

        //when
        mDataManager.getProductsObsFromNetwork()
                .subscribe(subscriber);
        subscriber.awaitTerminalEvent(); // ждем окончания последовательности

        //then
        subscriber.assertNoErrors(); //без ошибок
        subscriber.assertCompleted(); //последовательность холодная - должна завершиться
        subscriber.assertNoValues(); //без значений (последовательность сохраняется в Realm и возвращается пустая последовательность

        then(mockPrefManager).should(times(1)).saveLastProductUpdate(anyString()); //должена быть сохранена дата последнего обновления сущности
        then(mockRealmManager).should(times(2)).deleteFromRealm(any(),anyString()); // один неактивный товар должен быть удален из базы
        then(mockRealmManager).should(times(6)).saveProductResponseToRealm(any(ProductRes.class)); // 6 активных товаров должны быть сохранены
    }

    @Test
    public void getProductFromNetwork_304_NOT_RECORD_TO_REALM_MANAGER() throws Exception {
        //given
        mMockWebServer.enqueue(new MockResponse().setResponseCode(304));
        PreferencesManager mockPrefManager = mock(PreferencesManager.class); //мокируем преференс менеджер (туда сохраняется дата последнего обновления сущности)
        given(mockPrefManager.getLastProductUpdate()).willReturn(DEFAULT_LAST_UPDATE);
        RealmManager mockRealmManager = mock(RealmManager.class); //мокируем реалм менеджер
        mDataManager = new DataManager(mRestService, mockPrefManager, mockRealmManager); // создаем DataManager
        TestSubscriber<ProductRealm> subscriber = new TestSubscriber<>();

        //when
        mDataManager.getProductsObsFromNetwork()
                .subscribe(subscriber);
        subscriber.awaitTerminalEvent(); // ждем окончания последовательности

        //then
        subscriber.assertNoErrors(); //без ошибок
        subscriber.assertCompleted(); //последовательность холодная - должна завершиться
        subscriber.assertNoValues(); //без значений (последовательность сохраняется в Realm и возвращается пустая последовательность

        then(mockRealmManager).should(never()).deleteFromRealm(any(),anyString()); // никогда не вызывается
        then(mockRealmManager).should(never()).saveProductResponseToRealm(any(ProductRes.class)); // никогда не вызывается
    }


}