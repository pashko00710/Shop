package me.uptop.mvpgoodpractice.data.storage.dto;

import android.os.Parcel;
import android.os.Parcelable;

import me.uptop.mvpgoodpractice.data.storage.realm.AddressRealm;

public class UserAddressDto implements Parcelable {
    private int id;
    private String name;
    private String street;
    private String house;
    private String appartament;
    private int floor;
    private String comment;
    private boolean favorite;

    public UserAddressDto(int id, String name, String street, String house, String appartament, int floor, String comment) {
        this.id = id;
        this.name = name;
        this.street = street;
        this.house = house;
        this.appartament = appartament;
        this.floor = floor;
        this.comment = comment;
    }

    public void update(UserAddressDto address) {
        this.name = address.getName();
        this.street = address.getStreet();
        this.house = address.getHouse();
        this.appartament = address.getAppartament();
        this.floor = address.getFloor();
        this.comment = address.getComment();
    }

    protected UserAddressDto(Parcel in) {
        id = in.readInt();
        name = in.readString();
        street = in.readString();
        house = in.readString();
        appartament = in.readString();
        floor = in.readInt();
        comment = in.readString();
        favorite = in.readByte() != 0;
    }

    public static final Creator<UserAddressDto> CREATOR = new Creator<UserAddressDto>() {
        @Override
        public UserAddressDto createFromParcel(Parcel in) {
            return new UserAddressDto(in);
        }

        @Override
        public UserAddressDto[] newArray(int size) {
            return new UserAddressDto[size];
        }
    };

    public UserAddressDto(AddressRealm addressRealm) {
        this.id = addressRealm.getId();
        this.name = addressRealm.getName();
        this.street = addressRealm.getStreet();
        this.house = addressRealm.getHouse();
        this.appartament = addressRealm.getAppartment();
        this.floor = addressRealm.getFloor();
        this.comment = addressRealm.getComment();
        this.favorite = addressRealm.isFavorite();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getAppartament() {
        return appartament;
    }

    public void setAppartament(String appartament) {
        this.appartament = appartament;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(street);
        dest.writeString(house);
        dest.writeString(appartament);
        dest.writeInt(floor);
        dest.writeString(comment);
        dest.writeByte((byte) (favorite ? 1 : 0));
    }
}
