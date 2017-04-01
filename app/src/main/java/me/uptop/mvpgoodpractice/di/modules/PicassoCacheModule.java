package me.uptop.mvpgoodpractice.di.modules;

import android.content.Context;
import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import me.uptop.mvpgoodpractice.di.scopes.RootScope;

@Module
public class PicassoCacheModule {
    @Provides
    @RootScope
    Picasso providePicasso(Context context) {
        OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(context);
        Picasso picasso = new Picasso.Builder(context)
                .downloader(okHttp3Downloader)
                .debugging(true)
                .listener((picasso1, uri, exception) -> {
                    Log.e("this", "onImageLoadFailed: " + exception);
                    exception.printStackTrace();
                })

                .build();
//        Picasso.setSingletonInstance(picasso);
        return picasso;
    }
}
