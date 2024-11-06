package com.engdiarytoon.server.user;

public class KakaoUser {
    private String email;
    private String nickname;

    public KakaoUser(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }
}
