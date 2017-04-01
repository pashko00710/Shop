package me.uptop.mvpgoodpractice.di.modules;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.uptop.mvpgoodpractice.data.managers.PreferencesManager;

@Module
public class LocalModule extends FlavorLocalModule {
    @Provides
    @Singleton
    PreferencesManager providePreferencesManager(Context context) {
        return new PreferencesManager(context);
    }

//    @Provides
//    @Singleton
//    RealmManager provideRealmManager() {
//        return new RealmManager();
//    }
}
