package me.uptop.mvpgoodpractice.ui.screens.product_detail.description;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.dto.DescriptionDto;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.di.scopes.DaggerScope;
import me.uptop.mvpgoodpractice.flow.AbstractScreen;
import me.uptop.mvpgoodpractice.flow.Screen;
import me.uptop.mvpgoodpractice.mvp.models.DetailModel;
import me.uptop.mvpgoodpractice.mvp.presenters.AbstractPresenter;
import me.uptop.mvpgoodpractice.ui.screens.product_detail.ProductDetailScreen;
import mortar.MortarScope;

@Screen(R.layout.screen_product_desc)
public class DescriptionScreen extends AbstractScreen<ProductDetailScreen.Component> {
    private ProductRealm mProductDto;

    public DescriptionScreen(ProductRealm productRealm) {
        mProductDto = productRealm;
    }

    @Override
    public Object createScreenComponent(ProductDetailScreen.Component parentComponent) {
        return DaggerDescriptionScreen_Component.builder()
                .module(new Module())
                .component(parentComponent)
                .build();
    }

    //region ============================== DI ==============================

    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(DescriptionScreen.class)
        DescriptionPresenter provideDescriptionPresenter() {
            return new DescriptionPresenter(mProductDto);
        }
    }

    @dagger.Component(dependencies = ProductDetailScreen.Component.class, modules = Module.class)
    @DaggerScope(DescriptionScreen.class)
    public interface Component {
        void inject(DescriptionPresenter presenter);

        void inject(DescriptionView view);
    }

    //endregion

    //region ============================== Presenter ==============================

    public class DescriptionPresenter extends AbstractPresenter<DescriptionView, DetailModel> {
        private final ProductRealm mProductRealm;
        private RealmChangeListener mListener;

        public DescriptionPresenter(ProductRealm productRealm) {
            mProductRealm = productRealm;
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            getView().initView(new DescriptionDto(mProductRealm));

            mListener = element -> {
                if(getView() != null) {
                    getView().initView(new DescriptionDto(mProductRealm));
                }
            };

            mProductRealm.addChangeListener(mListener);
        }

        @Override
        public void dropView(DescriptionView view) {
            mProductRealm.removeChangeListener(mListener);
            super.dropView(view);
        }

        @Override
        protected void initActionBar() {
            //empty
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        //
//        public ProductRealm getProductRealm() {
//            return mProductRealm;
//        }



        public void clickOnPlus() {
            if(getView() != null) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(realm1 -> mProductRealm.add());
                realm.close();
            }

        }

        public void clickOnMinus() {
            if(getView() != null) {
                if(mProductRealm.getCount() > 0) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(realm1 -> mProductRealm.remove());
                    realm.close();
                }
            }
        }

        public void clickIsFavorite() {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> mProductRealm.changeFavorite());
            if(mProductRealm.isFavorite() == true) {
                getView().favoriteFab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFA815")));
            } else {
//                realm.executeTransaction(realm1 -> mProductRealm.changeFavorite());
                getView().favoriteFab.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            }
            realm.close();
        }

    }

    //endregion
}
