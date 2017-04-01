package me.uptop.mvpgoodpractice.mvp.models;

import io.realm.RealmResults;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;

public class FavoriteModel extends AbstractModel {
    public RealmResults<ProductRealm> getAllFavorites() {
        return mDataManager.getAllFavoriteProducts();
    }
}
