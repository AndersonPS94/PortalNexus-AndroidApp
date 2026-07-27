package com.example.portalnexus.viewmodel;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.app.Application;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.portalnexus.data.repository.CharacterRepository;
import com.example.portalnexus.utils.NetworkUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

public class CharacterViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private CharacterViewModel viewModel;
    private MockedStatic<CharacterRepository> mockedRepository;
    private MockedStatic<NetworkUtils> mockedNetworkUtils;
    private MockedStatic<Log> mockedLog;

    @Mock
    private CharacterRepository repository;

    @Mock
    private Application application;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockedRepository = mockStatic(CharacterRepository.class);
        mockedRepository.when(CharacterRepository::getInstance).thenReturn(repository);
        
        mockedNetworkUtils = mockStatic(NetworkUtils.class);
        mockedNetworkUtils.when(() -> NetworkUtils.isNetworkAvailable(any())).thenReturn(true);

        mockedLog = mockStatic(Log.class);

        doAnswer(invocation -> {
            CharacterRepository.RepositoryCallback callback = invocation.getArgument(5);
            callback.onSuccess(null);
            return null;
        }).when(repository).fetchCharacters(anyInt(), anyString(), anyString(), anyString(), anyString(), any());

        viewModel = new CharacterViewModel(application);
    }

    @After
    public void tearDown() {
        if (mockedRepository != null) mockedRepository.close();
        if (mockedNetworkUtils != null) mockedNetworkUtils.close();
        if (mockedLog != null) mockedLog.close();
    }

    @Test
    public void loadCharacters_initialLoad_callsRepository() {
        verify(repository).fetchCharacters(anyInt(), anyString(), anyString(), anyString(), anyString(), any());
    }

    @Test
    public void loadCharacters_reachesMaxPages_stopsLoading() {
        viewModel.loadCharacters(true);
        viewModel.loadCharacters(true);
        viewModel.loadCharacters(true);

        verify(repository, times(3)).fetchCharacters(anyInt(), anyString(), anyString(), anyString(), anyString(), any());
    }

    @Test
    public void applyFilters_resetsPageAndCallsRepository() {
        viewModel.applyFilters("alive", "male", "human", "Rick");
        verify(repository, times(2)).fetchCharacters(anyInt(), anyString(), anyString(), anyString(), anyString(), any());
    }
}
