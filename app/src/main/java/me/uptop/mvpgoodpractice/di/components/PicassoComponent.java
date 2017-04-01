package me.uptop.mvpgoodpractice.di.components;

import com.squareup.picasso.Picasso;

import dagger.Component;
import me.uptop.mvpgoodpractice.di.modules.PicassoCacheModule;
import me.uptop.mvpgoodpractice.di.scopes.RootScope;

@Component(dependencies = AppComponent.class, modules = PicassoCacheModule.class)
@RootScope
public interface PicassoComponent {
    Picasso getPicasso();
}
