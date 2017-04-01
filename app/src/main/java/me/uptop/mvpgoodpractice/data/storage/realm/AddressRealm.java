package me.uptop.mvpgoodpractice.data.storage.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import me.uptop.mvpgoodpractice.data.storage.dto.UserAddressDto;

public class AddressRealm extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;
    private String street;
    private String house;
    private String appartment;
    private int floor;
    private String comment;
    private boolean favorite;

    public AddressRealm() {
    }

    public AddressRealm(UserAddressDto adressDto) {
        this.id = adressDto.getId();
        this.name = adressDto.getName();
        this.street = adressDto.getStreet();
        this.house = adressDto.getHouse();
        this.appartment = adressDto.getAppartament();
        this.floor = adressDto.getFloor();
        this.comment = adressDto.getComment();
        this.favorite = adressDto.isFavorite();
    }

    public AddressRealm(int id, String name, String street, String house, String appartment, int floor, String comment) {
        this.id = id;
        this.name = name;
        this.street = street;
        this.house = house;
        this.appartment = appartment;
        this.floor = floor;
        this.comment = comment;
        this.favorite = false;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getHouse() {
        return house;
    }

    public String getAppartment() {
        return appartment;
    }

    public int getFloor() {
        return floor;
    }

    public String getComment() {
        return comment;
    }

    public boolean isFavorite() {
        return favorite;
    }
}