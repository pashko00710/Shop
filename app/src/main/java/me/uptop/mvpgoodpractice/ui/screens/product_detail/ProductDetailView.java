package me.uptop.mvpgoodpractice.ui.screens.product_detail;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import javax.inject.Inject;

import butterknife.BindView;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.mvp.views.AbstractView;
import me.uptop.mvpgoodpractice.mvp.views.IProductDetailView;

public class ProductDetailView extends AbstractView<ProductDetailScreen.ProductDetailPresenter> implements IProductDetailView {

    @BindView(R.id.more_info_pager)
    ViewPager mViewPager;
    @BindView(R.id.more_info_tabs)
    TabLayout mTabLayout;
    @Inject
    ProductDetailScreen.ProductDetailPresenter mPresenter;

    public ProductDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<ProductDetailScreen.Component>getDaggerComponent(context).inject(this);
    }

    //region ============================== IProductDetailView ==============================

    @Override
    public void initView(ProductRealm productRealm) {
        setupViewPager(productRealm);
    }

    private void setupViewPager(ProductRealm productRealm) {
        mViewPager.setAdapter(new DetailAdapter(getContext(), productRealm));
        mTabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    //endregion


}
