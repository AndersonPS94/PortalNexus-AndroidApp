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
    
    private final MutableLiveData<List<Employee>> _employees = new MutableLiveData<>();
    public LiveData<List<Employee>> employees = _employees;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _operationSuccess = new MutableLiveData<>();
    public LiveData<Boolean> operationSuccess = _operationSuccess;

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
                _isLoading.postValue(false);
                _employees.postValue(employees);
            }

            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                _error.postValue(message);
            }
        });
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
