package com.example.portalnexus.data.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.util.Log;

import com.example.portalnexus.data.model.Employee;
import com.example.portalnexus.service.EmployeeService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

public class EmployeeRepositoryTest {

    private EmployeeRepository repository;
    private MockedStatic<Log> mockedLog;

    @Mock
    private Context mockContext;

    @Mock
    private EmployeeService mockService;

    @Mock
    private EmployeeRepository.EmployeeListCallback mockListCallback;

    @Mock
    private EmployeeRepository.ActionCallback mockActionCallback;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockedLog = mockStatic(Log.class);
        repository = new EmployeeRepository(mockService);
    }

    @After
    public void tearDown() {
        if (mockedLog != null) {
            mockedLog.close();
        }
    }

    @Test
    public void getAll_callsService() {
        repository.getAll(mockContext, mockListCallback);
        verify(mockService).getAll(any());
    }

    @Test
    public void add_callsService() {
        Employee newEmployee = new Employee(0, "Mock Employee", "Tester", "mock@test.com", 1000.0, true, null);
        repository.add(mockContext, newEmployee, mockActionCallback);
        verify(mockService).add(any(), any());
    }

    @Test
    public void delete_callsService() {
        repository.delete(mockContext, 1, mockActionCallback);
        verify(mockService).delete(anyInt(), any());
    }
}
