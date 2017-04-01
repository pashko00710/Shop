//package me.uptop.mvpgoodpractice.mvp.presenters;
//
//import android.util.Log;
//
//import java.util.List;
//
//import javax.inject.Inject;
//
//import me.uptop.mvpgoodpractice.data.storage.ProductDto;
//import me.uptop.mvpgoodpractice.mvp.models.CatalogModel;
//import me.uptop.mvpgoodpractice.mvp.views.ICatalogView;
//
//import static android.content.ContentValues.TAG;
//
//public class CatalogPresenter extends AbstractPresenter<ICatalogView> implements ICatalogPresenter {
//    @Inject
//    RootPresenter mRootPresenter;
//    @Inject
//    CatalogModel mCatalogModel;
//
//    protected List<ProductDto> mProductList;
//
//    public CatalogPresenter() {
////        Component component = DaggerService.getComponent(Component.class);
////        if(component == null) {
////            component = createDaggerComponent();
////            DaggerService.registerComponent(Component.class, component);
////        }
////        component.inject(this);
//    }
//
//    @Override
//    public void initView() {
//        if(mProductList == null) {
//            mProductList = mCatalogModel.getProductList();
//        }
//
//        if(getView() != null){
//            getView().showCatalogView(mProductList);
//        }
//    }
//
//    @Override
//    public void clickOnBuyButton(int position) {
//        if(getView() != null) {
//            if(checkUserAuth()) {
//                Log.e(TAG, "clickOnBuyButton: "+mProductList.get(position).getProductName());
//                addBasketCounter();
//                getRootView().showMessage("Товар " + mProductList.get(position).getProductName() + " успешно добавлен корзину");
//                getRootView().showBasketCounter();
//            } else {
////                getView().showAuthScreen();
//            }
//        }
//    }
//
//    private void addBasketCounter() {
//        mCatalogModel.addBasketCounter();
//    }
//
//    public int getBasketCounter() {
//        return mCatalogModel.getBasketCounter();
//    }
//
//    public void saveBasketCounter(int counter) {
//        mCatalogModel.saveBasketCounter(counter);
//    }
//
//    @Override
//    public boolean checkUserAuth() {
//        return mCatalogModel.isUserAuth();
//    }
//
//    private IRootView getRootView() {
//        return mRootPresenter.getView();
//    }
//
//
//    //region --------------------DI--------------------------------
//
////    @dagger.Module
////    public class Module {
////        @Provides
////        @CatalogScope
////        CatalogModel provideCatalogModel() {
////            return new CatalogModel();
////        }
////    }
////
////
////    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
////    @CatalogScope
////    interface Component {
////        void inject(CatalogPresenter catalogPresenter);
////    }
//
////    private Component createDaggerComponent() {
////        return DaggerCatalogPresenter_Component.builder()
////                .component(DaggerService.getComponent(RootActivity.RootComponent.class))
////                .module(new Module())
////                .build();
////    }
//
//    //endregion
//}
