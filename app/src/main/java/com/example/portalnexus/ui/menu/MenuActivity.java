package com.example.portalnexus.ui.menu;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.portalnexus.databinding.ActivityMenuBinding;
import com.example.portalnexus.ui.characters.CharacterListActivity;
import com.example.portalnexus.ui.employees.EmployeeListActivity;
import com.example.portalnexus.ui.home.HomeActivity;
import com.example.portalnexus.utils.DialogHelper;
import com.example.portalnexus.utils.SessionManager;

public class MenuActivity extends AppCompatActivity {

    private ActivityMenuBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
            androidx.core.graphics.Insets insets = windowInsets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, 0);
            binding.btnLogout.setTranslationY(-insets.bottom);
            return windowInsets;
        });

        sessionManager = new SessionManager(this);

        setupListeners();
    }

    private void setupListeners() {
        binding.cardCharacters.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, CharacterListActivity.class));
        });

        binding.cardEmployees.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, EmployeeListActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> {
            DialogHelper.showLogoutConfirmation(this, () -> {
                sessionManager.logout();
                startActivity(new Intent(MenuActivity.this, HomeActivity.class));
                finishAffinity();
            });
        });
    }
}
