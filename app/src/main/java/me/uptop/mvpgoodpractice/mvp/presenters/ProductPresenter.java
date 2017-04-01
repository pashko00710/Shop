//package me.uptop.mvpgoodpractice.mvp.presenters;
//
//import javax.inject.Inject;
//
//import dagger.Provides;
//import me.uptop.mvpgoodpractice.data.storage.ProductDto;
//import me.uptop.mvpgoodpractice.di.DaggerService;
//import me.uptop.mvpgoodpractice.di.scopes.ProductScope;
//import me.uptop.mvpgoodpractice.mvp.models.ProductModel;
//import me.uptop.mvpgoodpractice.mvp.views.IProductView;
//
//public class ProductPresenter extends AbstractPresenter<IProductView> implements IProductPresenter {
//
//    private static final String TAG = "ProductPresenter";
//    @Inject
//    ProductModel mProductModel;
//
//    private ProductDto mProduct;
//
//    public ProductPresenter(ProductDto product){
//        Component component = DaggerService.getComponent(Component.class);
//        if(component == null) {
//            component = createDaggerComponent();
//            DaggerService.registerComponent(Component.class, component);
//        }
//        component.inject(this);
////        mProduct = product;
//
//    }
//
//    public void setProduct(ProductDto product) {
//        mProduct = product;
//    }
//
//    @Override
//    public void initView() {
//        if(getView() != null){
//            getView().showProductView(mProduct);
//        }
//    }
//
//
//    @Override
//    public void clickOnPlus() {
//        mProduct.addProduct();
//        mProductModel.updateProduct(mProduct);
//        if(getView()!=null){
//            getView().updateProductCountView(mProduct);
//        }
//    }
//
//    @Override
//    public void clickOnMinus() {
//        if(mProduct.getCount() > 0){
//            mProduct.deleteProduct();
//            mProductModel.updateProduct(mProduct);
//            if(getView()!=null){
//                getView().updateProductCountView(mProduct);
//            }
//        }
//    }
//
//    //region --------------------DI--------------------------------
//
//    @dagger.Module
//    public class Module {
//        @Provides
//        @ProductScope
//        ProductModel productModel() {
//            return new ProductModel();
//        }
//    }
//
//
//    @dagger.Component(modules = Module.class)
//    @ProductScope
//    interface Component {
//        void inject(ProductPresenter productPresenter);
////        void inject(ProductFactoryPresenter productFactoryPresenter);
//    }
//
//    private Component createDaggerComponent() {
//        return DaggerProductPresenter_Component.builder()
//                .module(new Module())
//                .build();
//    }
//
//    //endregion
//}
