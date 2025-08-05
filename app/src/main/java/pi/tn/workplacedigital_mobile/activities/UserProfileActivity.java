package pi.tn.workplacedigital_mobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.api.UserProfileApiClient;
import pi.tn.workplacedigital_mobile.api.UserProfileApiService;
import pi.tn.workplacedigital_mobile.model.UserProfile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    // Déclaration des composants UI
    private ImageView profileImageView, backArrow;
    private EditText usernameEdit, emailEdit, bioEdit;
    private Button saveButton, deleteButton;

    // URI de l’image sélectionnée
    private Uri selectedImageUri;

    // Exemple d'ID utilisateur (à adapter selon ta logique)
    private Long userId = 10L;

    // Service Retrofit
    private UserProfileApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // 📌 Initialisation des vues
        profileImageView = findViewById(R.id.profileImageView);
        usernameEdit = findViewById(R.id.usernameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        bioEdit = findViewById(R.id.bioEdit);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        backArrow = findViewById(R.id.backArrow);

        // 📌 Initialisation de Retrofit
        apiService = UserProfileApiClient.getClient().create(UserProfileApiService.class);

        // 📌 Chargement du profil utilisateur dès l'ouverture
        loadUserProfile();

        // 📌 Clic sur l'image → choisir image depuis galerie
        profileImageView.setOnClickListener(v -> pickImage());

        // 📌 Clic sur enregistrer → confirmer avant update
        saveButton.setOnClickListener(v -> confirmUpdate());

        // 📌 Clic sur supprimer → confirmer avant suppression
        deleteButton.setOnClickListener(v -> confirmDelete());

        // 📌 Clic sur flèche retour
        backArrow.setOnClickListener(v -> onBackPressed());
    }

    /**
     * 📌 Récupère le profil utilisateur via l'API et remplit les champs.
     */
    private void loadUserProfile() {
        apiService.getUser(userId).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile user = response.body();

                    // Remplir les champs texte
                    usernameEdit.setText(user.getUsername());
                    emailEdit.setText(user.getEmail());
                    bioEdit.setText(user.getBio());

                    // Charger l’image si elle existe
                    if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
                        String imageUrl = user.getImageUrl();

                        // Remplace localhost par IP locale
                        if (imageUrl.contains("localhost")) {
                            imageUrl = imageUrl.replace("localhost", "172.19.3.153");
                        }

                        // Si URL relative → compléter
                        if (!(imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))) {
                            imageUrl = "http://172.19.3.153:8086/api/user-profiles/images/" + imageUrl;
                        }

                        // Glide pour afficher image
                        Glide.with(UserProfileActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.error_image)
                                .circleCrop() // 🔴 ici on ajoute ça
                                .into(profileImageView);
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, "Profil introuvable.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 📌 Ouvre la galerie pour choisir une image.
     */
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * 📌 Gestion du retour de sélection d'image.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            profileImageView.setImageURI(selectedImageUri);
        }
    }

    /**
     * 📌 Récupère le chemin réel du fichier image sélectionné.
     */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    /**
     * 📌 Affiche un dialogue de confirmation avant de mettre à jour.
     */
    private void confirmUpdate() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment modifier ce profil ?")
                .setPositiveButton("Oui", (dialog, which) -> updateUserProfile())
                .setNegativeButton("Non", null)
                .show();
    }

    /**
     * 📌 Met à jour le profil via l'API, avec image si modifiée.
     */
    private void updateUserProfile() {
        // Création des champs texte
        RequestBody usernamePart = RequestBody.create(MediaType.parse("text/plain"), usernameEdit.getText().toString());
        RequestBody emailPart = RequestBody.create(MediaType.parse("text/plain"), emailEdit.getText().toString());
        RequestBody bioPart = RequestBody.create(MediaType.parse("text/plain"), bioEdit.getText().toString());

        MultipartBody.Part imagePart = null;

        // Ajout image si sélectionnée
        if (selectedImageUri != null) {
            String filePath = getRealPathFromURI(selectedImageUri);
            if (filePath != null) {
                File file = new File(filePath);
                RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImageUri)), file);
                imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            }
        }

        // Appel Retrofit pour update
        apiService.updateUser(userId, usernamePart, emailPart, bioPart, imagePart).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserProfileActivity.this, "Profil mis à jour !", Toast.LENGTH_SHORT).show();
                    loadUserProfile(); // recharge les infos
                    selectedImageUri = null; // reset image sélectionnée
                } else {
                    Toast.makeText(UserProfileActivity.this, "Échec de mise à jour", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 📌 Affiche un dialogue de confirmation avant suppression.
     */
    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment supprimer ce profil ?")
                .setPositiveButton("Oui", (dialog, which) -> deleteUserProfile())
                .setNegativeButton("Non", null)
                .show();
    }

    /**
     * 📌 Supprime le profil utilisateur via l'API.
     */
    private void deleteUserProfile() {
        apiService.deleteUser(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(UserProfileActivity.this, "Profil supprimé.", Toast.LENGTH_SHORT).show();
                finish(); // quitte activité
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Erreur de suppression", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
