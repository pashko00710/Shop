package me.uptop.mvpgoodpractice.di.components;

import javax.inject.Singleton;

import dagger.Component;
import me.uptop.mvpgoodpractice.data.managers.DataManager;
import me.uptop.mvpgoodpractice.di.modules.LocalModule;
import me.uptop.mvpgoodpractice.di.modules.NetworkModule;

@Component(dependencies = AppComponent.class, modules = {NetworkModule.class, LocalModule.class})
@Singleton
public interface DataManagerComponent {
    void inject(DataManager dataManager);
}
