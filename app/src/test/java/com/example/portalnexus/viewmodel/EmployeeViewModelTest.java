package com.example.portalnexus.viewmodel;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import android.app.Application;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.portalnexus.data.model.Employee;
import com.example.portalnexus.data.repository.EmployeeRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

public class EmployeeViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private EmployeeViewModel viewModel;
    private MockedStatic<EmployeeRepository> mockedRepository;
    private MockedStatic<Log> mockedLog;

    @Mock
    private EmployeeRepository repository;

    @Mock
    private Application application;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockedRepository = mockStatic(EmployeeRepository.class);
        mockedRepository.when(EmployeeRepository::getInstance).thenReturn(repository);
        mockedLog = mockStatic(Log.class);

        doAnswer(invocation -> {
            EmployeeRepository.EmployeeListCallback callback = invocation.getArgument(1);
            callback.onSuccess(null);
            return null;
        }).when(repository).getAll(any(), any());

        doAnswer(invocation -> {
            EmployeeRepository.ActionCallback callback = invocation.getArgument(2);
            callback.onSuccess(null);
            return null;
        }).when(repository).add(any(), any(), any());

        viewModel = new EmployeeViewModel(application);
    }

    @After
    public void tearDown() {
        if (mockedRepository != null) mockedRepository.close();
        if (mockedLog != null) mockedLog.close();
    }

    @Test
    public void loadEmployees_callsRepository() {
        viewModel.loadEmployees();
        verify(repository).getAll(any(), any());
    }

    @Test
    public void addEmployee_callsRepository() {
        Employee emp = new Employee(0, "Test", "Dev", "t@t.com", 1000, true, null);
        viewModel.addEmployee(emp);
        verify(repository).add(any(), any(), any());
    }

    @Test
    public void deleteEmployee_callsRepository() {
        viewModel.deleteEmployee(1);
        verify(repository).delete(any(), anyInt(), any());
    }
}
