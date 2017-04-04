package me.uptop.mvpgoodpractice.mvp.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

import me.uptop.mvpgoodpractice.data.storage.dto.UserAddressDto;
import me.uptop.mvpgoodpractice.data.storage.dto.UserInfoDto;
import me.uptop.mvpgoodpractice.data.storage.dto.UserSettingsDto;
import me.uptop.mvpgoodpractice.jobs.UploadAvatarJob;
import rx.Observable;
import rx.subjects.BehaviorSubject;

import static me.uptop.mvpgoodpractice.data.managers.PreferencesManager.NOTIFICATION_ORDER_KEY;
import static me.uptop.mvpgoodpractice.data.managers.PreferencesManager.NOTIFICATION_PROMO_KEY;
import static me.uptop.mvpgoodpractice.data.managers.PreferencesManager.PROFILE_AVATAR_KEY;
import static me.uptop.mvpgoodpractice.data.managers.PreferencesManager.PROFILE_FULL_NAME_KEY;
import static me.uptop.mvpgoodpractice.data.managers.PreferencesManager.PROFILE_PHONE_KEY;

public class AccountModel extends AbstractModel {
//    public UserDto getUserDto() {
//        return null;//new UserDto(getUserProfileInfo(), getUserAddress(), getUserSettings());
//    }

    private BehaviorSubject<UserInfoDto> mUserInfoObs = BehaviorSubject.create();

    public AccountModel() {
        mUserInfoObs.onNext(getUserProfileInfo());
    }

    //region =========================== Address =====================
    public Observable<UserAddressDto> getAddressObs() {
        return Observable.from(getUserAddress());
//        return mDataManager.getAllAdressFromRealm();
    }

    private ArrayList<UserAddressDto> getUserAddress() {
        return mDataManager.getUserAddress();
    }

    public void updateOrInsertAddress(UserAddressDto userAddressDto) {
        mDataManager.updateOrInsertAddress(userAddressDto);
    }

    public void removeAddress(UserAddressDto userAddressDto) {
        mDataManager.removeAddress(userAddressDto);
    }

    public UserAddressDto getAddressFromPosition(int position) {
        return getUserAddress().get(position);
    }
    //endregion


    //region =========================== Settings =====================
    public Observable<UserSettingsDto> getUserSettingsObs() {
        return Observable.just(getUserSettings());
    }

    private UserSettingsDto getUserSettings() {
        Map<String , Boolean> map = mDataManager.getUserSettings();
        return new UserSettingsDto(map.get(NOTIFICATION_ORDER_KEY), map.get(NOTIFICATION_PROMO_KEY));
    }

    public void saveSettings(UserSettingsDto settings) {
        mDataManager.saveSettings(NOTIFICATION_ORDER_KEY, settings.isOrderNotification());
        mDataManager.saveSettings(NOTIFICATION_PROMO_KEY, settings.isPromoNotification());
    }
    //endregion

    //region =========================== User =====================
    public void saveProfileInfo(UserInfoDto userInfo) {
        mDataManager.saveProfileInfo(userInfo.getName(), userInfo.getPhone(), userInfo.getAvatar());
        mUserInfoObs.onNext(userInfo);

        String uriAvatar = userInfo.getAvatar();

        Log.e("uriAvatar", "saveProfileInfo: "+uriAvatar);

        if(!uriAvatar.contains("http")) {
            uploadAvatarOnServer(uriAvatar);
        }

    }

    public UserInfoDto getUserProfileInfo() {
        Map<String, String> map = mDataManager.getUserProfileInfo();
        return new UserInfoDto(map.get(PROFILE_FULL_NAME_KEY),
                map.get(PROFILE_PHONE_KEY),
                map.get(PROFILE_AVATAR_KEY));
    }

    public Observable<UserInfoDto> getUserInfoObs() {
        return mUserInfoObs;
    }


    private void uploadAvatarOnServer(String imageUri) {
        mJobManager.addJobInBackground(new UploadAvatarJob(imageUri));
    }

    public void updateUserInfo() {
        mUserInfoObs.onNext(getUserProfileInfo());
    }
    //endregion
}
