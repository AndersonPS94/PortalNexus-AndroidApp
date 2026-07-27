package com.example.portalnexus.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.portalnexus.data.repository.LoginRepository;

public class LoginViewModel extends ViewModel {

    private final LoginRepository repository;
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    private final MutableLiveData<String> _loginSuccess = new MutableLiveData<>();
    public LiveData<String> loginSuccess = _loginSuccess;

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

    public LoginViewModel() {
        this.repository = LoginRepository.getInstance();
    }

    public void login(String email, String password) {
        if (email == null || email.isEmpty() || !email.matches(EMAIL_PATTERN)) {
            _errorMessage.setValue("Email inválido");
            return;
        }

        if (password == null || password.length() < 6) {
            _errorMessage.setValue("A senha deve ter pelo menos 6 caracteres");
            return;
        }

        _isLoading.setValue(true);

        repository.login(email, password, new LoginRepository.LoginCallback() {
            @Override
            public void onSuccess(String token) {
                _isLoading.postValue(false);
                _loginSuccess.postValue(token);
            }

            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _errorMessage.postValue(message);
            }
        });
    }
}
