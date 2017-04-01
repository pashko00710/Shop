package me.uptop.mvpgoodpractice.di.components;

import android.content.Context;

import dagger.Component;
import me.uptop.mvpgoodpractice.di.modules.AppModule;

@Component(modules = AppModule.class)
public interface AppComponent {
    Context getContext();
}
