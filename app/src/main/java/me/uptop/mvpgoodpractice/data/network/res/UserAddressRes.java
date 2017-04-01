package me.uptop.mvpgoodpractice.data.network.res;

import com.squareup.moshi.Json;

class UserAddressRes {
    @Json(name = "_id")
    private String id;
    private String name;
    private String street;
    private String house;
    @Json(name = "apartament")
    private String appartament;
    private int floor;
    private String comment;
}
