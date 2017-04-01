package me.uptop.mvpgoodpractice.ui.screens.catalog;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;
import mortar.MortarScope;

public class CatalogAdapter extends PagerAdapter {
    private static final String TAG = "CatalogAdapter";
    private List<ProductRealm> mProductList = new ArrayList<>();

    public CatalogAdapter() {

    }

    @Override
    public int getCount() {
        return mProductList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public void addItem(ProductRealm product) {
        mProductList.add(product);
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ProductRealm productDto = mProductList.get(position);
        Context productContext = CatalogScreen.Factory.createProductContext(productDto, container.getContext());
        View newView = LayoutInflater.from(productContext).inflate(R.layout.screen_product, container, false);
        newView.setTag("Product"+position);//добавляем тег к вью продукта
        container.addView(newView);
        return newView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        MortarScope screenScope = MortarScope.getScope(((View)object).getContext());
        container.removeView((View)object);
        screenScope.destroy();
    }
}
