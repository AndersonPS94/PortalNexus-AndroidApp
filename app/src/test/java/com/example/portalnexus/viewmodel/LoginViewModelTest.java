package com.example.portalnexus.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;

import com.example.portalnexus.data.repository.LoginRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockedStatic;

public class LoginViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private LoginViewModel viewModel;
    private LoginRepository repository;
    private MockedStatic<LoginRepository> mockedRepository;

    @Before
    public void setUp() {
        repository = mock(LoginRepository.class);
        mockedRepository = mockStatic(LoginRepository.class);
        mockedRepository.when(LoginRepository::getInstance).thenReturn(repository);
        
        viewModel = new LoginViewModel();
    }

    @After
    public void tearDown() {
        if (mockedRepository != null) {
            mockedRepository.close();
        }
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
    public void login_validCredentials_startsLoading() {
        viewModel.login("mock@empresa.com", "123456");
        assertTrue(viewModel.isLoading.getValue());
    }
}
