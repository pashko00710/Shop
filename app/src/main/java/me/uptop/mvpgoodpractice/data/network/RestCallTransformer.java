package me.uptop.mvpgoodpractice.data.network;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.fernandocejas.frodo.annotation.RxLogObservable;

import me.uptop.mvpgoodpractice.data.managers.DataManager;
import me.uptop.mvpgoodpractice.data.network.error.ErrorUtils;
import me.uptop.mvpgoodpractice.data.network.error.ForbiddenApiError;
import me.uptop.mvpgoodpractice.data.network.error.NetworkAvailableError;
import me.uptop.mvpgoodpractice.utils.ConstantManager;
import me.uptop.mvpgoodpractice.utils.NetworkStatusChecker;
import retrofit2.Response;
import rx.Observable;

import static android.support.annotation.VisibleForTesting.NONE;
import static me.uptop.mvpgoodpractice.data.managers.DataManager.TAG;

public class RestCallTransformer<R> implements Observable.Transformer<Response<R>, R> {
    private boolean mTestmode;

    @Override
    @RxLogObservable
    public Observable<R> call(Observable<Response<R>> responseObservable) {
        Observable<Boolean> networkStatus;

        if (mTestmode) {
            networkStatus = Observable.just(true);
        } else {
            networkStatus = NetworkStatusChecker.isInternetAvialable();
        }

       return networkStatus
                .flatMap(aBoolean -> aBoolean ? responseObservable : Observable.error(new NetworkAvailableError()))
                .flatMap(rResponse -> {
                    Log.e(TAG, "callRest: "+rResponse.code());
                    switch (rResponse.code()) {
                        case 200:
                            String lastModified = rResponse.headers().get(ConstantManager.LAST_MODIFIED_HEADER);
                            if(lastModified != null) {
                                DataManager.getInstance().getPreferencesManager().saveLastProductUpdate(lastModified);
                            }
                            return Observable.just(rResponse.body());
                        case 304:
                            return Observable.empty();
                        case 403:
                            return Observable.error(new ForbiddenApiError());
                        default:
                            return Observable.error(ErrorUtils.parseError(rResponse));
                    }
                });
    }

    @VisibleForTesting(otherwise = NONE)
    public void setTestMode() {
        mTestmode = true;
    }
}
