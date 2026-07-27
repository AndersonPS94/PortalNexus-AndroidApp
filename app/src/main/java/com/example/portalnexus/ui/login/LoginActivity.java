package com.example.portalnexus.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.portalnexus.R;
import com.example.portalnexus.databinding.ActivityLoginBinding;
import com.example.portalnexus.ui.menu.MenuActivity;
import com.example.portalnexus.utils.SessionManager;
import com.example.portalnexus.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
            androidx.core.graphics.Insets insets = windowInsets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, 0);
            binding.cardLogin.setTranslationY(-insets.bottom / 2f);
            return windowInsets;
        });

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        sessionManager = new SessionManager(this);

        setupObservers();
        setupListeners();
        setupTextWatchers();
    }

    private void setupObservers() {
        viewModel.isLoading.observe(this, isLoading -> {
            binding.loading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnLogin.setEnabled(!isLoading);
        });

        viewModel.errorMessage.observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.loginSuccess.observe(this, token -> {
            if (token != null && !token.isEmpty()) {
                boolean keepLoggedIn = binding.checkKeepLoggedIn.isChecked();
                sessionManager.saveSession(token, "Usuário Backend", binding.editEmail.getText().toString(), keepLoggedIn);
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                finishAffinity();
            }
        });
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            if (validate()) {
                String email = binding.editEmail.getText().toString().trim();
                String password = binding.editPassword.getText().toString().trim();
                viewModel.login(email, password);
            }
        });
    }

    private void setupTextWatchers() {
        binding.editEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.inputEmail.setError(null);
            }
        });

        binding.editPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.inputPassword.setError(null);
            }
        });
    }

    private boolean validate() {
        boolean isValid = true;

        String email = binding.editEmail.getText().toString().trim();
        if (email.isEmpty()) {
            binding.inputEmail.setError(getString(R.string.email_required));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.setError(getString(R.string.invalid_email));
            isValid = false;
        } else {
            binding.inputEmail.setError(null);
        }

        String password = binding.editPassword.getText().toString().trim();
        if (password.isEmpty()) {
            binding.inputPassword.setError(getString(R.string.fill_all_fields));
            isValid = false;
        } else if (password.length() < 6) {
            binding.inputPassword.setError(getString(R.string.invalid_password));
            isValid = false;
        } else {
            binding.inputPassword.setError(null);
        }

        return isValid;
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}
