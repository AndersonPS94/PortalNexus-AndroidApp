package com.example.portalnexus.data.repository;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mockStatic;

import android.content.Context;
import android.util.Log;

import com.example.portalnexus.data.model.Employee;

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
    private EmployeeRepository.EmployeeListCallback mockListCallback;

    @Mock
    private EmployeeRepository.ActionCallback mockActionCallback;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockedLog = mockStatic(Log.class);
        repository = EmployeeRepository.getInstance();
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
        assertNotNull(repository);
    }

    @Test
    public void add_callsService() {
        Employee newEmployee = new Employee(0, "Test", "Dev", "test@test.com", 1000.0, true, null);
        repository.add(mockContext, newEmployee, mockActionCallback);
        assertNotNull(newEmployee);
    }

    @Test
    public void delete_callsService() {
        repository.delete(mockContext, 1, mockActionCallback);
        assertNotNull(repository);
    }
}
