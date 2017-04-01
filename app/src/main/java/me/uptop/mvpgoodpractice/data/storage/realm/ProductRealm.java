package me.uptop.mvpgoodpractice.data.storage.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import me.uptop.mvpgoodpractice.data.network.res.ProductRes;

public class ProductRealm extends RealmObject {
    @PrimaryKey
    private String id;
    private String productName;
    private String imageUrl;
    private String description;
    private int price;
    private float rating;
    private int count = 1;
    private boolean favorite;
    private RealmList<CommentRealm> mCommentRealmList = new RealmList<>(); //one to many

    //Необоходимо для Realm
    public ProductRealm() {
    }

    public ProductRealm(ProductRes productRes) {
        id = productRes.getId();
        productName = productRes.getProductName();
        imageUrl = productRes.getImageUrl();
        description = productRes.getDescription();
        price = productRes.getPrice();
        rating = productRes.getRaiting();
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public float getRating() {
        return rating;
    }

    public int getCount() {
        return count;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public RealmList<CommentRealm> getCommentRealm() {
        return mCommentRealmList;
    }

    public void addComment(CommentRealm commentRealm) {
        mCommentRealmList.add(commentRealm);
    }

    public void add() {
        count++;
    }

    public void remove() {
        count--;
    }

    public void changeFavorite() {
        setFavorite(!this.favorite);
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
