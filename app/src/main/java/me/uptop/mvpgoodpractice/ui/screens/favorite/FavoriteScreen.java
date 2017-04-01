package me.uptop.mvpgoodpractice.ui.screens.favorite;

import android.os.Bundle;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import dagger.Provides;
import flow.Flow;
import io.realm.Realm;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.di.scopes.DaggerScope;
import me.uptop.mvpgoodpractice.flow.AbstractScreen;
import me.uptop.mvpgoodpractice.flow.Screen;
import me.uptop.mvpgoodpractice.mvp.models.FavoriteModel;
import me.uptop.mvpgoodpractice.mvp.presenters.AbstractPresenter;
import me.uptop.mvpgoodpractice.mvp.presenters.MenuItemHolder;
import me.uptop.mvpgoodpractice.mvp.presenters.RootPresenter;
import me.uptop.mvpgoodpractice.ui.activities.RootActivity;
import me.uptop.mvpgoodpractice.ui.screens.product_detail.ProductDetailScreen;
import mortar.MortarScope;

@Screen(R.layout.screen_favorite)
public class FavoriteScreen extends AbstractScreen<RootActivity.RootComponent> {

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerFavoriteScreen_Component.builder()
                .rootComponent(parentComponent)
                .module(new Module())
                .build();
    }
    //region ============================== DI ===================================


    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(FavoriteScreen.class)
        FavoriteModel provideFavoriteModel() {
            return new FavoriteModel();
        }

        @Provides
        @DaggerScope(FavoriteScreen.class)
        FavoritePresenter provideCatalogPresenter() {
            return new FavoritePresenter();
        }
    }


    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @DaggerScope(FavoriteScreen.class)
    public interface Component {
        void inject(FavoritePresenter favoritePresenter);
        void inject(FavoriteView favoriteView);
        void inject(FavoriteAdapter adapter);

        FavoriteModel favoriteModel();

        Picasso getPicasso();

        RootPresenter getRootPresenter();
    }

    //endregion

    //region ============================== Presenter ===================================
    public class FavoritePresenter extends AbstractPresenter<FavoriteView, FavoriteModel> {

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
            getView().showFavoriteList(mModel.getAllFavorites());
        }

        //endregion

        public void onProductImageClick(ProductRealm product) {
            Flow.get(getView()).set(new ProductDetailScreen(product, new FavoriteScreen()));
        }

        public void onCartClick(ProductRealm product) {
            //TODO : implement this
        }

        public void onFavoriteClick(ProductRealm product) {
            if(getView() != null) getView().showOnRemoveFromFavoriteDialog(product);
        }

        public void deleteProductFromFavorites(ProductRealm product) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> product.changeFavorite());
            realm.close();
        }
    }
    //endregion
}
