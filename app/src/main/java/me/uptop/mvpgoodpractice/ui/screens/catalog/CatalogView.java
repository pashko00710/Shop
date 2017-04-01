package me.uptop.mvpgoodpractice.ui.screens.catalog;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.mvp.views.AbstractView;
import me.uptop.mvpgoodpractice.mvp.views.ICatalogView;
import me.uptop.mvpgoodpractice.ui.screens.product.ProductView;

public class CatalogView extends AbstractView<CatalogScreen.CatalogPresenter> implements ICatalogView {
    private static final String TAG = "CatalogView";

//    @Inject
//    CatalogScreen.CatalogPresenter mPresenter;

    @BindView(R.id.add_to_card_btn)
    Button mAddToCardBtn;
    @BindView(R.id.product_pager)
    ViewPager mProductPager;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    private CatalogAdapter mAdapter;

    public CatalogView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<CatalogScreen.Component>getDaggerComponent(context).inject(this);
        mAdapter = new CatalogAdapter();
    }

    @Override
    public void showCatalogView() {
        mProductPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mProductPager, true);
    }

    @Override
    public void updateProductCounter() {

    }

    public int getCurrentPagerPosition() {
        return mProductPager.getCurrentItem();
    }

    @Override
    public boolean viewOnBackPressed() {
        return getCurrentProductView().viewOnBackPressed();
    }

    @OnClick(R.id.add_to_card_btn)
     void clickAddToCard() {
        mPresenter.clickOnBuyButton(mProductPager.getCurrentItem());
    }

    public CatalogAdapter getAdapter() {
        return mAdapter;
    }

    public ProductView getCurrentProductView() {
        return (ProductView) mProductPager.findViewWithTag("Product"+ mProductPager.getCurrentItem());
    }
}
