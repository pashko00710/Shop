package me.uptop.mvpgoodpractice.ui.screens.product_detail;

import android.content.Context;
import android.os.Bundle;

import com.squareup.picasso.Picasso;

import dagger.Provides;
import flow.TreeKey;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.di.scopes.DaggerScope;
import me.uptop.mvpgoodpractice.flow.AbstractScreen;
import me.uptop.mvpgoodpractice.flow.Screen;
import me.uptop.mvpgoodpractice.mvp.models.DetailModel;
import me.uptop.mvpgoodpractice.mvp.presenters.AbstractPresenter;
import me.uptop.mvpgoodpractice.mvp.presenters.IProductDetailPresenter;
import me.uptop.mvpgoodpractice.mvp.presenters.MenuItemHolder;
import me.uptop.mvpgoodpractice.mvp.presenters.RootPresenter;
import me.uptop.mvpgoodpractice.ui.activities.RootActivity;
import mortar.MortarScope;

@Screen(R.layout.screen_product_detail)
public class ProductDetailScreen extends AbstractScreen<RootActivity.RootComponent> implements TreeKey {
    private static final String TAG = "ProductDetailScreen";
    private ProductRealm mProductRealm;
    private AbstractScreen<RootActivity.RootComponent> mParentScreen;

    public ProductDetailScreen(ProductRealm product, AbstractScreen<RootActivity.RootComponent> parentScreen) {
        mProductRealm = product;
        mParentScreen = parentScreen;
    }

    @Override
    public Object getParentKey() {
        return mParentScreen;
    }

    @Override
    public Object createScreenComponent(RootActivity.RootComponent mParentScreen) {
        return DaggerProductDetailScreen_Component.builder()
                .rootComponent(mParentScreen)
                .module(new Module())
                .build();
    }

    //region ============================== DI ==============================

    @dagger.Module
    public class Module {

        @Provides
        @DaggerScope(ProductDetailScreen.class)
        ProductDetailPresenter provideProductDetailPresenter() {
            return new ProductDetailPresenter(mProductRealm);
        }

        @Provides
        @DaggerScope(ProductDetailScreen.class)
        DetailModel provideDetailModel() {
            return new DetailModel();
        }

    }

    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = ProductDetailScreen.Module.class)
    @DaggerScope(ProductDetailScreen.class)
    public interface Component {
        void inject(ProductDetailPresenter presenter);

        void inject(ProductDetailView view);

        DetailModel getDetailModel();
        RootPresenter getRootPresenter();
        Picasso getPicasso();
    }

    //endregion

    //region ============================== Presenter ==============================

    public class ProductDetailPresenter extends AbstractPresenter<ProductDetailView, DetailModel> implements IProductDetailPresenter {

        private final ProductRealm mProduct;

        public ProductDetailPresenter(ProductRealm productRealm) {
            mProduct = productRealm;
        }

        //region =========================== Getters =====================
//        public ProductRealm getProductRealm() {
//            return mProductRealm;
//        }
        //endregion

        //region ============================== Lifecycle ==============================

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            getView().initView(mProduct);
        }

        @Override
        public void dropView(ProductDetailView view) {
//            getView().destroyViewPager();
            super.dropView(view);
        }

        @Override
        protected void initActionBar() {
            mRootPresenter.newActionBarBuilder()
                    .setTitle(mProduct.getProductName())
                    .setBackArrow(true)
                    .addAction(new MenuItemHolder("В корзину", R.layout.icon_count_busket, item -> {
                        getRootView().showMessage("Перейти в корзину");
                        return true;
                    }))
                    .setTab(getView().getViewPager())
                    .build();
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        //endregion
    }


    public static class Factory {

        public static Context createChildContext(Context parentContext, AbstractScreen<ProductDetailScreen.Component> screen) {
            MortarScope parentScope = MortarScope.getScope(parentContext);
            MortarScope childScope;
            String scopeName = screen.getScopeName();

            if (parentScope.findChild(scopeName) == null) {
                childScope = parentScope.buildChild()
                        .withService(DaggerService.SERVICE_NAME,
                                screen.createScreenComponent(DaggerService.getDaggerComponent(parentContext)))
                        .build(scopeName);
            } else {
                childScope = parentScope.findChild(scopeName);
            }

            return childScope.createContext(parentContext);
        }
    }

    //endregion
}
