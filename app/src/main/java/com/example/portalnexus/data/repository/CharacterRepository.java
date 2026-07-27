package com.example.portalnexus.data.repository;

import com.example.portalnexus.data.model.Character;
import com.example.portalnexus.data.model.CharacterResponse;
import com.example.portalnexus.service.CharacterService;

import java.util.HashMap;
import java.util.Map;

public class CharacterRepository {

    private static CharacterRepository instance;
    private final CharacterService characterService;
    
    private static final Map<Integer, String> capturedPhotosCache = new HashMap<>();

    private CharacterRepository() {
        this.characterService = new CharacterService();
    }

    public static synchronized CharacterRepository getInstance() {
        if (instance == null) {
            instance = new CharacterRepository();
        }
        return instance;
    }

    public void fetchCharacters(int page, String status, String gender, String species, String name, final RepositoryCallback callback) {
        characterService.getCharacters(page, status, gender, species, name, new CharacterService.CharacterCallback() {
            @Override
            public void onSuccess(CharacterResponse response) {
                if (response != null && response.getResults() != null) {
                    for (Character character : response.getResults()) {
                        if (capturedPhotosCache.containsKey(character.getId())) {
                            character.setImage(capturedPhotosCache.get(character.getId()));
                        }
                    }
                }
                callback.onSuccess(response);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void saveCapturedPhoto(int characterId, String uri) {
        capturedPhotosCache.put(characterId, uri);
    }

    public void simulatePost(Character character, String uri, final PostCallback callback) {
        characterService.simulatePost(character, uri, new CharacterService.PostCallback() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public interface RepositoryCallback {
        void onSuccess(CharacterResponse response);
        void onError(String message);
    }

    public interface PostCallback {
        void onSuccess(String result);
        void onError(String message);
    }
}
