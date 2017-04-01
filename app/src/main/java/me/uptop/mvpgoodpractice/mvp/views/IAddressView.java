package me.uptop.mvpgoodpractice.mvp.views;

import me.uptop.mvpgoodpractice.data.storage.dto.UserAddressDto;

public interface IAddressView extends IView {
    void showInputError();

    UserAddressDto getUserAddress();
    void goBackToParentScreen();
}
