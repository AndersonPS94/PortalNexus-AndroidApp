package com.example.portalnexus.data.repository;

import android.content.Context;
import com.example.portalnexus.data.model.Employee;
import com.example.portalnexus.service.EmployeeService;
import com.example.portalnexus.utils.PhotoStorageHelper;

import java.util.List;

public class EmployeeRepository {
    private static EmployeeRepository instance;
    private final EmployeeService employeeService;

    private EmployeeRepository() {
        this.employeeService = new EmployeeService();
    }

    public static synchronized EmployeeRepository getInstance() {
        if (instance == null) {
            instance = new EmployeeRepository();
        }
        return instance;
    }

    public void getAll(Context context, final EmployeeListCallback callback) {
        employeeService.getAll(new EmployeeService.EmployeeListCallback() {
            @Override
            public void onSuccess(List<Employee> employees) {
                if (employees != null) {
                    for (Employee emp : employees) {
                        String localPath = PhotoStorageHelper.getLocalPhotoPath(context, emp.getId());
                        emp.setPhoto(localPath);
                    }
                }
                callback.onSuccess(employees);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void add(Context context, Employee employee, final ActionCallback callback) {
        final String tempPhotoUri = employee.getPhoto();
        employeeService.add(employee, new EmployeeService.ActionCallback() {
            @Override
            public void onSuccess(Employee savedEmployee) {
                if (savedEmployee != null && tempPhotoUri != null) {
                    String finalPath = PhotoStorageHelper.savePhotoLocally(context, tempPhotoUri, savedEmployee.getId());
                    savedEmployee.setPhoto(finalPath);
                }
                callback.onSuccess(savedEmployee);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void update(Context context, Employee employee, final ActionCallback callback) {
        final String photoUri = employee.getPhoto();
        employeeService.update(employee, new EmployeeService.ActionCallback() {
            @Override
            public void onSuccess(Employee savedEmployee) {
                if (savedEmployee != null && photoUri != null && photoUri.startsWith("content://")) {
                    String finalPath = PhotoStorageHelper.savePhotoLocally(context, photoUri, savedEmployee.getId());
                    savedEmployee.setPhoto(finalPath);
                }
                callback.onSuccess(savedEmployee);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void delete(Context context, int id, final ActionCallback callback) {
        employeeService.delete(id, new EmployeeService.ActionCallback() {
            @Override
            public void onSuccess(Employee employee) {
                PhotoStorageHelper.deletePhotoLocally(context, id);
                callback.onSuccess(null);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public interface EmployeeListCallback {
        void onSuccess(List<Employee> employees);
        void onError(String message);
    }

    public interface ActionCallback {
        void onSuccess(Employee employee);
        void onError(String message);
    }
}
