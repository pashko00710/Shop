package me.uptop.mvpgoodpractice.ui.screens.catalog;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.mvp.models.CatalogModel;
import me.uptop.mvpgoodpractice.mvp.presenters.RootPresenter;
import me.uptop.mvpgoodpractice.ui.activities.RootActivity;
import me.uptop.mvpgoodpractice.ui.screens.auth.AuthScreen;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class CatalogPresenterTest {
    @Mock
    CatalogView mockView;
    @Mock
    Context mockContext;
    @Mock
    CatalogModel mockModel;
    @Mock
    RootPresenter mockRootPresenter;
    @Mock
    RootActivity mockRootView;

    private CatalogScreen.CatalogPresenter mPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        BundleServiceRunner mockBundleServiceRunner = new BundleServiceRunner();
        MortarScope mockMortarScope = MortarScope.buildRootScope()
                .withService(BundleServiceRunner.SERVICE_NAME, mockBundleServiceRunner)
                .withService(DaggerService.SERVICE_NAME, mock(AuthScreen.Component.class))
                .build("MockScope");

        given(mockContext.getSystemService(BundleServiceRunner.SERVICE_NAME)).willReturn(mockBundleServiceRunner);//когда у контекста запрашивает системный сервис с названием SERVICE_NAME возвратить мокированный BundleServiceRunner
        given(mockContext.getSystemService(MortarScope.class.getName()))
                .willReturn(mockMortarScope); //когда запрашивается системный сервис с именем MortarScope вернуть замокированный скоп
        given(mockView.getContext()).willReturn(mockContext);
        given(mockRootPresenter.getRootView()).willReturn(mockRootView);

        mPresenter = new CatalogScreen.CatalogPresenter(mockRootPresenter,mockModel);
    }

    @Test
    public void clickOnBuyButton_AUTH_USER() throws Exception {
        given(mockModel.isUserAuth()).willReturn(true);
    }

}