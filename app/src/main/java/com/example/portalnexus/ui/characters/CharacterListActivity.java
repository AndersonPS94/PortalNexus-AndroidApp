package com.example.portalnexus.ui.characters;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portalnexus.R;
import com.example.portalnexus.adapter.CharacterAdapter;
import com.example.portalnexus.databinding.ActivityCharacterListBinding;
import com.example.portalnexus.databinding.BottomSheetFiltersBinding;
import com.example.portalnexus.ui.profile.ProfileActivity;
import com.example.portalnexus.viewmodel.CharacterViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class CharacterListActivity extends AppCompatActivity {

    private ActivityCharacterListBinding binding;
    private CharacterViewModel viewModel;
    private CharacterAdapter adapter;
    private String currentSearchName = "";
    private String currentStatusFilter = "";
    private String currentGenderFilter = "";
    private String currentSpeciesFilter = "";
    
    private Parcelable recyclerViewState;
    
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityCharacterListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, 0);
            
            binding.btnOpenFilters.setTranslationY(-insets.bottom);
            binding.fabScrollToTop.setTranslationY(-insets.bottom);
            
            return windowInsets;
        });

        setSupportActionBar(binding.toolbar);

        viewModel = new ViewModelProvider(this).get(CharacterViewModel.class);
        
        setupRecyclerView();
        setupObservers();
        setupListeners();
    }

    private void setupRecyclerView() {
        adapter = new CharacterAdapter((character, sharedView) -> {
            Intent intent = new Intent(CharacterListActivity.this, ProfileActivity.class);
            intent.putExtra("character", character);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, sharedView, "hero_image");
            startActivity(intent, options.toBundle());
        });

        binding.rvCharacters.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCharacters.setAdapter(adapter);

        binding.rvCharacters.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int firstVisible = layoutManager.findFirstVisibleItemPosition();
                    binding.fabScrollToTop.setVisibility(firstVisible > 5 ? View.VISIBLE : View.GONE);
                    
                    if (layoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1) {
                        viewModel.loadCharacters(true);
                    }
                }
            }
        });
    }

    private void setupObservers() {
        viewModel.characters.observe(this, characters -> {
            adapter.submitList(characters, () -> {
                if (recyclerViewState != null) {
                    binding.rvCharacters.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                    recyclerViewState = null;
                }
            });
            binding.swipeRefresh.setRefreshing(false);
        });

        viewModel.isLoading.observe(this, isLoading -> {
            if (!binding.swipeRefresh.isRefreshing()) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.error.observe(this, error -> {
            binding.swipeRefresh.setRefreshing(false);
            if (error != null) {
                binding.errorView.setVisibility(View.VISIBLE);
                binding.rvCharacters.setVisibility(View.GONE);
                View retryBtn = binding.errorView.findViewById(R.id.btnRetry);
                if (retryBtn != null) retryBtn.setOnClickListener(v -> viewModel.retry());
            } else {
                binding.errorView.setVisibility(View.GONE);
                binding.rvCharacters.setVisibility(View.VISIBLE);
            }
        });

        viewModel.isEmpty.observe(this, isEmpty -> {
            binding.emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        });
    }

    private void setupListeners() {
        binding.chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> syncAndApplyFilters());
        binding.chipGroupGender.setOnCheckedStateChangeListener((group, checkedIds) -> syncAndApplyFilters());
        binding.chipGroupSpecies.setOnCheckedStateChangeListener((group, checkedIds) -> syncAndApplyFilters());
        
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.loadCharacters(false));
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.fabScrollToTop.setOnClickListener(v -> binding.rvCharacters.smoothScrollToPosition(0));
        binding.btnOpenFilters.setOnClickListener(v -> showFiltersBottomSheet());
    }

    private void syncAndApplyFilters() {
        int statusId = binding.chipGroupStatus.getCheckedChipId();
        if (statusId == R.id.chipAlive) currentStatusFilter = "alive";
        else if (statusId == R.id.chipDead) currentStatusFilter = "dead";
        else currentStatusFilter = "";

        int genderId = binding.chipGroupGender.getCheckedChipId();
        if (genderId == R.id.chipMale) currentGenderFilter = "male";
        else if (genderId == R.id.chipFemale) currentGenderFilter = "female";
        else currentGenderFilter = "";

        int speciesId = binding.chipGroupSpecies.getCheckedChipId();
        if (speciesId == R.id.chipHuman) currentSpeciesFilter = "human";
        else if (speciesId == R.id.chipAlien) currentSpeciesFilter = "alien";
        else currentSpeciesFilter = "";

        viewModel.applyFilters(currentStatusFilter, currentGenderFilter, currentSpeciesFilter, currentSearchName);
    }

    private void showFiltersBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        BottomSheetFiltersBinding bsBinding = BottomSheetFiltersBinding.inflate(getLayoutInflater());
        dialog.setContentView(bsBinding.getRoot());

        if (currentStatusFilter.equals("alive")) bsBinding.bsChipAlive.setChecked(true);
        else if (currentStatusFilter.equals("dead")) bsBinding.bsChipDead.setChecked(true);
        else bsBinding.bsChipStatusAll.setChecked(true);

        if (currentGenderFilter.equals("male")) bsBinding.bsChipMale.setChecked(true);
        else if (currentGenderFilter.equals("female")) bsBinding.bsChipFemale.setChecked(true);
        else bsBinding.bsChipGenderAll.setChecked(true);

        if (currentSpeciesFilter.equals("human")) bsBinding.bsChipHuman.setChecked(true);
        else if (currentSpeciesFilter.equals("alien")) bsBinding.bsChipAlien.setChecked(true);
        else bsBinding.bsChipSpeciesAll.setChecked(true);

        bsBinding.btnApplyFilters.setOnClickListener(v -> {
            int sId = bsBinding.bsChipGroupStatus.getCheckedChipId();
            if (sId == R.id.bsChipAlive) binding.chipAlive.setChecked(true);
            else if (sId == R.id.bsChipDead) binding.chipDead.setChecked(true);
            else binding.chipAll.setChecked(true);

            int gId = bsBinding.bsChipGroupGender.getCheckedChipId();
            if (gId == R.id.bsChipMale) binding.chipMale.setChecked(true);
            else if (gId == R.id.bsChipFemale) binding.chipFemale.setChecked(true);
            else binding.chipGenderAll.setChecked(true);

            int spId = bsBinding.bsChipGroupSpecies.getCheckedChipId();
            if (spId == R.id.bsChipHuman) binding.chipHuman.setChecked(true);
            else if (spId == R.id.bsChipAlien) binding.chipAlien.setChecked(true);
            else binding.chipSpeciesAll.setChecked(true);
            
            syncAndApplyFilters();
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (binding.rvCharacters.getLayoutManager() != null) {
            outState.putParcelable("recycler_state", binding.rvCharacters.getLayoutManager().onSaveInstanceState());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recyclerViewState = savedInstanceState.getParcelable("recycler_state");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.character_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        
        if (searchView != null) {
            searchView.setQueryHint(getString(R.string.search_hint));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    currentSearchName = query;
                    syncAndApplyFilters();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                    
                    searchRunnable = () -> {
                        currentSearchName = newText;
                        syncAndApplyFilters();
                    };
                    searchHandler.postDelayed(searchRunnable, 500);
                    return true;
                }
            });
        }
        return true;
    }
}
