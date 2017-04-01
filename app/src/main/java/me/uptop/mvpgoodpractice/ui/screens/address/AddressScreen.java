package me.uptop.mvpgoodpractice.ui.screens.address;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;

import dagger.Provides;
import flow.Flow;
import flow.TreeKey;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.dto.UserAddressDto;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.di.scopes.AddressScope;
import me.uptop.mvpgoodpractice.flow.AbstractScreen;
import me.uptop.mvpgoodpractice.flow.Screen;
import me.uptop.mvpgoodpractice.mvp.models.AccountModel;
import me.uptop.mvpgoodpractice.mvp.presenters.AbstractPresenter;
import me.uptop.mvpgoodpractice.mvp.presenters.IAddressPresenter;
import me.uptop.mvpgoodpractice.mvp.presenters.MenuItemHolder;
import me.uptop.mvpgoodpractice.ui.screens.account.AccountScreen;
import mortar.MortarScope;

@Screen(R.layout.screen_add_address)
public class AddressScreen extends AbstractScreen<AccountScreen.Component> implements TreeKey {
    @Nullable
    private UserAddressDto mAddressDto;

    public AddressScreen(@Nullable UserAddressDto mAddressDto) {
        this.mAddressDto = mAddressDto;
    }

    @Override
    public boolean equals(Object o) {
        if(mAddressDto != null) {
            return o instanceof AddressScreen && mAddressDto.equals(((AddressScreen) o).mAddressDto);
        }
        return super.equals(o);
    }


    @Override
    public int hashCode() {
        return mAddressDto != null ? mAddressDto.hashCode() : super.hashCode();
    }

    @Override
    public Object createScreenComponent(AccountScreen.Component parentComponent) {
        return DaggerAddressScreen_Component.builder()
                .component(parentComponent)
                .module(new Module())
                .build();
    }

    @Override
    public Object getParentKey() {
        return new AccountScreen();
    }

    //region ===================== DI =====================

    @dagger.Module
    public class Module {
        @Provides
        @AddressScope
        AddressPresenter provideAddressPresenter() {
            return new AddressPresenter();
        }
    }

    @AddressScope
    @dagger.Component(dependencies = AccountScreen.Component.class, modules = Module.class)
    public interface Component {
        void inject(AddressPresenter presenter);

        void inject(AddressView view);
    }

    //endregion

    //region ===================== Presenter =====================

    public class AddressPresenter extends AbstractPresenter<AddressView, AccountModel> implements IAddressPresenter {

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            if(mAddressDto != null && getView() != null) {
                getView().initView(mAddressDto);
            }
        }

        @Override
        protected void initActionBar() {
            String title = "Добавление адреса";
            if(mAddressDto != null) {
                title = "Редактирование адреса";
            }

            mRootPresenter.newActionBarBuilder()
                    .setTitle(title)
                    .setBackArrow(true)
                    .build();
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component) scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        public void clickOnAddAddress() {
            // TODO: 28.11.2016 save address in model
            if (getView() != null) {
                mModel.updateOrInsertAddress(getView().getUserAddress());
                goBackToParentScreen();
            }
        }

        @Override
        public void goBackToParentScreen() {
            Flow.get(getView()).goBack();
        }

//        @Nullable
//        IRootView getRootView() {
//            return mRootPresenter.getView();
//        }
    }

    //endregion
}
