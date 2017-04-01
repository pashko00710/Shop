package me.uptop.mvpgoodpractice.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.uptop.mvpgoodpractice.data.managers.DataManager;

@Module
public class ModelModule extends FlavorModelModule {
    @Provides
    @Singleton
    DataManager provideDataManager() {
        return DataManager.getInstance();
    }
}
