package com.example.portalnexus.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.portalnexus.data.model.Employee;
import com.example.portalnexus.data.repository.EmployeeRepository;

import java.util.List;

public class EmployeeViewModel extends AndroidViewModel {
    private final EmployeeRepository repository;
    
    private final MutableLiveData<List<Employee>> _employees = new MutableLiveData<>(new java.util.ArrayList<>());
    private final MutableLiveData<List<Employee>> _filteredEmployees = new MutableLiveData<>(new java.util.ArrayList<>());
    public LiveData<List<Employee>> employees = _filteredEmployees;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _operationSuccess = new MutableLiveData<>();
    public LiveData<Boolean> operationSuccess = _operationSuccess;

    private String currentQuery = "";

    public EmployeeViewModel(@NonNull Application application) {
        super(application);
        this.repository = EmployeeRepository.getInstance();
    }

    public void loadEmployees() {
        android.util.Log.d("EmployeeViewModel", "loadEmployees called");
        _isLoading.setValue(true);
        repository.getAll(getApplication(), new EmployeeRepository.EmployeeListCallback() {
            @Override
            public void onSuccess(List<Employee> employees) {
                android.util.Log.d("EmployeeViewModel", "onSuccess: received " + (employees != null ? employees.size() : 0) + " employees");
                _isLoading.postValue(false);
                _employees.postValue(employees);
                applyFilter(employees, currentQuery);
            }

            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void filterEmployees(String query) {
        this.currentQuery = query;
        applyFilter(_employees.getValue(), query);
    }

    private void applyFilter(List<Employee> all, String query) {
        if (all == null || all.isEmpty()) {
            _filteredEmployees.postValue(new java.util.ArrayList<>());
            return;
        }

        if (query == null || query.isEmpty()) {
            _filteredEmployees.postValue(all);
            return;
        }

        String lowerQuery = query.toLowerCase().trim();
        java.util.List<Employee> filtered = new java.util.ArrayList<>();
        for (Employee emp : all) {
            if (emp.getName().toLowerCase().contains(lowerQuery) || 
                emp.getPosition().toLowerCase().contains(lowerQuery)) {
                filtered.add(emp);
            }
        }
        _filteredEmployees.postValue(filtered);
    }

    public void addEmployee(Employee employee) {
        _isLoading.setValue(true);
        repository.add(getApplication(), employee, new EmployeeRepository.ActionCallback() {
            @Override
            public void onSuccess(Employee savedEmployee) {
                _isLoading.postValue(false);
                _operationSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void updateEmployee(Employee employee) {
        _isLoading.setValue(true);
        repository.update(getApplication(), employee, new EmployeeRepository.ActionCallback() {
            @Override
            public void onSuccess(Employee savedEmployee) {
                _isLoading.postValue(false);
                _operationSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }

    public void deleteEmployee(int id) {
        _isLoading.setValue(true);
        repository.delete(getApplication(), id, new EmployeeRepository.ActionCallback() {
            @Override
            public void onSuccess(Employee employee) {
                _isLoading.postValue(false);
                _operationSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
    }
    
    public void resetOperationSuccess() {
        _operationSuccess.setValue(null);
    }
}
