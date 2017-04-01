package me.uptop.mvpgoodpractice.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import rx.Observable;

public class NetworkStatusChecker {
    public static boolean isNetworkAvailable (){
        ConnectivityManager cm = (ConnectivityManager) MvpAuthApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static Observable<Boolean> isInternetAvialable() {
        return Observable.just(isNetworkAvailable());
    }
}
