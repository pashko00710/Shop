package me.uptop.mvpgoodpractice.data.storage.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Map;

import me.uptop.mvpgoodpractice.data.managers.PreferencesManager;

public class UserDto implements Parcelable{
    private int id;
    private String fullname;
    private String avatar;
    private String phone;

    private boolean orderNotification;
    private boolean promoNotification;
    private ArrayList<UserAddressDto> userAddress;

    public UserDto(Map<String,String> userProfileInfo,ArrayList<UserAddressDto> userAddress, Map<String, Boolean> userSettings) {
        this.fullname = userProfileInfo.get(PreferencesManager.PROFILE_FULL_NAME_KEY);
        this.avatar = userProfileInfo.get(PreferencesManager.PROFILE_AVATAR_KEY);
        this.phone = userProfileInfo.get(PreferencesManager.PROFILE_PHONE_KEY);
        this.orderNotification = userSettings.get(PreferencesManager.NOTIFICATION_ORDER_KEY);
        this.promoNotification = userSettings.get(PreferencesManager.NOTIFICATION_PROMO_KEY);
        this.userAddress = userAddress;
    }

    public UserDto(Parcel in) {
        id = in.readInt();
        fullname = in.readString();
        avatar = in.readString();
        phone = in.readString();
        orderNotification = in.readByte() != 0;
        promoNotification = in.readByte() != 0;
        userAddress = in.createTypedArrayList(UserAddressDto.CREATOR);
    }

    public static final Creator<UserDto> CREATOR = new Creator<UserDto>() {
        @Override
        public UserDto createFromParcel(Parcel in) {
            return new UserDto(in);
        }

        @Override
        public UserDto[] newArray(int size) {
            return new UserDto[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isOrderNotification() {
        return orderNotification;
    }

    public void setOrderNotification(boolean orderNotification) {
        this.orderNotification = orderNotification;
    }

    public boolean isPromoNotification() {
        return promoNotification;
    }

    public void setPromoNotification(boolean promoNotification) {
        this.promoNotification = promoNotification;
    }

    public ArrayList<UserAddressDto> getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(ArrayList<UserAddressDto> userAddress) {
        this.userAddress = userAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(fullname);
        dest.writeString(avatar);
        dest.writeString(phone);
        dest.writeByte((byte) (orderNotification ? 1 : 0));
        dest.writeByte((byte) (promoNotification ? 1 : 0));
        dest.writeTypedList(userAddress);
    }
}
