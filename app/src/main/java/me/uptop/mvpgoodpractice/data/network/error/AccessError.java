package me.uptop.mvpgoodpractice.data.network.error;

public class AccessError extends Exception  {
    public AccessError() {
        super("Неверный логин или пароль");
    }
}
