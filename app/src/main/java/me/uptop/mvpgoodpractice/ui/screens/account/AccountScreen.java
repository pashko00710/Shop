package me.uptop.mvpgoodpractice.ui.screens.account;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import dagger.Provides;
import flow.Flow;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.dto.ActivityResultDto;
import me.uptop.mvpgoodpractice.data.storage.dto.UserAddressDto;
import me.uptop.mvpgoodpractice.data.storage.dto.UserInfoDto;
import me.uptop.mvpgoodpractice.data.storage.dto.UserSettingsDto;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.di.scopes.AccountScope;
import me.uptop.mvpgoodpractice.flow.AbstractScreen;
import me.uptop.mvpgoodpractice.flow.Screen;
import me.uptop.mvpgoodpractice.mvp.models.AccountModel;
import me.uptop.mvpgoodpractice.mvp.presenters.AbstractPresenter;
import me.uptop.mvpgoodpractice.mvp.presenters.IAccountPresenter;
import me.uptop.mvpgoodpractice.mvp.presenters.MenuItemHolder;
import me.uptop.mvpgoodpractice.mvp.presenters.RootPresenter;
import me.uptop.mvpgoodpractice.ui.activities.RootActivity;
import me.uptop.mvpgoodpractice.ui.screens.address.AddressScreen;
import me.uptop.mvpgoodpractice.utils.ConstantManager;
import mortar.MortarScope;
import rx.Observable;
import rx.Subscription;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Environment.DIRECTORY_PICTURES;
import static java.text.DateFormat.MEDIUM;
import static me.uptop.mvpgoodpractice.ui.screens.account.AccountView.EDIT_STATE;
import static me.uptop.mvpgoodpractice.utils.ConstantManager.REQUEST_PROFILE_PHOTO_CAMERA;
import static me.uptop.mvpgoodpractice.utils.ConstantManager.REQUEST_PROFILE_PHOTO_PICKER;

@Screen(R.layout.screen_account)
public class AccountScreen extends AbstractScreen<RootActivity.RootComponent> {
    public static final String TAG = "AccountScreen";

    @Override
    public Object createScreenComponent(RootActivity.RootComponent parentComponent) {
        return DaggerAccountScreen_Component.builder()
                .rootComponent(parentComponent)
                .module(new Module())
                .build();
    }

    //region =========================== DI =====================
    @dagger.Module
    public class Module {
        @Provides
        @AccountScope
        AccountPresenter provideAccountPresenter() {
            return new AccountPresenter();
        }
    }
    @dagger.Component(dependencies = RootActivity.RootComponent.class, modules = Module.class)
    @AccountScope
    public interface Component {
        void inject(AccountPresenter accountPresenter);
        void inject(AccountView accountView);

        RootPresenter getRootPresenter();
        AccountModel getAccountModel();
    }
    //endregion

    //region =========================== Presenter =====================
    public class AccountPresenter extends AbstractPresenter<AccountView, AccountModel> implements IAccountPresenter {
        private int mCustomState = 1;

        public int getCustomState() {
            return mCustomState;
        }

        public void setCustomState(int mCustomState) {
            this.mCustomState = mCustomState;
        }

//        private Uri mAvatarUri;
        private Subscription addressSub;
        private Subscription settingsSub;
        private File mPhotoFile;
        private Subscription mActivityResultSub;
        private Subscription mUserInfoSub;
        
        //region =========================== Lifecycle =====================
        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            if(getView() != null && getRootView() != null) {
                getView().initView();
//                mAvatarUri = Uri.parse(mAccountModel.getUserDto().getAvatar());
            }

            subscribeOnAddressObs();
            subscribeOnSettingsObs();
            subscribeOnUserInfoObs();
        }

        @Override
        protected void initActionBar() {
            MenuItem.OnMenuItemClickListener listener = item -> {
                getRootView().showMessage("hello cart");
                return true;
            };

            mRootPresenter.newActionBarBuilder()
                    .setTitle("Аккаунт")
                    .addAction(new MenuItemHolder("В корзину", R.layout.icon_count_busket, listener))
                    .build();
        }

        @Override
        protected void initDagger(MortarScope scope) {
            ((Component)scope.getService(DaggerService.SERVICE_NAME)).inject(this);
        }

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);
            subscribeOnActivityResult();
        }

        @Override
        protected void onSave(Bundle outState) {
            super.onSave(outState);
            addressSub.unsubscribe();
            settingsSub.unsubscribe();
            mUserInfoSub.unsubscribe();
        }

        @Override
        protected void onExitScope() {
            mActivityResultSub.unsubscribe();
            super.onExitScope();
        }

        //endregion


        //region =========================== Subscription =====================
        private void subscribeOnAddressObs() {

            addressSub = subscribe(mModel.getAddressObs(), new ViewSubscriber<UserAddressDto>() {
                @Override
                public void onNext(UserAddressDto userAddressDto) {
                    if(getView() != null) {
                        getView().getAdapter().addItem(userAddressDto);
                    }
                }
            });
        }

        private void updateListView() {
            getView().getAdapter().reloadAdapter();
            subscribeOnAddressObs();
        }


        private void subscribeOnSettingsObs() {
            settingsSub = subscribe(mModel.getUserSettingsObs(), new ViewSubscriber<UserSettingsDto>() {
                @Override
                public void onNext(UserSettingsDto userSettingsDto) {
                    if(getView() != null) {
                        getView().initSettings(userSettingsDto);
                    }
                }
            });
        }


        private void subscribeOnActivityResult() {
            Observable<ActivityResultDto> activityResultObs = mRootPresenter.getActivityResultDtoObs()
                    .filter(activityResultDto -> activityResultDto.getResultCode() == Activity.RESULT_OK);

            mActivityResultSub = subscribe(activityResultObs, new ViewSubscriber<ActivityResultDto>() {
                @Override
                public void onNext(ActivityResultDto activityResultDto) {
                    handleActivityResult(activityResultDto);
                }
            });
        }

        private void handleActivityResult(ActivityResultDto activityResultDto) {
            // TODO: 06.12.2016 do it in RX
            //сюда должен выдаваться только uri файла, который нужно подставить

            switch (activityResultDto.getRequestCode()) {
                case REQUEST_PROFILE_PHOTO_PICKER:
                    if (activityResultDto.getIntent() != null) {
                        String photoUrl = activityResultDto.getIntent().getData().toString();
                        getView().updateAvatarPhoto(Uri.parse(photoUrl));
                    }
                    break;
                case REQUEST_PROFILE_PHOTO_CAMERA:
                    if (mPhotoFile != null) {
                        getView().updateAvatarPhoto(Uri.fromFile(mPhotoFile));
                    }
                    break;
            }

        }

        private void subscribeOnUserInfoObs() {
            mUserInfoSub = subscribe(mModel.getUserInfoObs(), new ViewSubscriber<UserInfoDto>() {
                @Override
                public void onNext(UserInfoDto userInfoDto) {
                    if (getView() != null) {
                        getView().updateProfileInfo(userInfoDto);
                    }
                }
            });
        }

        @Override
        public void chooseCamera() {
            if (getRootView() != null) {
                String[] permissions = new String[]{CAMERA, WRITE_EXTERNAL_STORAGE};
                if (mRootPresenter.checkPermissionsAndRequestIfNotGranted(permissions,
                        ConstantManager.REQUEST_PERMISSION_CAMERA)) {
                    mPhotoFile = createFileForPhoto();
                    if (mPhotoFile == null) {
                        getRootView().showMessage("Фотография не может быть создана");
                        return;
                    }
                    takePhotoFromCamera();
                }
            }
        }

        private void takePhotoFromCamera() {
            Uri uriForFile = FileProvider.getUriForFile(((RootActivity) getRootView()), ConstantManager.FILE_PROVIDER_AUTHORITY, mPhotoFile);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
            ((RootActivity) getRootView()).startActivityForResult(takePictureIntent, REQUEST_PROFILE_PHOTO_CAMERA);
        }

        private File createFileForPhoto() {
            DateFormat dateTimeInstance = SimpleDateFormat.getTimeInstance(MEDIUM);
            String timeStamp = dateTimeInstance.format(new Date());
            String imageFileName = ConstantManager.PHOTO_FILE_PREFIX + timeStamp;
            File storageDir = getView().getContext().getExternalFilesDir(DIRECTORY_PICTURES);
            File fileImage;
            try {
                fileImage = File.createTempFile(imageFileName, ".jpg", storageDir);
            } catch (IOException e) {
                return null;
            }
            return fileImage;
        }

        @Override
        public void chooseGalery() {
            if (getRootView() != null) {
                String[] permissions = new String[]{READ_EXTERNAL_STORAGE};
                if (mRootPresenter.checkPermissionsAndRequestIfNotGranted(permissions,
                        ConstantManager.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE)) takePhotoFromGallery();
            }
        }

        private void takePhotoFromGallery() {
            Intent intent = new Intent();
            intent.setType("image/*");
            if (Build.VERSION.SDK_INT < 19) {
                intent.setAction(Intent.ACTION_GET_CONTENT);
            } else {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
            }
            ((RootActivity) getRootView()).startActivityForResult(intent, REQUEST_PROFILE_PHOTO_PICKER);
        }

        //endregion

        @Override
        public void clickOnAddress() {
            Flow.get(getView()).set(new AddressScreen(null));
        }

        @Override
        public void switchViewState() {
            Log.e(TAG, "switchViewState: "+getCustomState());
            if (getCustomState() == EDIT_STATE && getView() != null) {
                mModel.saveProfileInfo(getView().getUserProfileInfo());
            }
            if (getView() != null) {
                getView().changeState();
            }
        }

//        @Override
//        public void switchOrder(boolean isChecked) {
//            mAccountModel.saveOrderNotification(isChecked);
//        }
//
//        @Override
//        public void switchPromo(boolean isChecked) {
//            mAccountModel.savePromoNotification(isChecked);
//        }

        @Override
        public void takePhoto() {
            Log.e(TAG, "takePhoto: "+mCustomState + "EDIT STATE - "+ EDIT_STATE);
            if (getView() != null) {
                if (mCustomState == EDIT_STATE) {
                    getView().showPhotoSourceDialog();
                }
            }
        }


//        @Override
//        public void chooseCamera() {
//            if (getRootView() != null) {
//                getRootView().showMessage("chooseCamera");
//                getRootView().pickAvatarFromCamera();
//            }
//            // TODO: 28.11.2016 choose from camera
//        }

//        @Override
//        public void saveAvatarPhoto(Uri avatarUri) {
//            mAccountModel.saveAvatarPhoto(avatarUri);
//        }

//        @Override
//        public List<UserAddressDto> getUserAddressDto() {
//            return mAccountModel.getUserAddressDto();
//        }

        @Override
        public void removeAddress(int position) {
            mModel.removeAddress(mModel.getAddressFromPosition(position));
            updateListView();
        }

        @Override
        public void editAddress(int position) {
            Flow.get(getView()).set(new AddressScreen(mModel.getAddressFromPosition(position)));
        }


//        @Nullable
//        @Override
//        protected IRootView getRootView() {
//            return mRootPresenter.getView();
//        }

        public void switchSettings() {
            if(getView() != null) {
                mModel.saveSettings(getView().getSettings());
            }
        }
    }

    //endregion
}
