package me.uptop.mvpgoodpractice.di.modules;

import dagger.Provides;
import me.uptop.mvpgoodpractice.di.scopes.RootScope;
import me.uptop.mvpgoodpractice.mvp.models.AccountModel;
import me.uptop.mvpgoodpractice.mvp.models.RootModel;
import me.uptop.mvpgoodpractice.mvp.presenters.RootPresenter;

@dagger.Module
public class RootModule {
    @Provides
    @RootScope
    RootModel provideRootModel() {
        return new RootModel();
    }

    @Provides
    @RootScope
    AccountModel provideAccountModel() {
        return new AccountModel();
    }

    @Provides
    @RootScope
    RootPresenter provideRootPresenter() {
        return new RootPresenter();
    }
}
