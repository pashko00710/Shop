package me.uptop.mvpgoodpractice.mvp.presenters;

public interface IAccountPresenter {
    void clickOnAddress();
    void switchViewState();
//    void switchOrder(boolean isCheck);
//    void switchPromo(boolean isCheck);
    void takePhoto();
    void chooseCamera();
    void chooseGalery();
//    void saveAvatarPhoto(Uri avatarUri);
//    List<UserAddressDto> getUserAddressDto();
    void removeAddress(int position);
    void editAddress(int position);
}
