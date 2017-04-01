package me.uptop.mvpgoodpractice.ui.screens.product_detail.comments;

import android.os.Bundle;

import java.util.List;

import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import me.uptop.mvpgoodpractice.BuildConfig;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.dto.CommentDto;
import me.uptop.mvpgoodpractice.data.storage.realm.CommentRealm;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.di.scopes.DaggerScope;
import me.uptop.mvpgoodpractice.flow.AbstractScreen;
import me.uptop.mvpgoodpractice.flow.Screen;
import me.uptop.mvpgoodpractice.mvp.models.DetailModel;
import me.uptop.mvpgoodpractice.mvp.presenters.AbstractPresenter;
import me.uptop.mvpgoodpractice.ui.screens.product_detail.ProductDetailScreen;
import mortar.MortarScope;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

@Screen(R.layout.screen_product_comments)
public class CommentsScreen extends AbstractScreen<ProductDetailScreen.Component> {
    private ProductRealm mProductRealm;

    public CommentsScreen(ProductRealm mProductRealm) {
        this.mProductRealm = mProductRealm;
    }

    @Override
    public Object createScreenComponent(ProductDetailScreen.Component parentComponent) {
//        return null;
        return DaggerCommentsScreen_Component.builder()
                .component(parentComponent)
                .module(new Module())
                .build();
    }

    //region ============================== DI ==============================

    @dagger.Module
    public class Module {
        @Provides
        @DaggerScope(CommentsScreen.class)
        CommentsPresenter provideCommentsPresenter() {
            return new CommentsPresenter(mProductRealm);
        }
    }

    @dagger.Component(dependencies = ProductDetailScreen.Component.class, modules = Module.class)
    @DaggerScope(CommentsScreen.class)
    public interface Component {
        void inject(CommentsPresenter presenter);

        void inject(CommentsView view);
        void inject(CommentsAdapter adapter);
    }

    //endregion

    //region ============================== Presenter ==============================

    public class CommentsPresenter extends AbstractPresenter<CommentsView, DetailModel> {
        private final ProductRealm mProduct;
        private RealmChangeListener mListener;

        public CommentsPresenter(ProductRealm mProduct) {
            this.mProduct = mProduct;
        }


        public void sendToServer(float rating, String addComment) {
            CommentRealm commentRealm = new CommentRealm(rating, addComment);

            switch (BuildConfig.FLAVOR) {
                case "base":
                    mModel.sendComment(mProduct.getId(), commentRealm);
                    break;
                case "realmMp":
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(realm1 -> mProduct.getCommentRealm().add(commentRealm));
                    realm.close();
                    break;
            }

            //обновление подписки на адаптер с его листом комментов
            Observable<CommentDto> newCommentObs = Observable.from(new CommentRealm[]{commentRealm})
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .map(CommentDto::new);

            mCompositeSubscription.add(subscribe(newCommentObs, new ViewSubscriber<CommentDto>() {
                @Override
                public void onNext(CommentDto commentDto) {
                    getView().getAdapter().addItem(commentDto);
                }
            }));
            getView().getAdapter().notifyDataSetChanged();
        }

        @Override
        protected void initActionBar() {

        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component)scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        public void dropView(CommentsView view) {
            mProduct.removeChangeListener(mListener);
            super.dropView(view);
        }

        public void addCommentSub() {
            RealmList<CommentRealm> comments = mProduct.getCommentRealm();
            Observable<CommentDto> commentsObs = Observable.from(comments)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .map(CommentDto::new);

            mCompositeSubscription.add(subscribe(commentsObs, new ViewSubscriber<CommentDto>() {
                @Override
                public void onNext(CommentDto commentDto) {
                    getView().getAdapter().addItem(commentDto);
                }
            }));
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);

            mListener = (RealmChangeListener<ProductRealm>) element -> CommentsPresenter.this.updateProductList(element);

            mProduct.addChangeListener(mListener);

            addCommentSub();

            getView().initView();
        }

        private void updateProductList(ProductRealm element) {
            Observable<List<CommentDto>> obs = Observable.from(element.getCommentRealm())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .map(CommentDto::new)
                    .toList();

            mCompositeSubscription.add(subscribe(obs, new ViewSubscriber<List<CommentDto>>() {
                @Override
                public void onNext(List<CommentDto> commentDtos) {
                    getView().getAdapter().reloadAdapter(commentDtos);
                }
            }));
        }
    }

    //endregion
}
