package me.uptop.mvpgoodpractice.data.network.res;

import android.support.annotation.Nullable;

import com.squareup.moshi.Json;

import java.util.List;

public class UserRes {
    @Json(name = "_id")
    private String id;
    private String fullName;
    private String avatarUrl;
    private String token;
//    private String phone;
    @Nullable
    private String phone;
    @Nullable
    private List<UserAddressRes> addresses;


    //for unit tests
    public UserRes(String id, String fullName, String avatarUrl, String token, String phone,  List<UserAddressRes> addresses) {
        this.id = id;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.token = token;
        this.phone = phone;
        this.addresses = addresses;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getToken() {
        return token;
    }

    public String getPhone() {
        return phone;
    }

    public List<UserAddressRes> getAddresses() {
        return addresses;
    }
}
