package com.team8.diary.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryRefreshTokenStore implements RefreshTokenStore {
    private final Map<String, String> refreshMap = new ConcurrentHashMap<>();

    @Override
    public void save(String email, String refreshToken) {
        refreshMap.put(email, refreshToken);
    }

    @Override
    public boolean isSame(String email, String refreshToken) {
        return refreshToken.equals(refreshMap.get(email));
    }

    @Override
    public void delete(String email) {
        refreshMap.remove(email);
    }

    @Override
    public void blacklistAccess(String accessToken) {

    }

}
