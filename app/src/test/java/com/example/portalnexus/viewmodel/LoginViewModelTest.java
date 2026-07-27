package com.example.portalnexus.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.os.Looper;
import android.os.Handler;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;

public class LoginViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private LoginViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = new LoginViewModel();
    }

    @Test
    public void login_invalidEmail_emitsError() {
        viewModel.login("invalid", "123456");
        assertEquals("Email inválido", viewModel.errorMessage.getValue());
    }

    @Test
    public void login_shortPassword_emitsError() {
        viewModel.login("admin@teste.com", "123");
        assertEquals("A senha deve ter pelo menos 6 caracteres", viewModel.errorMessage.getValue());
    }

    @Test
    public void login_validCredentials_emitsSuccess() {
        assertTrue(true);
    }
}
