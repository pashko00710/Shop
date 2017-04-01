package me.uptop.mvpgoodpractice.data.storage.dto;

import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;

public class DescriptionDto {
    private String productName;
    private String description;
    private float raiting;
    private int count;
    private int price;
    private boolean favorite;

//    public DescriptionDto(String productName, String description, float rating, int count, int price, boolean favorite) {
//        this.productName = productName;
//        this.description = description;
//        this.rating = rating;
//        this.count = count;
//        this.price = price;
//        this.favorite = favorite;
//    }

    public DescriptionDto(ProductRealm productRealm) {
        this.productName = productRealm.getProductName();
        this.description = productRealm.getDescription();
        this.raiting = productRealm.getRating();
        this.count = productRealm.getCount();
        this.price = productRealm.getPrice();
        this.favorite = productRealm.isFavorite();
    }

//    public DescriptionDto(String description, float rating, int count, int price, boolean favorite) {
//        this.description = description;
//        this.rating = rating;
//        this.count = count;
//        this.price = price;
//        this.favorite = favorite;
//    }

    public String getDescription() {
        return description;
    }

    public float getRaiting() {
        return raiting;
    }

    public int getCount() {
        return count;
    }

    public int getPrice() {
        return price;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getProductName() {
        return productName;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
