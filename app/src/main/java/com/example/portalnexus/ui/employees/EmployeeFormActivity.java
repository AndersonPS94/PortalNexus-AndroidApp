package com.example.portalnexus.ui.employees;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.example.portalnexus.R;
import com.example.portalnexus.data.model.Employee;
import com.example.portalnexus.databinding.ActivityEmployeeFormBinding;
import com.example.portalnexus.utils.CurrencyTextWatcher;
import com.example.portalnexus.utils.DialogHelper;
import com.example.portalnexus.utils.ImageUtils;
import com.example.portalnexus.utils.PermissionHelper;
import com.example.portalnexus.viewmodel.EmployeeViewModel;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EmployeeFormActivity extends AppCompatActivity {

    private static final String TAG = "EmployeeForm";
    private ActivityEmployeeFormBinding binding;
    private EmployeeViewModel viewModel;
    private Employee employee;
    
    private Uri photoUri;
    private String currentPhotoUri;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    dispatchTakePictureIntent();
                } else {
                    if (!PermissionHelper.shouldShowCameraRationale(this)) {
                        DialogHelper.showPermissionSettingsDialog(this,
                                "Câmera Desativada",
                                "Habilite a câmera nas configurações para adicionar uma foto ao funcionário.");
                    } else {
                        Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    currentPhotoUri = photoUri.toString();
                    Log.d(TAG, "Captured photo URI: " + currentPhotoUri);
                    ImageUtils.loadImage(binding.imgEmployeeForm, currentPhotoUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityEmployeeFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, windowInsets) -> {
            androidx.core.graphics.Insets insets = windowInsets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, 0);
            binding.footerActions.setTranslationY(-insets.bottom);
            return windowInsets;
        });

        viewModel = new ViewModelProvider(this).get(EmployeeViewModel.class);

        employee = (Employee) getIntent().getSerializableExtra("employee");
        if (employee != null) {
            fillForm();
            binding.toolbar.setTitle(R.string.edit_employee);
            binding.btnDelete.setVisibility(View.VISIBLE);
        }

        setupListeners();
        setupObservers();
        setupTextWatchers();
    }

    private void setupObservers() {
        viewModel.isLoading.observe(this, isLoading -> {
            if (isLoading) {
                DialogHelper.showLoading(this, "Salvando...");
            } else {
                DialogHelper.hideLoading();
            }
        });

        viewModel.error.observe(this, error -> {
            if (error != null) {
                DialogHelper.showError(this, "Erro", error);
            }
        });

        viewModel.operationSuccess.observe(this, success -> {
            if (success != null && success) {
                viewModel.resetOperationSuccess();
                finish();
            }
        });
    }

    private void fillForm() {
        binding.editName.setText(employee.getName());
        binding.editPosition.setText(employee.getPosition());
        binding.editEmail.setText(employee.getEmail());
        
        String formattedSalary = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(employee.getSalary());
        binding.editSalary.setText(formattedSalary);
        
        binding.switchActive.setChecked(employee.isActive());
        
        currentPhotoUri = employee.getPhoto();
        if (currentPhotoUri != null && !currentPhotoUri.isEmpty()) {
            ImageUtils.loadImage(binding.imgEmployeeForm, currentPhotoUri);
        } else {
            binding.imgEmployeeForm.setImageResource(R.drawable.iconapp);
        }
    }

    private void setupListeners() {
        binding.btnSave.setOnClickListener(v -> saveEmployee());
        binding.btnDelete.setOnClickListener(v -> confirmDelete());
        binding.btnChangeEmployeePhoto.setOnClickListener(v -> checkCameraPermission());
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTextWatchers() {
        binding.editSalary.addTextChangedListener(new CurrencyTextWatcher(binding.editSalary));

        binding.editName.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.inputName.setError(null);
            }
        });
        binding.editPosition.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.inputPosition.setError(null);
            }
        });
        binding.editEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.inputEmail.setError(null);
            }
        });
        binding.editSalary.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.inputSalary.setError(null);
            }
        });
    }

    private void checkCameraPermission() {
        if (PermissionHelper.hasCameraPermission(this)) {
            dispatchTakePictureIntent();
        } else {
            if (PermissionHelper.shouldShowCameraRationale(this)) {
                DialogHelper.showCustomDialog(this,
                        R.drawable.iconapp,
                        "Acesso à Câmera",
                        "Para adicionar uma foto ao perfil do funcionário, o Portal Nexus precisa acessar sua câmera.",
                        "Continuar",
                        () -> requestPermissionLauncher.launch(Manifest.permission.CAMERA));
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Toast.makeText(this, "Erro ao criar arquivo de imagem", Toast.LENGTH_SHORT).show();
        }
        
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            cameraLauncher.launch(takePictureIntent);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_EMP_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void confirmDelete() {
        String title = getString(R.string.delete) + " " + getString(R.string.employees);
        String message = getString(R.string.delete) + " " + (employee != null ? employee.getName() : "") + "?";
        DialogHelper.showDeleteConfirmation(this, title, message, () -> {
            if (employee != null) viewModel.deleteEmployee(employee.getId());
        });
    }

    private boolean validate() {
        boolean isValid = true;
        
        String name = binding.editName.getText().toString().trim();
        if (name.isEmpty()) {
            binding.inputName.setError(getString(R.string.name_required));
            isValid = false;
        }

        String position = binding.editPosition.getText().toString().trim();
        if (position.isEmpty()) {
            binding.inputPosition.setError(getString(R.string.position_required));
            isValid = false;
        }

        String email = binding.editEmail.getText().toString().trim();
        if (email.isEmpty()) {
            binding.inputEmail.setError(getString(R.string.email_required));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.setError(getString(R.string.invalid_email));
            isValid = false;
        }

        String salaryStr = binding.editSalary.getText().toString().trim();
        if (salaryStr.isEmpty()) {
            binding.inputSalary.setError(getString(R.string.salary_required));
            isValid = false;
        }

        return isValid;
    }

    private void saveEmployee() {
        if (!validate()) return;

        String name = binding.editName.getText().toString().trim();
        String position = binding.editPosition.getText().toString().trim();
        String email = binding.editEmail.getText().toString().trim();
        double salary = CurrencyTextWatcher.parseCurrencyValue(binding.editSalary.getText().toString());
        boolean active = binding.switchActive.isChecked();

        Log.d(TAG, "Saving employee. Photo URI: " + currentPhotoUri);

        if (employee == null) {
            Employee newEmployee = new Employee(0, name, position, email, salary, active, currentPhotoUri);
            viewModel.addEmployee(newEmployee);
        } else {
            employee.setName(name);
            employee.setPosition(position);
            employee.setEmail(email);
            employee.setSalary(salary);
            employee.setActive(active);
            employee.setPhoto(currentPhotoUri);
            viewModel.updateEmployee(employee);
        }
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}
