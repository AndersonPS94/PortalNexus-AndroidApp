package com.example.portalnexus.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.portalnexus.R;
import com.example.portalnexus.data.model.Character;
import com.example.portalnexus.data.repository.CharacterRepository;
import com.example.portalnexus.databinding.ActivityProfileBinding;
import com.example.portalnexus.utils.DialogHelper;
import com.example.portalnexus.utils.ImageUtils;
import com.example.portalnexus.utils.PermissionHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private Character character;
    private Uri photoUri;
    private CharacterRepository repository;
    private String lastCapturedUri;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    dispatchTakePictureIntent();
                } else {
                    if (!PermissionHelper.shouldShowCameraRationale(this)) {
                        DialogHelper.showPermissionSettingsDialog(this,
                                "Câmera Desativada",
                                "Você negou o acesso à câmera. Por favor, habilite nas configurações para registrar personagens.");
                    } else {
                        Toast.makeText(this, "Permissão de câmera negada", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    binding.imgProfile.setImageURI(photoUri);
                    lastCapturedUri = photoUri.toString();
                    repository.saveCapturedPhoto(character.getId(), lastCapturedUri);
                    binding.btnSendCapture.setVisibility(View.VISIBLE);
                    sendSimulatedPost(lastCapturedUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = CharacterRepository.getInstance();
        character = (Character) getIntent().getSerializableExtra("character");
        if (character == null) {
            finish();
            return;
        }

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        binding.collapsingToolbar.setTitle(character.getName() != null ? character.getName() : "Character");
        ImageUtils.loadImage(binding.imgProfile, character.getImage());

        binding.txtProfileName.setText(character.getName() != null ? character.getName() : "");
        
        StringBuilder details = new StringBuilder();
        details.append("Status: ").append(character.getStatus() != null ? character.getStatus() : "Unknown").append("\n");
        details.append("Espécie: ").append(character.getSpecies() != null ? character.getSpecies() : "Unknown").append("\n");
        details.append("Gênero: ").append(character.getGender() != null ? character.getGender() : "Unknown").append("\n");
        
        String originName = (character.getOrigin() != null && character.getOrigin().getName() != null) 
                ? character.getOrigin().getName() : "Unknown";
        details.append("Origem: ").append(originName).append("\n");
        
        String locationName = (character.getLocation() != null && character.getLocation().getName() != null) 
                ? character.getLocation().getName() : "Unknown";
        details.append("Localização: ").append(locationName).append("\n");
        
        details.append("URL: ").append(character.getUrl() != null ? character.getUrl() : "N/A");
        binding.txtProfileDetails.setText(details.toString());

        StringBuilder advanced = new StringBuilder();
        advanced.append("ID: ").append(character.getId()).append("\n");
        advanced.append("Tipo: ").append(character.getType() != null && !character.getType().isEmpty() ? character.getType() : "N/A").append("\n");
        advanced.append("Episódios: ").append(character.getEpisode() != null ? character.getEpisode().size() : 0).append("\n");
        advanced.append("Criado em: ").append(character.getCreated() != null ? character.getCreated() : "Unknown");
        binding.txtAdvancedInfo.setText(advanced.toString());
    }

    private void setupListeners() {
        binding.btnChangePhoto.setOnClickListener(v -> checkCameraPermission());
        binding.btnShare.setOnClickListener(v -> shareCharacter());
        binding.btnSendCapture.setOnClickListener(v -> {
            if (lastCapturedUri != null) sendSimulatedPost(lastCapturedUri);
        });
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void shareCharacter() {
        String shareBody = "Confira este personagem do Portal Nexus: " + character.getName() +
                "\nEspécie: " + character.getSpecies() +
                "\nStatus: " + character.getStatus();
        
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Personagem Portal Nexus");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Compartilhar via"));
    }

    private void checkCameraPermission() {
        if (PermissionHelper.hasCameraPermission(this)) {
            dispatchTakePictureIntent();
        } else {
            if (PermissionHelper.shouldShowCameraRationale(this)) {
                DialogHelper.showCustomDialog(this,
                        R.drawable.iconapp,
                        "Acesso à Câmera",
                        "Para registrar capturas dos personagens e atualizar o perfil, o Portal Nexus precisa acessar sua câmera.",
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
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void sendSimulatedPost(String uri) {
        repository.simulatePost(character, uri, new CharacterRepository.PostCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("ProfileActivity", "POST simulado com sucesso: " + result);
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, 
                        "Captura enviada com sucesso ao Nexo!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(String message) {
                Log.e("ProfileActivity", "Erro no POST simulado: " + message);
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, 
                        "Falha ao sincronizar captura.", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
