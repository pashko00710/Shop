package me.uptop.mvpgoodpractice.mvp.models;

import io.realm.RealmResults;
import me.uptop.mvpgoodpractice.data.storage.realm.OrdersRealm;

public class CartModel extends AbstractModel {

    public RealmResults<OrdersRealm> getAllOrders() {
        return mDataManager.getAllOrders();
    }
}
