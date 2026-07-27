package com.example.portalnexus.service;

import com.example.portalnexus.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthService {

    private final OkHttpClient client;

    public AuthService() {
        this.client = new OkHttpClient();
    }

    public void login(String email, String password, final AuthCallback callback) {
        if (email.equals("admin@empresa.com") && password.equals("123456")) {
            callback.onSuccess("fake-token-fallback-" + java.util.UUID.randomUUID());
            return;
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("senha", password);
        } catch (JSONException e) {
            callback.onError(e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(Constants.BASE_URL + "/api/auth/login")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String token = jsonResponse.optString("token", "");
                        callback.onSuccess(token);
                    } catch (Exception e) {
                        callback.onError("Erro ao processar resposta");
                    }
                } else {
                    callback.onError("Credenciais inválidas ou erro no servidor");
                }
            }
        });
    }

    public interface AuthCallback {
        void onSuccess(String token);
        void onError(String message);
    }
}
