package me.uptop.mvpgoodpractice.data.storage.dto;

public class UserSettingsDto {
    private boolean orderNotification;
    private boolean promoNotification;

    public UserSettingsDto(boolean orderNotification, boolean promoNotification) {
        this.orderNotification = orderNotification;
        this.promoNotification = promoNotification;
    }

    public boolean isPromoNotification() {
        return promoNotification;
    }

    public boolean isOrderNotification() {
        return orderNotification;
    }
}
