package me.uptop.mvpgoodpractice.data.network.req;

public class UserLoginReq {
    private String login;
    private String password;

    public UserLoginReq(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
