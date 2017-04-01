package me.uptop.mvpgoodpractice.data.network.error;

public class ForbiddenApiError extends ApiError {
    public ForbiddenApiError() {
        super("Неверный логин или пароль");
    }
}
