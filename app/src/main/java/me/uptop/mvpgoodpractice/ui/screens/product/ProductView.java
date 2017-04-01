package me.uptop.mvpgoodpractice.ui.screens.product;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.ChangeImageTransform;
import com.transitionseverywhere.Explode;
import com.transitionseverywhere.SidePropagation;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.dto.ProductDto;
import me.uptop.mvpgoodpractice.data.storage.dto.ProductLocalInfo;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.mvp.views.AbstractView;
import me.uptop.mvpgoodpractice.mvp.views.IProductView;
import me.uptop.mvpgoodpractice.utils.ImageTransformation;
import me.uptop.mvpgoodpractice.utils.ViewHelper;

public class ProductView extends AbstractView<ProductScreen.ProductPresenter> implements IProductView {
    public static final String TAG = "ProductView";

    @BindView(R.id.product_name_txt)
    TextView mProductNameTxt;

    @BindView(R.id.product_description_txt)
    TextView mProductDescriptionTxt;

    @BindView(R.id.product_image)
    ImageView mProductImage;

    @BindView(R.id.product_count_txt)
    TextView mProductCountTxt;

    @BindView(R.id.product_price_txt)
    TextView mProductPriceTxt;

    @BindView(R.id.plus_btn)
    ImageButton mPlusBtn;

    @BindView(R.id.minus_btn)
    ImageButton mMinusBtn;

    @BindView(R.id.favorite_btn)
    CheckBox favoriteBtn;

    @BindView(R.id.product_wrapper)
    LinearLayout mProductWrapper;

    @BindView(R.id.product_card)
    CardView mProductCard;

//    @Inject
//    ProductScreen.ProductPresenter mPresenter;

    @Inject
    Picasso mPicasso;
    private ArrayList<View> mChildList;
    private boolean isZoomed;
    private int mInImageHeight;
    private float mDen;

    public ProductView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<ProductScreen.Component>getDaggerComponent(context).inject(this);
        mDen = ViewHelper.getDensity(context);
    }

    //region =========================== IProductView =====================


    @Override
    public void showProductView(final ProductDto product) {
        mProductNameTxt.setText(product.getProductName());
        mProductDescriptionTxt.setText(product.getDescription());
        mProductCountTxt.setText(String.valueOf(product.getCount()));
        Log.e(TAG, "showProductView: prodName"+product.getProductName()+"  favorite  "+product.isFavorite());

        if(product.isFavorite()) {
            favoriteBtn.setChecked(true);
        } else {
            favoriteBtn.setChecked(false);
        }

        if(product.getCount()>0) {
            mProductPriceTxt.setText(String.valueOf(product.getCount()*product.getPrice() + ".-"));
        } else {
            mProductPriceTxt.setText(String.valueOf(product.getPrice() + ".-"));
        }

        // TODO: 27.10.16 Picasso load from url
        Log.d(TAG, "showProductView: "+product.getImageUrl());
        mPicasso.with(getContext())
                .load(product.getImageUrl())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .transform(ImageTransformation.getTransformation(mProductImage))
                .into(mProductImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "onSuccess: load from a cache");

                    }

                    @Override
                    public void onError() {
                        Log.e(TAG, "onError: ");
                        mPicasso.with(getContext())
                                .load(product.getImageUrl())
                                .transform(ImageTransformation.getTransformation(mProductImage))
//                                .centerCrop()
                                .into(mProductImage);
                    }
                });
    }

    public ProductRealm getProductRealm() {
        return mPresenter.getProduct();
    }

    public ProductLocalInfo getProductLocalInfo() {
        return new ProductLocalInfo(0, favoriteBtn.isChecked(), Integer.parseInt(mProductCountTxt.getText().toString()));
    }



    @Override
    public void updateProductCountView(ProductDto product) {
        mProductCountTxt.setText(String.valueOf(product.getCount()));
        if(product.getCount()>0){
            Log.e(TAG, "updateProductCountView: -"+product.getCount()*product.getPrice());
            mProductPriceTxt.setText(String.valueOf(product.getCount()*product.getPrice() + ".-"));
        }
    }

    @Override
    public boolean viewOnBackPressed() {
        if(isZoomed) {
            startZoomTransition();
            return true;
        } else {
            return false;
        }
    }

    //endregion


    //region =========================== Events =====================

    @OnClick(R.id.plus_btn)
    public void clickPlus() {
        mPresenter.clickOnPlus();
    }

    @OnClick(R.id.minus_btn)
    public void clickMinus() {
        mPresenter.clickOnMinus();
    }

    @OnClick(R.id.favorite_btn)
    public void clickOnFavorite() {
        mPresenter.clickFavorite();
//        startAddToCardAnim();
    }

    @OnClick(R.id.show_more_btn)
    public void clickOnShowMore() {
        mPresenter.clickShowMore();
    }

    @OnClick(R.id.product_image)
    public void zoomImage() {
        startZoomTransition();
    }


    //region =========================== Animation =====================

    public void startAddToCardAnim() {
        final int cx = (mProductWrapper.getLeft()+mProductWrapper.getRight())/2; //вычисляем центр карточки по х
        final int cy = (mProductWrapper.getTop()+mProductWrapper.getBottom())/2; //вычисляем центр карточки по у
        final int radius = Math.max(mProductWrapper.getWidth(), mProductWrapper.getHeight()); //вычисляем радиус
        final Animator hideCircleAnim;
        final Animator showCircleAnim;
        Animator hideColorAnim = null;
        Animator showColorAnim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            /*создаем анимацию для объекта карточка, из центра, с максимального радиуса до 0*/
            hideCircleAnim = ViewAnimationUtils.createCircularReveal(mProductWrapper, cx, cy, radius, 0);
            hideCircleAnim.addListener(new AnimatorListenerAdapter() { //вешаем слушатель на окончание анимации, когда анимация заканчивается делаем карточку невидимой

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mProductWrapper.setVisibility(INVISIBLE);
                }
            });

            showCircleAnim = ViewAnimationUtils.createCircularReveal(mProductWrapper, cx, cy, 0, radius);
            showCircleAnim.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mProductWrapper.setVisibility(VISIBLE);
                }
            });

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                ColorDrawable cdr = ((ColorDrawable) mProductWrapper.getForeground());
                hideColorAnim = ObjectAnimator.ofArgb(mProductWrapper, "color",  getResources().getColor(R.color.colorAccent, null));
                showColorAnim = ObjectAnimator.ofArgb(mProductWrapper, "color", getResources().getColor(R.color.transparent, null));
            }

        } else {
            // TODO: 14.02.17 add any animation for old version device
            hideCircleAnim = ObjectAnimator.ofFloat(mProductWrapper, "alpha", 0);
            showCircleAnim = ObjectAnimator.ofFloat(mProductWrapper, "alpha", 1);
        }

        AnimatorSet hideSet = new AnimatorSet();
        AnimatorSet showSet = new AnimatorSet();
        AnimatorSet resultSet = new AnimatorSet();

        addAnimatorTogetherInSet(hideSet, hideCircleAnim, hideColorAnim);
        addAnimatorTogetherInSet(showSet, showCircleAnim, showColorAnim);

        hideSet.setDuration(800);
        hideSet.setInterpolator(new FastOutSlowInInterpolator());

        showSet.setStartDelay(400);
        showSet.setDuration(700);
        showSet.setInterpolator(new FastOutSlowInInterpolator());


        if((resultSet != null && !resultSet.isStarted()) || resultSet == null) {
            resultSet.playSequentially(hideSet,showSet);
            resultSet.start();
        }
    }

    private void addAnimatorTogetherInSet(AnimatorSet set, Animator... anims) {
        ArrayList<Animator> animatorList = new ArrayList();

        for (Animator animator : anims) {
            if(animator != null) {
                animatorList.add(animator);
            }
        }

        set.playTogether(animatorList);
    }


    private void startZoomTransition() {
        TransitionSet set = new TransitionSet();
        Transition  explode = new Explode(); //анимация исчезновения от эпицентра (почти как Slide, но от заданного эпицентра
        final Rect rect = new Rect(mProductImage.getLeft(), mProductImage.getTop(), mProductImage.getRight(), mProductImage.getBottom());
        explode.setEpicenterCallback(new Transition.EpicenterCallback() { //установка эпицентра
            @Override
            public Rect onGetEpicenter(Transition transition) {
                return rect;
            }
        });
        SidePropagation prop = new SidePropagation(); //чем удаленнее объект от эпицентра, тем позже начнется его анимация
        prop.setPropagationSpeed(4f);
        explode.setPropagation(prop);

        ChangeBounds bounds = new ChangeBounds();
        ChangeImageTransform imageTransform = new ChangeImageTransform();

        if(!isZoomed) {
            bounds.setStartDelay(100);
            imageTransform.setStartDelay(100);
        }


        set.addTransition(explode)
                .addTransition(bounds)
                .addTransition(imageTransform)
                .setDuration(1000);
//                .setInterpolator(new FastOutSlowInInterpolator());
        TransitionManager.beginDelayedTransition(mProductCard, set);

        if(mChildList == null) mChildList = ViewHelper.getChildExcludeView(mProductWrapper, R.id.product_image);

        ViewGroup.LayoutParams cardParam = mProductCard.getLayoutParams();
        cardParam.height = !isZoomed ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
        mProductCard.setLayoutParams(cardParam);

        ViewGroup.LayoutParams wrapParam = mProductWrapper.getLayoutParams();
        wrapParam.height = !isZoomed ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
        mProductWrapper.setLayoutParams(wrapParam);

        LinearLayout.LayoutParams imgParam;

        if(!isZoomed) {
            mInImageHeight = mProductImage.getHeight();
            imgParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mProductImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imgParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mInImageHeight);
            int defMargin = (int) (16 * mDen);
            imgParam.setMargins(defMargin, 0, defMargin, 0);
            mProductImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        mProductImage.setLayoutParams(imgParam);

        if(!isZoomed) {
            for(View view: mChildList) {
                view.setVisibility(GONE);
            }
        } else {
            for(View view: mChildList) {
                view.setVisibility(VISIBLE);
            }
        }

        isZoomed = !isZoomed;
    }


    //endregion


    //endregion
}
