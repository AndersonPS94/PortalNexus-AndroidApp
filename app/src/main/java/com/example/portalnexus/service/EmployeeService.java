package com.example.portalnexus.service;

import android.util.Log;

import com.example.portalnexus.data.model.Employee;
import com.example.portalnexus.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmployeeService {

    private static final String TAG = "EmployeeService";
    private final OkHttpClient client;
    private final Gson gson;

    public EmployeeService() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    public void getAll(final EmployeeListCallback callback) {
        Request request = new Request.Builder()
                .url(Constants.BASE_URL + "/api/funcionarios")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "getAll failed: " + e.getMessage());
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "getAll status: " + response.code());
                Log.d(TAG, "getAll raw response: " + responseBody);
                
                if (response.isSuccessful()) {
                    try {
                        Type listType = new TypeToken<List<Employee>>(){}.getType();
                        List<Employee> employees = gson.fromJson(responseBody, listType);
                        callback.onSuccess(employees != null ? employees : new ArrayList<>());
                    } catch (JsonSyntaxException e) {
                        Log.e(TAG, "Parsing error: " + e.getMessage());
                        callback.onError("Erro ao processar dados do servidor");
                    }
                } else {
                    callback.onError("Erro ao buscar funcionários: " + response.code());
                }
            }
        });
    }

    public void add(Employee employee, final ActionCallback callback) {
        String json = gson.toJson(employee);
        Log.d(TAG, "Adding employee JSON: " + json);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(Constants.BASE_URL + "/api/funcionarios")
                .post(body)
                .build();

        executeAction(request, callback, "add");
    }

    public void update(Employee employee, final ActionCallback callback) {
        String json = gson.toJson(employee);
        Log.d(TAG, "Updating employee JSON: " + json);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(Constants.BASE_URL + "/api/funcionarios/" + employee.getId())
                .put(body)
                .build();

        executeAction(request, callback, "update");
    }

    public void delete(int id, final ActionCallback callback) {
        Log.d(TAG, "Deleting employee ID: " + id);
        Request request = new Request.Builder()
                .url(Constants.BASE_URL + "/api/funcionarios/" + id)
                .delete()
                .build();

        executeAction(request, callback, "delete");
    }

    private void executeAction(Request request, final ActionCallback callback, String actionName) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, actionName + " network failure: " + e.getMessage());
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";
                Log.d(TAG, actionName + " response code: " + response.code());
                Log.d(TAG, actionName + " response body: " + responseBody);
                
                if (response.isSuccessful()) {
                    if (actionName.equals("add") || actionName.equals("update")) {
                        try {
                            Employee savedEmployee = gson.fromJson(responseBody, Employee.class);
                            callback.onSuccess(savedEmployee);
                        } catch (Exception e) {
                            callback.onSuccess(null);
                        }
                    } else {
                        callback.onSuccess(null);
                    }
                } else {
                    callback.onError("Erro na operação: " + response.code());
                }
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
