package me.uptop.mvpgoodpractice.ui.screens.cart;

import android.os.Bundle;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import dagger.Provides;
import flow.Flow;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.realm.OrdersRealm;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.di.scopes.DaggerScope;
import me.uptop.mvpgoodpractice.flow.AbstractScreen;
import me.uptop.mvpgoodpractice.flow.Screen;
import me.uptop.mvpgoodpractice.mvp.models.CartModel;
import me.uptop.mvpgoodpractice.mvp.presenters.AbstractPresenter;
import me.uptop.mvpgoodpractice.mvp.presenters.MenuItemHolder;
import me.uptop.mvpgoodpractice.mvp.presenters.RootPresenter;
import me.uptop.mvpgoodpractice.ui.activities.RootActivity;
import me.uptop.mvpgoodpractice.ui.screens.product_detail.ProductDetailScreen;
import mortar.MortarScope;

@Screen(R.layout.screen_cart)
public class CartScreen extends AbstractScreen<RootActivity.RootComponent> {
    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerCartScreen_Component.builder()
                .rootComponent(parentComponent)
                .module(new Module())
                .build();
    }

    //region ============================== DI ===================================


    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(CartScreen.class)
        CartModel provideFavoriteModel() {
            return new CartModel();
        }

        @Provides
        @DaggerScope(CartScreen.class)
        CartPresenter provideCartPresenter() {
            return new CartPresenter();
        }
    }


    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(CartScreen.class)
    public interface Component {
        void inject(CartPresenter cartPresenter);
        void inject(CartView cartView);
        void inject(CartAdapter adapter);

        CartModel cartModel();

        Picasso getPicasso();

        RootPresenter getRootPresenter();
    }

    //endregion

    //region ============================== Presenter ===================================
    public class CartPresenter extends AbstractPresenter<CartView, CartModel> {
        private ProductRealm product;

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
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            getView().showCartList(getOrders());
            getView().initPrice(getOrders());
        }


        public RealmResults<OrdersRealm> getOrders() {
            return mModel.getAllOrders();
        }

        public void onProductImageClick(OrdersRealm order) {
            Realm realm = Realm.getDefaultInstance();
            product = realm.where(ProductRealm.class).equalTo("id", order.getProductId()).findFirst();
            realm.close();
            Flow.get(getView()).set(new ProductDetailScreen(product, new CartScreen()));
        }

        public void onDeleteProduct(OrdersRealm order) {
            Realm realm = Realm.getDefaultInstance();
            RealmObject results = realm.where(OrdersRealm.class).equalTo("id", order.getId()).findFirst();
            realm.executeTransaction(realm1 -> results.deleteFromRealm());
            realm.close();
            getView().initPrice(getOrders());
        }

        //endregion
    }
    //endregion
}
