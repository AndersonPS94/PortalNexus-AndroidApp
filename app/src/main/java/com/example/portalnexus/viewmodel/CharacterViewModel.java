package com.example.portalnexus.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.portalnexus.data.model.Character;
import com.example.portalnexus.data.model.CharacterResponse;
import com.example.portalnexus.data.repository.CharacterRepository;
import com.example.portalnexus.utils.Constants;
import com.example.portalnexus.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class CharacterViewModel extends AndroidViewModel {

    private final CharacterRepository repository;

    private final MutableLiveData<List<Character>> _characters = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Character>> characters = _characters;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _isEmpty = new MutableLiveData<>(false);
    public LiveData<Boolean> isEmpty = _isEmpty;

    private int currentPage = 1;
    private boolean isLastPage = false;
    
    private String currentStatus = "";
    private String currentGender = "";
    private String currentSpecies = "";
    private String currentName = "";

    public CharacterViewModel(@NonNull Application application) {
        super(application);
        this.repository = CharacterRepository.getInstance();
        if (_characters.getValue() == null || _characters.getValue().isEmpty()) {
            loadCharacters(false);
        }
    }

    public void loadCharacters(boolean loadNext) {
        if (isLoading.getValue() != null && isLoading.getValue()) return;
        if (loadNext && (isLastPage || currentPage >= Constants.MAX_PAGES)) return;

        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            _error.setValue("Sem conexão com a internet");
            return;
        }

        if (loadNext) {
            currentPage++;
        } else {
            currentPage = 1;
            isLastPage = false;
            _isEmpty.setValue(false);
            _error.setValue(null);
        }

        _isLoading.setValue(true);
        repository.fetchCharacters(currentPage, currentStatus, currentGender, currentSpecies, currentName, new CharacterRepository.RepositoryCallback() {
            @Override
            public void onSuccess(CharacterResponse response) {
                _isLoading.postValue(false);
                if (response != null && response.getResults() != null) {
                    List<Character> currentList = _characters.getValue();
                    if (currentList == null || currentPage == 1) {
                        currentList = new ArrayList<>(response.getResults());
                    } else {
                        currentList.addAll(response.getResults());
                    }
                    
                    _characters.postValue(currentList);
                    _isEmpty.postValue(currentList.isEmpty());
                    
                    if (response.getInfo() != null && response.getInfo().getNext() == null) {
                        isLastPage = true;
                    }
                } else {
                    if (currentPage == 1) {
                        _characters.postValue(new ArrayList<>());
                        _isEmpty.postValue(true);
                    }
                }
            }

            @Override
            public void onError(String message) {
                _isLoading.postValue(false);
                if (currentPage == 1) {
                    _error.postValue(message);
                }
            }
        });
    }

    public void applyFilters(String status, String gender, String species, String name) {
        if (status.equals(currentStatus) && gender.equals(currentGender) && 
            species.equals(currentSpecies) && name.equals(currentName)) {
            return;
        }
        
        this.currentStatus = status;
        this.currentGender = gender;
        this.currentSpecies = species;
        this.currentName = name;
        loadCharacters(false);
    }
    
    public void retry() {
        loadCharacters(false);
    }
}
