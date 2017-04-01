package me.uptop.mvpgoodpractice.data.network.error;

public class NetworkAvailableError extends Throwable {
    public NetworkAvailableError() {
        super("Интернет недоступен, попробуйте позже");
    }
}
