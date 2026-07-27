package com.example.portalnexus.service;

import com.example.portalnexus.data.model.Character;
import com.example.portalnexus.data.model.CharacterResponse;
import com.example.portalnexus.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CharacterService {

    private final OkHttpClient client;
    private final Gson gson;

    public CharacterService() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    public void getCharacters(int page, String status, String gender, String species, String name, final CharacterCallback callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.RICK_AND_MORTY_BASE_URL + "character").newBuilder();
        urlBuilder.addQueryParameter("page", String.valueOf(page));
        
        if (status != null && !status.isEmpty()) urlBuilder.addQueryParameter("status", status);
        if (gender != null && !gender.isEmpty()) urlBuilder.addQueryParameter("gender", gender);
        if (species != null && !species.isEmpty()) urlBuilder.addQueryParameter("species", species);
        if (name != null && !name.isEmpty()) urlBuilder.addQueryParameter("name", name);

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Erro: " + response.code());
                    return;
                }

                String jsonData = response.body().string();
                CharacterResponse characterResponse = gson.fromJson(jsonData, CharacterResponse.class);
                callback.onSuccess(characterResponse);
            }
        });
    }

    public void simulatePost(Character character, String uri, final PostCallback callback) {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("characterId", character.getId());
        jsonBody.addProperty("characterName", character.getName());
        jsonBody.addProperty("capturedImageUri", uri);
        jsonBody.addProperty("capturedAt", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        jsonBody.addProperty("source", "camera");

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(Constants.JSON_PLACEHOLDER_BASE_URL + "posts")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body() != null ? response.body().string() : "";
                    if (callback != null) callback.onSuccess(result);
                } else {
                    if (callback != null) callback.onError("Erro: " + response.code());
                }
            }
        });
    }

    public interface CharacterCallback {
        void onSuccess(CharacterResponse response);
        void onError(String message);
    }

    public interface PostCallback {
        void onSuccess(String result);
        void onError(String message);
    }
}
