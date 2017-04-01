package me.uptop.mvpgoodpractice.mvp.models;

import com.birbit.android.jobqueue.JobManager;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.util.List;

import me.uptop.mvpgoodpractice.data.managers.DataManager;
import me.uptop.mvpgoodpractice.data.storage.dto.ProductDto;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;
import rx.Observable;

public class CatalogModel extends AbstractModel {

    public CatalogModel() {
    }

    //for tests
    public CatalogModel(DataManager dataManager, JobManager jobManager) {
        super(dataManager, jobManager);
    }

    public List<ProductDto> getProductList() {
        return mDataManager.getPreferencesManager().getProductList();
    }

    public boolean isUserAuth() {
        return mDataManager.getPreferencesManager().isUserAuth();
    }

    public void addBasketCounter() {
        mDataManager.getPreferencesManager().saveBasketCounter(mDataManager.getPreferencesManager().getBasketCounter() + 1);
    }

    @RxLogObservable
    public Observable<ProductRealm> getProductObs() {
        Observable<ProductRealm> disk = fromDisk();
        Observable<ProductRealm> network = fromNetwork();
        return Observable.mergeDelayError(disk, network)
                .distinct(ProductRealm::getId);
    }

    @RxLogObservable
    public Observable<ProductRealm> fromNetwork() {
        return mDataManager.getProductsObsFromNetwork();
    }

    @RxLogObservable
    public Observable<ProductRealm> fromDisk() {
        return mDataManager.getProductFromRealm();
    }

    public void addOrder(ProductRealm product) {
        mDataManager.addOrderFromRealm(product);
    }
}
