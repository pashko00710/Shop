package me.uptop.mvpgoodpractice.data.managers;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.realm.RealmResults;
import me.uptop.mvpgoodpractice.data.network.RestCallTransformer;
import me.uptop.mvpgoodpractice.data.network.RestService;
import me.uptop.mvpgoodpractice.data.network.req.UserLoginReq;
import me.uptop.mvpgoodpractice.data.network.req.UserSignInReq;
import me.uptop.mvpgoodpractice.data.network.res.AvatarUrlRes;
import me.uptop.mvpgoodpractice.data.network.res.FbProfileRes;
import me.uptop.mvpgoodpractice.data.network.res.ProductRes;
import me.uptop.mvpgoodpractice.data.network.res.TwProfileRes;
import me.uptop.mvpgoodpractice.data.network.res.UserRes;
import me.uptop.mvpgoodpractice.data.network.res.VkProfileRes;
import me.uptop.mvpgoodpractice.data.network.res.models.AddCommentRes;
import me.uptop.mvpgoodpractice.data.network.res.models.Comments;
import me.uptop.mvpgoodpractice.data.storage.dto.FbDataDto;
import me.uptop.mvpgoodpractice.data.storage.dto.ProductDto;
import me.uptop.mvpgoodpractice.data.storage.dto.TwDataDto;
import me.uptop.mvpgoodpractice.data.storage.dto.UserAddressDto;
import me.uptop.mvpgoodpractice.data.storage.dto.VKDataDto;
import me.uptop.mvpgoodpractice.data.storage.realm.OrdersRealm;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.di.components.DaggerDataManagerComponent;
import me.uptop.mvpgoodpractice.di.components.DataManagerComponent;
import me.uptop.mvpgoodpractice.di.modules.LocalModule;
import me.uptop.mvpgoodpractice.di.modules.NetworkModule;
import me.uptop.mvpgoodpractice.utils.ConstantManager;
import me.uptop.mvpgoodpractice.utils.MvpAuthApplication;
import me.uptop.mvpgoodpractice.utils.NetworkStatusChecker;
import okhttp3.MultipartBody;
import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.support.annotation.VisibleForTesting.NONE;

public class DataManager {
    public static final String TAG = "DataManager";
    private static DataManager ourInstance = null;
    private RestCallTransformer mRestCallTransformer = null;
    @Inject
    RestService mRestService;
    @Inject
    PreferencesManager mPreferencesManager;
    @Inject
    Retrofit mRetrofit;
    @Inject
    RealmManager mRealmManager;

    private List<ProductDto> mMockProductList;
    private Map<String, String> mUserProfileInfoMap;
    private ArrayList<UserAddressDto> mUserAddressDto;
    private Map<String, Boolean> mUserSettingsMap;

    public static DataManager getInstance() {
        if(ourInstance == null) {
            ourInstance = new DataManager();
        }
        return ourInstance ;
    }


    //for unit tests
    public DataManager(RestService mRestService) {
        this.mRestService = mRestService;
    }

    @VisibleForTesting(otherwise = NONE)
    DataManager(PreferencesManager preferencesManager) {
        mPreferencesManager = preferencesManager;
        mRestCallTransformer = new RestCallTransformer<>();
        ourInstance = this;
    }

    @VisibleForTesting(otherwise = NONE)
    DataManager(RestService restService, PreferencesManager preferencesManager, RealmManager realmManager) {
        mRestService = restService;
        mPreferencesManager = preferencesManager;
        mRealmManager = realmManager;
        mRestCallTransformer = new RestCallTransformer<>();
        mRestCallTransformer.setTestMode();
        ourInstance = this;
    }

    public DataManager() {
        DataManagerComponent component = DaggerService.getComponent(DataManagerComponent.class);
        if(component == null) {
            component = DaggerDataManagerComponent.builder()
                    .appComponent(MvpAuthApplication.getAppComponent())
                    .localModule(new LocalModule())
                    .networkModule(new NetworkModule())
                    .build();
            DaggerService.registerComponent(DataManagerComponent.class, component);
        }
        component.inject(this);
        mRestCallTransformer = new RestCallTransformer<>();
        mUserAddressDto = new ArrayList<>();

        generateMockData();
        generateUserProfileInfo();
        generateUserAddressDto();
        generateUserSettingsInfo();

        updateLocalDataWithTimer();
    }

    private void updateLocalDataWithTimer() {
        Observable.interval(ConstantManager.UPDATE_DATA_INTERVAL, TimeUnit.SECONDS) //генерируем послед каждые 30 сек
                .flatMap(aLong -> NetworkStatusChecker.isInternetAvialable()) //проверяем состояние сети
                .filter(aBoolean -> aBoolean) //только если есть сеть, то запрашиваем данные
                .flatMap(aBoolean -> {
                    Log.e(TAG, "updateLocalDataWithTimer: hello");
                    return getProductsObsFromNetwork();
                }) //запрашиваем данные из сети
                .doOnError(Throwable::printStackTrace)
                .subscribe(productRealm -> {
                    Log.e(TAG, "updateLocalDataWithTimer: LOCAL UPDATE complete:");
                }, throwable -> {
                    Log.e(TAG, "updateLocalDataWithTimer: ERROR"+throwable.getMessage() );
                });
        Log.e(TAG, "updateLocalDataWithTimer: ");
    }

    public Observable<ProductRealm> getProductsObsFromNetwork() {
        return mRestService.getProductResObs(mPreferencesManager.getLastProductUpdate())
                .compose(((RestCallTransformer<List<ProductRes>>) mRestCallTransformer))
                .flatMap(Observable::from) // преобразуем список List в последовательность
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(productRes -> {
                    if(!productRes.isActive()) {
                       mRealmManager.deleteFromRealm(ProductRealm.class, productRes.getId());
                    }
                })
//                .distinct(ProductRes::getRemoteId)
                .filter(ProductRes::isActive) //пропускаем дальше только активные(неактивные не нужно показывать, они же пустые)
                .doOnNext(productRes -> {
                    mRealmManager.saveProductResponseToRealm(productRes);
//                    saveOnDisk(productRes); //сохраняем на диск только активные
                })
                .retryWhen(errorObservable ->
                        errorObservable.zipWith(Observable.range(1,
                                ConstantManager.RETRY_REQUEST_COUNT),
                                (throwable, retryCount) -> retryCount) // генерируем последовательность чисел от 1 до 5 (число повторений запроса)
                                .doOnNext(retryCount -> Log.e(TAG, "LOCAL UPDATE request retry " +
                                        "count: " + retryCount + " " + new Date()))
                                .map(retryCount ->
                                        ((long) (ConstantManager.RETRY_REQUEST_BASE_DELAY * Math
                                                .pow(Math.E, retryCount)))) // расчитываем экспоненциальную задержку
                                .doOnNext(delay -> Log.e(TAG, "LOCAL UPDATE delay: " +
                                        delay))
                                .flatMap(delay -> Observable.timer(delay,
                                        TimeUnit.MILLISECONDS)) // создаем и возвращаем задержку в миллисекундах
                )
                .flatMap(productRes -> Observable.empty());
    }

    public Observable<Comments> sendComment(String productId, AddCommentRes comment) {
        return mRestService.sendCommentToServer(productId, comment);
    }

    private void generateUserSettingsInfo() {
        mUserSettingsMap = new HashMap<>();
        mUserSettingsMap.put(PreferencesManager.NOTIFICATION_ORDER_KEY, false);
        mUserSettingsMap.put(PreferencesManager.NOTIFICATION_PROMO_KEY, false);
    }

    private void generateUserAddressDto() {
        mUserAddressDto.add(new UserAddressDto(1, "Pavel", "Улица Дружбы", "Дом 19", "166", 5, "Все ровненько"));
        mUserAddressDto.add(new UserAddressDto(2, "John", "Улица Мира", "Дом 119", "1626", 6, "Все ровненько =)"));
    }

    private void generateUserProfileInfo() {
        mUserProfileInfoMap = new HashMap<>();
        mUserProfileInfoMap.put(PreferencesManager.PROFILE_FULL_NAME_KEY, "Pavel Arkhipov");
        mUserProfileInfoMap.put(PreferencesManager.PROFILE_PHONE_KEY, "999 9999");
        mUserProfileInfoMap.put(PreferencesManager.PROFILE_AVATAR_KEY, "android.resource://me.uptop.mvpgoodpractice/drawable/account_avatar");
    }

//    public DataManager(PreferencesManager preferencesManager) {mPreferencesManager = preferencesManager;}

    public PreferencesManager getPreferencesManager() {return mPreferencesManager;}

    private void generateMockData() {
        mMockProductList = new ArrayList<>();
        mMockProductList.add(new ProductDto(1, "disk test 1", "https://pixabay.com/static/uploads/user/2015/01/20/20-56-42-330_250x250.jpg", "desc 1", 100, 1, false, null));
        mMockProductList.add(new ProductDto(2, "disk test 2", "https://pixabay.com/static/uploads/user/2015/01/20/20-56-42-330_250x250.jpg", "desc 2", 200, 1, false, null));
        mMockProductList.add(new ProductDto(3, "disk test 3", "http://transkrym.ru/attachments/Image/2014-mercedes-s.png?template=generic", "desc 3", 300, 1, false, null));
        mMockProductList.add(new ProductDto(4, "disk test 4", "http://www.autobereg.com.ua/assets/images/Trade-In/GAZ_21/2609_11.png", "desc 4", 400, 1, false, null));
        mMockProductList.add(new ProductDto(5, "disk test 5", "imageUrl", "desc 5", 500, 1, false, null));
        mMockProductList.add(new ProductDto(6, "disk test 6", "imageUrl", "desc 6", 600, 1, false, null));
        mMockProductList.add(new ProductDto(7, "disk test 7", "imageUrl", "desc 7", 700, 1, false, null));
        mMockProductList.add(new ProductDto(8, "disk test 8", "imageUrl", "desc 8", 800, 1, false, null));
        mMockProductList.add(new ProductDto(9, "disk test 9", "imageUrl", "desc 9", 900, 1, false, null));

        mPreferencesManager.generateProductsMockData(mMockProductList);
    }



    public Map<String, String> getUserProfileInfo() {
        Map<String, String> profileInfo = mPreferencesManager.loadProfileInfo();
        for (String key : mUserProfileInfoMap.keySet()) {
            if (profileInfo.containsKey(key)) {
                mUserProfileInfoMap.put(key, profileInfo.get(key));
            }
        }
        return mUserProfileInfoMap;
    }

//    public RealmManager getRealmManager() {
//        return mRealmManager;
//    }

    public ArrayList<UserAddressDto> getUserAddress() {
        return mUserAddressDto;
    }

    public Map<String, Boolean> getUserSettings() {
        Map<String, Boolean> settings = mPreferencesManager.loadSettings();
        for (String key : mUserSettingsMap.keySet()) {
            if (settings.containsKey(key)) {
                mUserSettingsMap.put(key, settings.get(key));
            }
        }
        return mUserSettingsMap;
    }


    public void saveProfileInfo(String name, String phone, String avatarUri) {
        mPreferencesManager.saveProfileInfo(name, phone, avatarUri);
    }

    public void saveSettings(String notificationKey, boolean isChecked) {
        mPreferencesManager.saveSettings(notificationKey, isChecked);
    }

    public void removeAddress(UserAddressDto position) {
        mUserAddressDto.remove(position);
//        mRealmManager.removeAddress(position);
    }

    public void updateOrInsertAddress(UserAddressDto userAddresDto) {
//        mRealmManager.updateOrInsertAddressFromRealm(userAddresDto);
        if (mUserAddressDto.isEmpty()) {
            userAddresDto.setId(1);
            mUserAddressDto.add(userAddresDto);
        } else {
            if (userAddresDto.getId() == 0) {
                userAddresDto.setId(mUserAddressDto.get(mUserAddressDto.size() - 1).getId()+1);
                mUserAddressDto.add(userAddresDto);
            } else {
                for (int i = 0; i < mUserAddressDto.size(); i++) {
                    if(mUserAddressDto.get(i).getId()==userAddresDto.getId()){
                        mUserAddressDto.set(i, userAddresDto);
                    }
                }
            }
        }
    }


    public void saveCartProductCounter(int count) {
        mPreferencesManager.saveBasketCounter(count);
    }

    public int loadCartProductCounter() {
        return mPreferencesManager.getBasketCounter();
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public Observable<ProductRealm> getProductFromRealm() {
        return mRealmManager.getAllProductFromRealm();
    }

    public Observable<AvatarUrlRes> uploadUserPhoto(MultipartBody.Part body) {
        return mRestService.uploadUserAvatar(body);
    }

    public RealmResults<ProductRealm> getAllFavoriteProducts() {
        return mRealmManager.getAllFavoriteProducts();
    }

    public void addOrderFromRealm(ProductRealm product) {
        mRealmManager.addOrder(product);
    }

    public RealmResults<OrdersRealm> getAllOrders() {
        return mRealmManager.getAllOrders();
    }

    public Observable<UserRes> loginUser(UserLoginReq userLoginReq) {
        return mRestService.loginUser(userLoginReq)
                .compose(((RestCallTransformer<UserRes>) mRestCallTransformer))
                .subscribeOn(Schedulers.newThread())
                .doOnNext(userRes -> mPreferencesManager.saveProfileInfo(userRes));
    }

    public boolean isAuthUser() {
        // TODO: 14.03.17 check user auth token in shared pref
        return mPreferencesManager.getAuthToken() != null;
    }

    public String getAuthToken() {
        return mPreferencesManager.getAuthToken();
    }


    public String getUserFullName() {
        return mPreferencesManager.getUserName();
    }

    public Observable<UserRes> socialSignIn(UserSignInReq signInReq) {
        return mRestService.socialSignIn(signInReq)
                .compose(((RestCallTransformer<UserRes>) mRestCallTransformer))
                .retryWhen(errorObservable ->
                        errorObservable.zipWith(Observable.range(1, ConstantManager.RETRY_REQUEST_COUNT),
                                (throwable, retryCount) -> retryCount) // генерируем последовательность чисел от 1 до 5 (число повторений запроса)
                                .doOnNext(retryCount -> Log.e(TAG, "LOCAL UPDATE request retry " +
                                        "count: " + retryCount + " " + new Date()))
                                .map(retryCount ->
                                        ((long) (ConstantManager.RETRY_REQUEST_BASE_DELAY * Math
                                                .pow(Math.E, retryCount)))) // расчитываем экспоненциальную задержку
                                .doOnNext(delay -> Log.e(TAG, "LOCAL UPDATE delay: " +
                                        delay))
                                .flatMap(delay -> Observable.timer(delay,
                                        TimeUnit.MILLISECONDS)) // создаем и возвращаем задержку в миллисекундах
                );
    }

    public Observable<VkProfileRes> getVkProfile(String baseUrl, VKDataDto vkDataDto) {
        return mRestService.signInVk(baseUrl, vkDataDto.getAccessToken());
    }

    public Observable<FbProfileRes> getFbProfile(String baseUrl, FbDataDto fbDataDto) {
        return mRestService.getFbProfile(baseUrl, fbDataDto.getAccessToken());
    }

    public Observable<TwProfileRes> getTwProfile(String baseUrl, TwDataDto twDataDto) {
        return mRestService.getTwProfile(baseUrl, twDataDto.getAccessToken());
    }

    // TODO: 27.03.2017 Test me
    public Observable<UserRes> signInVk(VKDataDto vkDataDto) {
        return getVkProfile(ConstantManager.VK_BASE_URL + "users.get?fields=photo_200,contacts", vkDataDto)
                .map(vkProfileRes -> new UserSignInReq(vkProfileRes.getFirstName(), vkProfileRes.getLastName(), vkProfileRes.getAvatar(), vkDataDto.getEmail(), vkProfileRes.getPhone()))
                .flatMap(this::socialSignIn)
                .doOnNext(userRes -> mPreferencesManager.saveProfileInfo(userRes))
                .subscribeOn(Schedulers.io());
    }

    public Observable<UserRes> signInFb(FbDataDto fbDataDto) {
        return getFbProfile(ConstantManager.FB_BASE_URL + "me?fields=email,picture.width(200).height(200),first_name,last_name", fbDataDto)
                .map(fbProfileRes -> new UserSignInReq(fbProfileRes.getFirstName(), fbProfileRes.getLastName(), fbProfileRes.getAvatar(), fbProfileRes.getEmail(), "не задан"))
                .flatMap(this::socialSignIn)
                .doOnNext(userRes -> mPreferencesManager.saveProfileInfo(userRes))
                .subscribeOn(Schedulers.io());
    }

    public Observable<UserRes> signInTw(TwDataDto twDataDto) {
        return getTwProfile(ConstantManager.TW_BASE_URL + "show.json?screen_name=pashko00710", twDataDto)
                .map(twProfileRes -> new UserSignInReq(twProfileRes.getName(), "", twProfileRes.getProfileImageUrl(), "", "не задан"))
                .flatMap(this::socialSignIn)
                .doOnNext(userRes -> mPreferencesManager.saveProfileInfo(userRes))
                .subscribeOn(Schedulers.io());
    }
}
