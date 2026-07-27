package com.example.portalnexus.data.repository;

import com.example.portalnexus.service.AuthService;

public class LoginRepository {

    private static LoginRepository instance;
    private final AuthService authService;

    private LoginRepository() {
        this.authService = new AuthService();
    }

    public static synchronized LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
        }
        return instance;
    }

    public void login(String email, String password, final LoginCallback callback) {
        authService.login(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(String token) {
                callback.onSuccess(token);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public interface LoginCallback {
        void onSuccess(String token);
        void onError(String message);
    }
}
