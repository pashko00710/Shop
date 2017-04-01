package me.uptop.mvpgoodpractice.di.components;

import javax.inject.Singleton;

import dagger.Component;
import me.uptop.mvpgoodpractice.di.modules.ModelModule;
import me.uptop.mvpgoodpractice.mvp.models.AbstractModel;

@Component(modules = ModelModule.class)
@Singleton
public interface ModelComponent {
    void inject(AbstractModel abstractModel);
}
