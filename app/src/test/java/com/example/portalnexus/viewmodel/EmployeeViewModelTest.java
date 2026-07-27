package com.example.portalnexus.viewmodel;

import static org.junit.Assert.assertEquals;
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
        Employee emp = new Employee(0, "Mock Employee", "Tester", "mock@test.com", 1000, true, null);
        viewModel.addEmployee(emp);
        verify(repository).add(any(), any(), any());
    }

    @Test
    public void deleteEmployee_callsRepository() {
        viewModel.deleteEmployee(1);
        verify(repository).delete(any(), anyInt(), any());
    }

    @Test
    public void filterEmployees_updatesFilteredList() {
        java.util.List<Employee> list = new java.util.ArrayList<>();
        list.add(new Employee(1, "Rick Sanchez", "Scientist", "rick@c137.com", 1000, true, null));
        list.add(new Employee(2, "Morty Smith", "Assistant", "morty@c137.com", 500, true, null));

        doAnswer(invocation -> {
            EmployeeRepository.EmployeeListCallback callback = invocation.getArgument(1);
            callback.onSuccess(list);
            return null;
        }).when(repository).getAll(any(), any());

        viewModel.loadEmployees();
        
        viewModel.filterEmployees("Rick");
        assertEquals(1, viewModel.employees.getValue().size());
        assertEquals("Rick Sanchez", viewModel.employees.getValue().get(0).getName());

        viewModel.filterEmployees("");
        assertEquals(2, viewModel.employees.getValue().size());
    }
}
