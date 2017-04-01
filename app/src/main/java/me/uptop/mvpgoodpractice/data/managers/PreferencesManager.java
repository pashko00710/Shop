package me.uptop.mvpgoodpractice.data.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.uptop.mvpgoodpractice.data.network.res.UserRes;
import me.uptop.mvpgoodpractice.data.storage.dto.ProductDto;
import me.uptop.mvpgoodpractice.data.storage.dto.UserAddressDto;
import me.uptop.mvpgoodpractice.data.storage.dto.UserInfoDto;
import me.uptop.mvpgoodpractice.data.storage.dto.UserSettingsDto;
import me.uptop.mvpgoodpractice.utils.ConstantManager;
import me.uptop.mvpgoodpractice.utils.MvpAuthApplication;

public class PreferencesManager {
    static final String PROFILE_USER_ID_KEY = "PROFILE_USER_ID_KEY";
    static final String PROFILE_AUTH_TOKEN_KEY = "PROFILE_AUTH_TOKEN_KEY";
    public static final String PROFILE_FULL_NAME_KEY = "profileFullNameKey";
    public static final String PROFILE_AVATAR_KEY = "profileAvatarKey";
    public static final String PROFILE_PHONE_KEY = "profilePhoneKey";
    public static final String NOTIFICATION_ORDER_KEY = "notificationOrderKey";
    public static final String NOTIFICATION_PROMO_KEY = "notificationPromoKey";
    public static final String PRODUCT_LAST_UPDATE_KEY = "PRODUCT_LAST_UPDATE_KEY";
    private static final String MOCK_PRODUCT_LIST = "MOCK_PRODUCT_LIST";
    static final String BASKET_COUNT_KEY = "BASKET_COUNT_KEY";
    static final String USER_ADDRESSES_KEY = "USER_ADDRESSES_KEY";

    //default values
    static final String DEFAULT_LAST_UPDATE = "Thu Jan 1 1970 00:00:00 GMT+0000 (UTC)";
    private JsonAdapter<UserAddressDto> jsonAdapter;

    private SharedPreferences mSharedPreferences;
    Context mContext;
//    private Moshi mMoshi;
//    private JsonAdapter<UserAddressDto> mJsonAdapter;
    private List<ProductDto> mProductDtoList = new ArrayList<>();

    public PreferencesManager(Context context) {
        this.mSharedPreferences= MvpAuthApplication.getSharedPreferences();
        mContext = context;
        Moshi moshi = new Moshi.Builder()
                .build();
        jsonAdapter = moshi.adapter(UserAddressDto.class);

//        initProductsMockData();

//        mMoshi = new Moshi.Builder().build();
//        mJsonAdapter = mMoshi.adapter(UserAddressDto.class);
    }

    public String getLastProductUpdate() {
        return mSharedPreferences.getString(PRODUCT_LAST_UPDATE_KEY, DEFAULT_LAST_UPDATE);
    }

    public void saveLastProductUpdate(String lastModified) {
//        Log.e(TAG, "saveLastProductUpdate: "+lastModified);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PRODUCT_LAST_UPDATE_KEY, lastModified);
        editor.apply();
    }

    public void saveAuthToken (String authToken){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ConstantManager.AUTH_TOKEN, authToken);
        editor.apply();
    }

    public void saveBasketCounter(int count) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(ConstantManager.BASKET_COUNT, count);
        editor.apply();
    }

    public int getBasketCounter() {
        return mSharedPreferences.getInt(ConstantManager.BASKET_COUNT, 0);
    }

    public String getAuthToken(){
        return mSharedPreferences.getString(ConstantManager.AUTH_TOKEN, null);
    }

    public boolean isUserAuth() {
        return !"".equals(mSharedPreferences.getString(ConstantManager.AUTH_TOKEN, ""));
    }

    void saveProfileInfo(String name, String phone, String avatarUri) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PROFILE_FULL_NAME_KEY, name);
        editor.putString(PROFILE_PHONE_KEY, phone);
        editor.putString(PROFILE_AVATAR_KEY, avatarUri);
        editor.apply();
    }

    Map<String, String> loadProfileInfo() {
        Map<String, String> profileInfo = new HashMap<>();
        if (mSharedPreferences.contains(PROFILE_FULL_NAME_KEY)) {
            profileInfo.put(PROFILE_FULL_NAME_KEY, mSharedPreferences.getString(PROFILE_FULL_NAME_KEY, "null"));
        }
        if (mSharedPreferences.contains(PROFILE_PHONE_KEY)) {
            profileInfo.put(PROFILE_PHONE_KEY, mSharedPreferences.getString(PROFILE_PHONE_KEY, "null"));
        }
        if (mSharedPreferences.contains(PROFILE_AVATAR_KEY)) {
            profileInfo.put(PROFILE_AVATAR_KEY, mSharedPreferences.getString(PROFILE_AVATAR_KEY, "null"));
        }
        return profileInfo;
    }

    void saveSettings(String notificationKey, boolean isChecked) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(notificationKey, isChecked);
        editor.apply();
    }

    Map<String, Boolean> loadSettings() {
        Map<String, Boolean> settings = new HashMap<>();
        if (mSharedPreferences.contains(NOTIFICATION_ORDER_KEY)) {
            settings.put(NOTIFICATION_ORDER_KEY, mSharedPreferences.getBoolean(NOTIFICATION_ORDER_KEY, false));
        }
        if (mSharedPreferences.contains(NOTIFICATION_PROMO_KEY)) {
            settings.put(NOTIFICATION_PROMO_KEY, mSharedPreferences.getBoolean(NOTIFICATION_PROMO_KEY, false));
        }
        return settings;
    }

    public void generateProductsMockData(List<ProductDto> mockProductList) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String products = gson.toJson(mockProductList);
        if (mSharedPreferences.getString(MOCK_PRODUCT_LIST, null) == null) {
            editor.putString(MOCK_PRODUCT_LIST, products);
            editor.apply();
        }
    }

    private void initProductsMockData() {
        String products = mSharedPreferences.getString(MOCK_PRODUCT_LIST, null);
        if (products != null) {
            Gson gson = new Gson();
            ProductDto[] productList = gson.fromJson(products, ProductDto[].class);
            List<ProductDto> productDtoList = Arrays.asList(productList);
            mProductDtoList = new ArrayList<>(productDtoList);
        }
    }

    public List<ProductDto> getProductList() {
        return mProductDtoList;
    }

    public void saveUserAvatar(String avatarUrl) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PROFILE_AVATAR_KEY, avatarUrl);
        editor.apply();
    }

    public String getUserAvatar() {
        return mSharedPreferences.getString(PROFILE_AVATAR_KEY, "http://skill-branch.ru/img/app/avatar-1.png");
    }

    public String getUserName() {
        return mSharedPreferences.getString(PROFILE_FULL_NAME_KEY, "Unknown");
    }

    public void saveProfileInfo(@NonNull UserRes userRes) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PROFILE_USER_ID_KEY, userRes.getId());
        editor.putString(PROFILE_AUTH_TOKEN_KEY, userRes.getToken());
        editor.putString(PROFILE_FULL_NAME_KEY, userRes.getFullName());
        editor.putString(PROFILE_AVATAR_KEY, userRes.getAvatarUrl());
        String phone = "не указан";
        if (userRes.getPhone() != null) {
            phone = userRes.getPhone();
        }
        editor.putString(PROFILE_PHONE_KEY, phone);
        editor.apply();
    }

    void saveProfileInfo(@NonNull UserInfoDto userInfoDto) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PROFILE_FULL_NAME_KEY, userInfoDto.getName());
        editor.putString(PROFILE_PHONE_KEY, userInfoDto.getPhone());
        editor.putString(PROFILE_AVATAR_KEY, userInfoDto.getAvatar());
        editor.apply();
    }

    @Nullable
    public UserInfoDto getUserProfileInfo() {
        String fullName = mSharedPreferences.getString(PROFILE_FULL_NAME_KEY, null);
        String phone = mSharedPreferences.getString(PROFILE_PHONE_KEY, null);
        String avatarUrl = mSharedPreferences.getString(PROFILE_AVATAR_KEY, null);
        return new UserInfoDto(fullName, phone, avatarUrl);
    }

    void saveUserSettings(UserSettingsDto settings) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(NOTIFICATION_ORDER_KEY, settings.isOrderNotification());
        editor.putBoolean(NOTIFICATION_PROMO_KEY, settings.isPromoNotification());
        editor.apply();
    }

    public UserSettingsDto getUserSetting() {
        boolean isOrder = mSharedPreferences.getBoolean(NOTIFICATION_ORDER_KEY, false);
        boolean isPromo = mSharedPreferences.getBoolean(NOTIFICATION_PROMO_KEY, false);
        return new UserSettingsDto(isOrder, isPromo);
    }

    public void updateUserAddress(UserAddressDto userAddressDto) {
        List<UserAddressDto> list = getUserAddress();
        for (UserAddressDto address : list) {
            if (address.getId() == userAddressDto.getId()) {
                address.update(userAddressDto);
                break;
            }
        }

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Set<String> setJson = new HashSet<String>();
        for (int i = 0; i < list.size(); i++) {
            setJson.add(jsonAdapter.toJson(list.get(i)));
        }

        editor.putStringSet(USER_ADDRESSES_KEY, setJson);
        editor.apply();
    }

    public void addUserAddress(UserAddressDto address) {
        List<UserAddressDto> list = getUserAddress();
        if (address.getId() == 0) {
            address.setId(list.size() + 1);
        }
        list.add(address);


        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Set<String> setJson = new HashSet<String>();
        for (int i = 0; i < list.size(); i++) {
            setJson.add(jsonAdapter.toJson(list.get(i)));
        }

        editor.putStringSet(USER_ADDRESSES_KEY, setJson);
        editor.apply();
    }

    public List<UserAddressDto> getUserAddress() {
        Set<String> setJson = mSharedPreferences.getStringSet(USER_ADDRESSES_KEY, new HashSet<String>());
        ArrayList<String> listJson = new ArrayList<String>(setJson);
        ArrayList<UserAddressDto> arrayAddress = new ArrayList<UserAddressDto>();

        for (int i = 0; i < listJson.size(); i++) {
            try {
                arrayAddress.add(jsonAdapter.fromJson(listJson.get(i)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return arrayAddress;
    }


    public void removeAddress(UserAddressDto userAddressDto) {
        List<UserAddressDto> arrayList = getUserAddress();

        Iterator<UserAddressDto> iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            UserAddressDto entry = iterator.next();
            if (entry.getId() == userAddressDto.getId()) {
                iterator.remove();
                break;
            }
        }

        arrayList.remove(userAddressDto);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Set<String> setJson = new HashSet<String>();
        for (int i = 0; i < arrayList.size(); i++) {
            setJson.add(jsonAdapter.toJson(arrayList.get(i)));
        }

        editor.putStringSet(USER_ADDRESSES_KEY, setJson);
        editor.apply();
    }

}
