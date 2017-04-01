package me.uptop.mvpgoodpractice.mvp.views;

import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;

public interface IProductDetailView extends IView{
    void initView(ProductRealm productRealm);
}
