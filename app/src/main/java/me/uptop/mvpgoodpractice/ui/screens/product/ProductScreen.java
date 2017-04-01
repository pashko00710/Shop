package me.uptop.mvpgoodpractice.ui.screens.product;

import android.os.Bundle;

import dagger.Provides;
import flow.Flow;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.dto.ProductDto;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.di.scopes.ProductScope;
import me.uptop.mvpgoodpractice.flow.AbstractScreen;
import me.uptop.mvpgoodpractice.flow.Screen;
import me.uptop.mvpgoodpractice.mvp.models.CatalogModel;
import me.uptop.mvpgoodpractice.mvp.presenters.AbstractPresenter;
import me.uptop.mvpgoodpractice.mvp.presenters.IProductPresenter;
import me.uptop.mvpgoodpractice.ui.screens.catalog.CatalogScreen;
import me.uptop.mvpgoodpractice.ui.screens.product_detail.ProductDetailScreen;
import mortar.MortarScope;

@Screen(R.layout.screen_product)
public class ProductScreen extends AbstractScreen<CatalogScreen.Component> {
    private ProductRealm mProductRealm;

    public ProductScreen(ProductRealm productRealm) {
        mProductRealm = productRealm;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ProductScreen && mProductRealm.equals(((ProductScreen)o).mProductRealm);
    }

    @Override
    public int hashCode() {
        return mProductRealm.hashCode();
    }

    @Override
    public Object createScreenComponent(CatalogScreen.Component parentComponent) {
        return DaggerProductScreen_Component.builder()
                .component(parentComponent)
                .module(new Module())
                .build();
    }

    //region =========================== DI =====================
    @dagger.Module
    public class Module {
        @Provides
        @ProductScope
        ProductScreen.ProductPresenter provideProductPresenter() {
            return new ProductScreen.ProductPresenter(mProductRealm);
        }
    }

    @dagger.Component(dependencies = CatalogScreen.Component.class, modules = Module.class)
    @ProductScope
    public interface Component {
        void inject(ProductPresenter productPresenter);
        void inject(ProductView productView);
    }
    //endregion

    //region =========================== Presenter =====================
    public class ProductPresenter extends AbstractPresenter<ProductView, CatalogModel> implements IProductPresenter {
        private ProductRealm mProduct;
        private RealmChangeListener mListener;


//        public boolean isZoomed() {
//            return isZoomed;
//        }
//
//        public void setZoomed(boolean zoomed) {
//            isZoomed = zoomed;
//        }

        public ProductPresenter(ProductRealm productRealm) {
            mProduct = productRealm;
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            if(mProduct.isValid()) {
                getView().showProductView(new ProductDto(mProduct));

                mListener = element -> {
                    if(getView() != null) {
                    getView().showProductView(new ProductDto(mProduct));
                }};

                mProduct.addChangeListener(mListener);
            }
        }

        public ProductRealm getProduct() {
            return mProduct;
        }

        @Override
        public void dropView(ProductView view) {
            mProduct.removeChangeListener(mListener);
            super.dropView(view);
        }

        @Override
        protected void initActionBar() {
            //empty
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component)scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        //region =========================== IProductPresenter =====================
        @Override
        public void clickOnPlus() {
            if(getView() != null) {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(realm1 -> mProduct.add());
                realm.close();
            }
        }


        @Override
        public void clickOnMinus() {
            if(getView() != null) {
                if(mProduct.getCount() > 0){
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(realm1 -> mProduct.remove());
                    realm.close();
                }
            }
        }

        public void clickFavorite() {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> mProduct.changeFavorite());
            realm.close();
        }

        public void clickShowMore() {
            Flow.get(getView()).set(new ProductDetailScreen(mProduct, new CatalogScreen()));
        }
        //endregion
    }

    //endregion

}
