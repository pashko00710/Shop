package me.uptop.mvpgoodpractice.ui.screens.account;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import flow.Flow;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.dto.UserDto;
import me.uptop.mvpgoodpractice.data.storage.dto.UserInfoDto;
import me.uptop.mvpgoodpractice.data.storage.dto.UserSettingsDto;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.mvp.views.AbstractView;
import me.uptop.mvpgoodpractice.mvp.views.IAccountView;
import me.uptop.mvpgoodpractice.ui.custom_views.CustomScrollView;
import me.uptop.mvpgoodpractice.ui.screens.address.AddressAdapter;
import me.uptop.mvpgoodpractice.utils.CircularTransformation;
import me.uptop.mvpgoodpractice.utils.ItemSwipeCallback;

public class AccountView extends AbstractView<AccountScreen.AccountPresenter> implements IAccountView {
    public static final String TAG = "AccountView";
    public static final int PREVIEW_STATE = 1;
    public static final int EDIT_STATE = 0;
    private TextWatcher mWatcher;
//    private AddressAdapter addressAdapter;
    private boolean mScrollable = true;
    @Inject
    AccountScreen.AccountPresenter mPresenter;
    @Inject
    Picasso mPicasso;

    @BindView(R.id.user_avatar)
    ImageView userAvatarImage;
    @BindView(R.id.main_phone)
    EditText userPhone;
    @BindView(R.id.profile_name_txt)
    TextView profileNameTxt;
    @BindView(R.id.user_full_name_et)
    EditText userFullNameEt;
    @BindView(R.id.profile_name_wrapper)
    LinearLayout profileNameWrapper;
    @BindView(R.id.add_address_btn)
    Button addAddressBtn;
    @BindView(R.id.address_list)
    RecyclerView addressList;
    @BindView(R.id.notification_order_sw)
    SwitchCompat notificationOrderSw;
    @BindView(R.id.notification_promo_sw)
    SwitchCompat notificationPromoSw;
    @BindView(R.id.account_nestedscroll)
    CustomScrollView accountNestedScroll;

    private AccountScreen mAccountScreen;
    private UserDto mUserDto;
    private AddressAdapter mAdapter;
    private  Uri mAvatarUri;

    public AddressAdapter getAdapter() {
        return mAdapter;
    }

    public AccountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            mAccountScreen = Flow.getKey(this);
        }
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<AccountScreen.Component>getDaggerComponent(context).inject(this);
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(!isInEditMode()) {
            mPresenter.takeView(this);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(!isInEditMode()) {
            mPresenter.dropView(this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void initView() {
        showViewFromState();
        Log.e(TAG, "initView: ");
        mAdapter = new AddressAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        addressList.setLayoutManager(layoutManager);
        addressList.setAdapter(mAdapter);
        initSwipe();
    }




    public void initSettings(UserSettingsDto userSettingsDto) {
        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> mPresenter.switchSettings();
        notificationOrderSw.setChecked(userSettingsDto.isOrderNotification());
        notificationOrderSw.setOnCheckedChangeListener(listener);
        notificationPromoSw.setChecked(userSettingsDto.isPromoNotification());
        notificationPromoSw.setOnCheckedChangeListener(listener);
    }

    private void initSwipe() {
        ItemSwipeCallback swipeCallback = new ItemSwipeCallback(getContext(), 0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    showRemoveAddressDialog(position);
                } else {
                    showEditAddressDialog(position);
                    // TODO: 28.11.2016 Implement action on swipe to right for edit item
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(addressList);
    }

    private void showRemoveAddressDialog(final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle(R.string.account_removing)
                .setMessage(R.string.account_remove_address_confirm)
                .setPositiveButton(R.string.account_yes, (dialogInterface, i) -> mPresenter.removeAddress(position))
                .setNegativeButton(R.string.account_cancel, (dialogInterface, i) -> dialogInterface.cancel())
                .setOnCancelListener(dialogInterface -> mAdapter.notifyDataSetChanged())
                .show();
    }

    private void showEditAddressDialog(int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle(R.string.account_editing)
//                .setView(R.layout.dialog_edit_address)
                .setPositiveButton(R.string.account_ok, (dialogInterface, i) -> mPresenter.editAddress(position))
                .setNegativeButton(R.string.account_cancel, (dialogInterface, i) -> dialogInterface.cancel())
                .setOnCancelListener(dialogInterface -> mAdapter.notifyDataSetChanged())
                .show();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mScrollable) return false;
        else return super.onInterceptTouchEvent(ev);
    }

    private void showViewFromState() {
        Log.e(TAG, "showViewFromState: "+mPresenter.getCustomState());
        if(mPresenter.getCustomState() == PREVIEW_STATE) {
            showPreviewState();
        } else {
            Log.e(TAG, "showViewFromState: edit");
            showEditState();
        }
    }

    //region =========================== IAccountView =====================
    @Override
    public void changeState() {
        Log.e(TAG, "changeState: "+ mPresenter.getCustomState());
        if(mPresenter.getCustomState() == PREVIEW_STATE) {
            mPresenter.setCustomState(EDIT_STATE);
        } else {
            mPresenter.setCustomState(PREVIEW_STATE);
        }
        showViewFromState();
    }

    @Override
    public void showEditState() {
        profileNameWrapper.setVisibility(VISIBLE);
        mWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                profileNameTxt.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        profileNameWrapper.setVisibility(VISIBLE);
        userFullNameEt.addTextChangedListener(mWatcher);
        userPhone.setEnabled(true);
        mPicasso.load(R.drawable.ic_add_white_24dp)
                .error(R.drawable.ic_add_white_24dp)
                .into(userAvatarImage);
    }

    @Override
    public void showPreviewState() {
        profileNameWrapper.setVisibility(GONE);
        userPhone.setEnabled(false);
        userFullNameEt.removeTextChangedListener(mWatcher);
        Log.e(TAG, "showPreviewState: "+mAvatarUri);
        if (mAvatarUri != null) {
            insertAvatar();
        }
    }

    @Override
    public void showPhotoSourceDialog() {
        String source[] = {"Загрузить из галлереи", "Сделать фото", "Отмена"};
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Установить фото");
        alertDialog.setItems(source, (dialog, which) -> {
            switch (which) {
                case 0:
                    mPresenter.chooseGalery();
                    break;
                case 1:
                    mPresenter.chooseCamera();
                    break;
                case 2:
                    dialog.cancel();
                    break;
            }
        });
        alertDialog.show();
    }

    @Override
    public String getUserName() {
        return String.valueOf(userFullNameEt.getText());
    }

    @Override
    public String getUserPhone() {
        return String.valueOf(userPhone.getText());
    }

    @Override
    public boolean viewOnBackPressed() {
        if(mPresenter.getCustomState() == EDIT_STATE) {
            changeState();
            return true;
        } else {
            return false;
        }
    }

    public void updateAvatarPhoto(Uri uri) {
        mAvatarUri = uri;

        insertAvatar();
    }

    private void insertAvatar() {
        Log.e(TAG, "insertAvatar: "+mAvatarUri);
        mPicasso.load(mAvatarUri)
                .resize(140, 140)
                .transform(new CircularTransformation())
                .centerCrop()
                .into(userAvatarImage);
    }

    public UserSettingsDto getSettings() {
        return new UserSettingsDto(notificationOrderSw.isChecked(), notificationPromoSw.isChecked());
    }

    public UserInfoDto getUserProfileInfo() {
        Log.e(TAG, "getUserProfileInfo: "+mAvatarUri+userFullNameEt.getText()+"  "+ userPhone.getText());
        return new UserInfoDto(userFullNameEt.getText().toString(), userPhone.getText().toString(),
                String.valueOf(mAvatarUri));
    }

    public void updateProfileInfo(UserInfoDto userInfoDto) {
        Log.e(TAG, "updateProfileInfo: "+userInfoDto.getName());
        profileNameTxt.setText(userInfoDto.getName());
        userFullNameEt.setText(userInfoDto.getName());
        userPhone.setText(userInfoDto.getPhone());
        if (mPresenter.getCustomState() == PREVIEW_STATE) {
            mAvatarUri = Uri.parse(userInfoDto.getAvatar());
            insertAvatar();
        }
    }

    //endregion


    //region =========================== Events =====================
    @OnClick(R.id.collapsing_toolbar)
    void testEditMode() {
        mPresenter.switchViewState();
    }

    @OnClick(R.id.add_address_btn)
    void addAddress() {
        mPresenter.clickOnAddress();
    }

    @OnClick(R.id.user_avatar)
    void clickUserAvatar() {
        if(mPresenter.getCustomState() == EDIT_STATE) mPresenter.takePhoto();
    }

    //endregion
}
