package com.example.portalnexus.data.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.portalnexus.data.model.Employee;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class EmployeeRepositoryTest {

    private EmployeeRepository repository;

    @Before
    public void setUp() {
        // Como o repositório é Singleton, precisamos garantir um estado limpo se possível,
        // mas aqui vamos apenas testar o comportamento básico.
        repository = EmployeeRepository.getInstance();
    }

    @Test
    public void getAll_returnsInitialData() {
        List<Employee> employees = repository.getAll();
        assertNotNull(employees);
        assertTrue(employees.size() >= 2); // João e Maria definidos no static block
    }

    @Test
    public void add_increasesListSize() {
        int initialSize = repository.getAll().size();
        Employee newEmployee = new Employee(0, "Test", "Dev", "test@test.com", 1000.0, true);
        
        repository.add(newEmployee);
        
        assertEquals(initialSize + 1, repository.getAll().size());
    }

    @Test
    public void delete_removesEmployee() {
        Employee newEmployee = new Employee(0, "Delete Me", "Dev", "del@test.com", 1000.0, true);
        repository.add(newEmployee);
        
        List<Employee> list = repository.getAll();
        int idToDelete = -1;
        for (Employee e : list) {
            if (e.getName().equals("Delete Me")) {
                idToDelete = e.getId();
                break;
            }
        }
        
        assertTrue(idToDelete != -1);
        repository.delete(idToDelete);
        
        boolean found = false;
        for (Employee e : repository.getAll()) {
            if (e.getId() == idToDelete) {
                found = true;
                break;
            }
        }
        assertFalse("Employee should have been deleted", found);
    }
}
