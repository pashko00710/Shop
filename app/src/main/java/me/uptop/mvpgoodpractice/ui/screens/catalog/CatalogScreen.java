package me.uptop.mvpgoodpractice.ui.screens.catalog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import java.util.Locale;

import dagger.Provides;
import flow.Flow;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.di.scopes.DaggerScope;
import me.uptop.mvpgoodpractice.flow.AbstractScreen;
import me.uptop.mvpgoodpractice.flow.Screen;
import me.uptop.mvpgoodpractice.mvp.models.CatalogModel;
import me.uptop.mvpgoodpractice.mvp.presenters.AbstractPresenter;
import me.uptop.mvpgoodpractice.mvp.presenters.ICatalogPresenter;
import me.uptop.mvpgoodpractice.mvp.presenters.MenuItemHolder;
import me.uptop.mvpgoodpractice.mvp.presenters.RootPresenter;
import me.uptop.mvpgoodpractice.ui.activities.RootActivity;
import me.uptop.mvpgoodpractice.ui.screens.auth.AuthScreen;
import me.uptop.mvpgoodpractice.ui.screens.product.ProductScreen;
import mortar.MortarScope;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@Screen(R.layout.screen_catalog)
public class CatalogScreen extends AbstractScreen<RootActivity.RootComponent> {

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerCatalogScreen_Component.builder()
                .rootComponent(parentComponent)
                .module(new Module())
                .build();
    }

    //region ============================== DI ===================================


    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(CatalogScreen.class)
        CatalogModel provideCatalogModel() {
            return new CatalogModel();
        }

        @Provides
        @DaggerScope(CatalogScreen.class)
        CatalogPresenter provideCatalogPresenter() {
            return new CatalogPresenter();
        }
    }


    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(CatalogScreen.class)
    public interface Component {
        void inject(CatalogPresenter catalogPresenter);
        void inject(CatalogView catalogView);

        CatalogModel catalogModel();

        Picasso getPicasso();

        RootPresenter getRootPresenter();
    }

    //endregion

    //region ============================== Presenter ===================================
    public static class CatalogPresenter extends AbstractPresenter<CatalogView, CatalogModel> implements ICatalogPresenter {
        private int lastPagerPosition;

        public CatalogPresenter() {
        }

        //for tests
        public CatalogPresenter(RootPresenter mockRootPresenter, CatalogModel mockModel) {
            this.mRootPresenter = mockRootPresenter;
            this.mModel = mockModel;
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            mCompositeSubscription.add(subscribeOnProductRealmObs());
        }

        @Override
        protected void initActionBar() {
            MenuItem.OnMenuItemClickListener listener = item -> {
                getRootView().showMessage("hello cart");
                return true;
            };

            mRootPresenter.newActionBarBuilder()
                    .setTitle("Каталог")
                    .addAction(new MenuItemHolder("В корзину", R.layout.icon_count_busket, listener))
                    .build();
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component)scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        public void clickOnBuyButton(int position) {
            if(getView() != null) {
                if(checkUserAuth() && getRootView() != null) {
                    getView().getCurrentProductView().startAddToCardAnim();
                    addBasketCounter();
                    getRootView().showMessage("Товар " + getView().getCurrentProductView().getProductRealm().getProductName() + " успешно добавлен корзину");
                    mModel.addOrder(getView().getCurrentProductView().getProductRealm());
                    getRootView().showBasketCounter();
                } else {
                    Flow.get(getView()).set(new AuthScreen("Catalog"));
                }
            }
        }

        private Subscription subscribeOnProductRealmObs() {
            if(getRootView() != null) {
                getRootView().showLoad();
            }

            return mModel.getProductObs()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RealmSubscriber());

        }

        private void addBasketCounter() {
            mModel.addBasketCounter();
        }

        @Override
        public boolean checkUserAuth() {
            return mModel.isUserAuth();
        }

        private class RealmSubscriber extends Subscriber<ProductRealm> {
            CatalogAdapter mAdapter = getView().getAdapter();

            @Override
            public void onCompleted() {
                Log.e("this", "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                try {
                    getRootView().hideLoad();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                if(getRootView() != null) {
                    getRootView().showError(e);
                }
            }

            @Override
            public void onNext(ProductRealm productRealm) {
                mAdapter.addItem(productRealm);
                if(mAdapter.getCount() -1 == lastPagerPosition) {
                    getRootView().hideLoad();
                    getView().showCatalogView();
                }
            }
        }

        @Override
        public void dropView(CatalogView view) {
            lastPagerPosition = getView().getCurrentPagerPosition();
            super.dropView(view);
        }
    }

    //endregion

    public static class Factory {
        public static Context createProductContext(ProductRealm productDto, Context parentContext) {
            MortarScope parentScope = MortarScope.getScope(parentContext);
            MortarScope childScope = null;
            ProductScreen productScreen = new ProductScreen(productDto);
//            String scopeName = String.format("%s_%d", productScreen.getScopeName(), productDto.getId());
            String scopeName = String.format(Locale.ENGLISH, "%s_%s", productScreen.getScopeName(), productDto.getId());


            if(parentScope.findChild(scopeName) == null) {
                childScope = parentScope.buildChild()
                        .withService(DaggerService.SERVICE_NAME,
                                productScreen.createScreenComponent((Component) DaggerService.<ProductScreen.Component>getDaggerComponent(parentContext)))
                        .build(scopeName);
            } else {
                childScope = parentScope.findChild(scopeName);
            }
            return childScope.createContext(parentContext);
        }
    }

}
