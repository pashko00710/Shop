package me.uptop.mvpgoodpractice.data.storage.dto;

public class TwDataDto {
    private String accessToken;
    private String userId;

    public TwDataDto(String accessToken, String userId) {
        this.accessToken = accessToken;
        this.userId=userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUserId() {
        return userId;
    }
}
