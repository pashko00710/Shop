package me.uptop.mvpgoodpractice.ui.screens.product_detail.description;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.AttributeSet;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.dto.DescriptionDto;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.mvp.views.AbstractView;
import me.uptop.mvpgoodpractice.mvp.views.IDescView;

public class DescriptionView extends AbstractView<DescriptionScreen.DescriptionPresenter> implements IDescView {
    @BindView(R.id.about_product)
    TextView mAboutProduct;
    @BindView(R.id.product_description)
    TextView mProductDesc;
    @BindView(R.id.rating_bar)
    AppCompatRatingBar mRatingBar;
    @BindView(R.id.product_count)
    TextView mProductCount;
    @BindView(R.id.product_price)
    TextView mProductPrice;
    @BindView(R.id.fab_desc_favorite)
    FloatingActionButton favoriteFab;

    @Inject
    DescriptionScreen.DescriptionPresenter mPresenter;

    public DescriptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<DescriptionScreen.Component>getDaggerComponent(context).inject(this);
    }

//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        ButterKnife.bind(this);
//    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        if (!isInEditMode()) {
//            mPresenter.takeView(this);
//        }
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        if (!isInEditMode()) {
//            mPresenter.dropView(this);
//        }
//    }


    @Override
    public void initView(DescriptionDto descriptionDto) {
        mAboutProduct.setText(descriptionDto.getProductName());
        mProductDesc.setText(descriptionDto.getDescription());
        mRatingBar.setRating(descriptionDto.getRaiting());
        mProductCount.setText(Integer.toString(descriptionDto.getCount()));

        if(descriptionDto.isFavorite() == true) {
            favoriteFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFA815")));
        } else {
            favoriteFab.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        }

        if(descriptionDto.getCount() > 0) {
            mProductPrice.setText(Integer.toString(descriptionDto.getPrice()*descriptionDto.getCount()));
        } else {
            mProductPrice.setText(Integer.toString(descriptionDto.getPrice()));
        }
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    @OnClick(R.id.plus_button)
    public void clickPlus() {
        mPresenter.clickOnPlus();
    }

    @OnClick(R.id.minus_button)
    public void clickMinus() {
        mPresenter.clickOnMinus();
    }

    @OnClick(R.id.fab_desc_favorite)
    public void clickIsFavorite() {
        mPresenter.clickIsFavorite();
    }
}
