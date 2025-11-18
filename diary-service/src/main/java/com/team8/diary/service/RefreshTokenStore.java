package com.team8.diary.service;

public interface RefreshTokenStore {

    void save(String email, String refreshToken);
    boolean isSame(String email, String refreshToken);
    void delete(String email);
    void blacklistAccess(String accessToken);

}
