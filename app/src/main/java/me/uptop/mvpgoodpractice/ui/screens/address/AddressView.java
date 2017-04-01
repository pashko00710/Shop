package me.uptop.mvpgoodpractice.ui.screens.address;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.data.storage.dto.UserAddressDto;
import me.uptop.mvpgoodpractice.di.DaggerService;
import me.uptop.mvpgoodpractice.mvp.views.AbstractView;
import me.uptop.mvpgoodpractice.mvp.views.IAddressView;

public class AddressView extends AbstractView<AddressScreen.AddressPresenter> implements IAddressView {
    @BindView(R.id.delivery_location)
    TextInputEditText addressNameEt;
    @BindView(R.id.street_name)
    TextInputEditText streetName;
    @BindView(R.id.house_number)
    TextInputEditText houseNumber;
    @BindView(R.id.apartment_number)
    TextInputEditText appartamentNumber;
    @BindView(R.id.floor_number)
    TextInputEditText floorNumber;
    @BindView(R.id.order_comment)
    TextInputEditText orderComment;
    @BindView(R.id.add_button)
    Button addButton;

    @Inject
    AddressScreen.AddressPresenter mPresenter;

    private int mAddressId;

    public AddressView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        if (!isInEditMode()) {
//            DaggerService.<AddressScreen.Component>getDaggerComponent(context).inject(this);
//        }
    }

    @Override
    protected void initDagger(Context context) {
        DaggerService.<AddressScreen.Component>getDaggerComponent(context).inject(this);
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
        if(!isInEditMode()) {
        }
    }

//region ===================== IAddressView =====================

    public void initView(@Nullable UserAddressDto address) {
        if(address != null) {
            mAddressId = address.getId();
            addressNameEt.setText(address.getName());
            streetName.setText(address.getStreet());
            houseNumber.setText(address.getHouse());
            appartamentNumber.setText(address.getAppartament());
            floorNumber.setText(Integer.toString(address.getFloor()));
            orderComment.setText(address.getComment());
            addButton.setText("Сохранить");
        }
    }

    @Override
    public void showInputError() {
        // TODO: 29.11.2016  implement this
    }

    @Override
    public UserAddressDto getUserAddress() {
        return new UserAddressDto(mAddressId,
                addressNameEt.getText().toString(),
                streetName.getText().toString(),
                houseNumber.getText().toString(),
                appartamentNumber.getText().toString(),
                Integer.parseInt(floorNumber.getText().toString()),
                orderComment.getText().toString());
    }

    @Override
    public void goBackToParentScreen() {
        mPresenter.goBackToParentScreen();
    }

    @Override
    public boolean viewOnBackPressed() {
        return false;
    }

    //endregion

    //region ===================== Events =====================

    @OnClick(R.id.add_button)
    void addAddress() {
        mPresenter.clickOnAddAddress();
    }

    //endregion
}
