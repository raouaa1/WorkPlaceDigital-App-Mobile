package pi.tn.workplacedigital_mobile.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.adapters.PublicationAdapter;
import pi.tn.workplacedigital_mobile.api.CommentaireApi;
import pi.tn.workplacedigital_mobile.api.PublicationApi;
import pi.tn.workplacedigital_mobile.api.RetrofitClientCommentaire;
import pi.tn.workplacedigital_mobile.api.RetrofitClientInstance;
import pi.tn.workplacedigital_mobile.model.Commentaire;
import pi.tn.workplacedigital_mobile.models.Publication;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    // Constantes pour le code des requêtes fichiers et permission
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    // Composants UI
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView welcomeText;

    private EditText editTextPublication;
    private Button btnUploadImage, btnUploadVideo, btnPublish;
    private TextView textMessage;
    private RecyclerView recyclerViewPublications;
    private PublicationAdapter adapter;
    private List<Publication> publications = new ArrayList<>();

    // Uri sélectionnés pour image et vidéo
    private Uri selectedImageUri = null;
    private Uri selectedVideoUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialisation des vues depuis le layout XML
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        welcomeText = findViewById(R.id.welcomeText);
        editTextPublication = findViewById(R.id.editTextPublication);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnUploadVideo = findViewById(R.id.btnUploadVideo);
        btnPublish = findViewById(R.id.btnPublish);
        textMessage = findViewById(R.id.textMessage);
        recyclerViewPublications = findViewById(R.id.recyclerViewPublications);

        // Mise en place de la Toolbar en tant que barre d'action
        setSupportActionBar(toolbar);

        // Configuration du Drawer toggle (bouton hamburger)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Récupération du prénom passé en extra et affichage message de bienvenue
        String firstname = getIntent().getStringExtra("firstname");
        welcomeText.setText(firstname != null ? "Bienvenue " + firstname + " dans WorkPlace Digital" :
                "Bienvenue sur WorkPlace Digital Mobile 🤳");

        // Gestion du menu navigation drawer
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();

            if (id == R.id.nav_logout) {
                startActivity(new Intent(this, LoginActivity.class)); // Déconnexion
                finish();
            } else if (id == R.id.nav_chatbot) {
                startActivity(new Intent(this, ChatActivity.class)); // ChatBot
            } else if (id == R.id.nav_actualites) {
                // ✅ Recharge l’activité DashboardActivity
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            } else if (id == R.id.nav_profile) {
                // ✅ Ouvre UserProfileActivity
                Intent profileIntent = new Intent(this, UserProfileActivity.class);
                startActivity(profileIntent);
            }
            // ✅ Ouvre Evenement
            else if (id == R.id.nav_evenements) {
                startActivity(new Intent(this, EvenementActivity.class));
            }

            // ✅ Ouvre Collaborateur
            else if (id == R.id.nav_collaborateur) {
                startActivity(new Intent(this, CollaborateurActivity.class));
            }

            // ✅ Ouvre Signalement
            else if (id == R.id.nav_signaler) {
                startActivity(new Intent(this, SignalementActivity.class));
            }

            // ✅ Ouvre Activites
            else if (id == R.id.nav_activites) {
                startActivity(new Intent(this, ActiviteActivity.class));
            }


            // ✅ Ouvre Amis
            else if (id == R.id.nav_friends) {
                startActivity(new Intent(this, AmisActivity.class));
            }


            drawerLayout.closeDrawers();
            return true;
        });


        // Initialisation du RecyclerView avec un layout manager vertical
        recyclerViewPublications.setLayoutManager(new LinearLayoutManager(this));

        // 📌 Gestion du clic sur la bulle Chatbot pour ouvrir la ChatActivity
        LinearLayout bubbleMessage = findViewById(R.id.bubbleMessage);
        bubbleMessage.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        // 📌 Gestion du clic sur la bulle Message pour ouvrir la DisccusionActivity
        LinearLayout bubbleChat = findViewById(R.id.bubbleChat);
        bubbleChat.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, DiscussionActivity.class);
            startActivity(intent);
        });


        // 📌 Gestion du clic sur la bulle Notification pour ouvrir la Notification Activity
        LinearLayout bubbleNotification = findViewById(R.id.bubbleNotification);
        bubbleNotification.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        // 📌 Gestion du clic sur la bulle Profil pour ouvrir la UserProfil Activity
        LinearLayout bubbleProfile = findViewById(R.id.bubbleProfile);
        bubbleProfile.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });











        // Création de l'adapter avec gestion des actions (like, delete, edit)
        adapter = new PublicationAdapter(this, publications, new PublicationAdapter.OnPublicationActionListener() {
            @Override
            public void onLike(Long publicationId, int position) {
                likePublication(publicationId, position);
            }

            @Override
            public void onDelete(Long publicationId, int position) {
                // Confirmation avant suppression
                new AlertDialog.Builder(DashboardActivity.this)
                        .setTitle("Confirmation")
                        .setMessage("Voulez-vous vraiment supprimer cette publication ?")
                        .setPositiveButton("Oui", (dialog, which) -> deletePublication(publicationId))
                        .setNegativeButton("Non", null)
                        .show();
            }

            @Override
            public void onEdit(Publication publication, int position) {
                startEditingPublication(publication);
            }
        });
        recyclerViewPublications.setAdapter(adapter);

        // Boutons pour choisir image ou vidéo
        btnUploadImage.setOnClickListener(v -> openFileChooser(PICK_IMAGE_REQUEST));
        btnUploadVideo.setOnClickListener(v -> openFileChooser(PICK_VIDEO_REQUEST));

        // Bouton publier : vérifie la permission et crée la publication
        btnPublish.setOnClickListener(v -> checkPermissionsAndCreatePublication());

        // Chargement initial des publications via API
        loadPublicationsFromApi();
    }

    /**
     * Vérifie la permission READ_EXTERNAL_STORAGE avant création de publication avec média.
     * Si permission refusée, demande la permission à l'utilisateur.
     */
    private void checkPermissionsAndCreatePublication() {
        if (selectedImageUri != null || selectedVideoUri != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                // Permission déjà accordée
                createPublication();
            }
        } else {
            // Pas de fichier à charger, on peut créer la publication directement
            createPublication();
        }
    }

    /**
     * Résultat de la demande de permission utilisateur.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Si permission accordée, créer la publication
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createPublication();
            } else {
                Toast.makeText(this, "Permission de lecture du stockage refusée.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Ouvre le sélecteur de fichiers (image ou vidéo selon requestCode).
     */
    private void openFileChooser(int requestCode) {
        Intent intent = new Intent();
        intent.setType(requestCode == PICK_IMAGE_REQUEST ? "image/*" : "video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Sélectionner un fichier"), requestCode);
    }

    /**
     * Récupère le résultat du sélecteur de fichiers, stocke l'uri sélectionné.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                selectedImageUri = data.getData();
                Toast.makeText(this, "Image sélectionnée", Toast.LENGTH_SHORT).show();
            } else if (requestCode == PICK_VIDEO_REQUEST) {
                selectedVideoUri = data.getData();
                Toast.makeText(this, "Vidéo sélectionnée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Charge les publications depuis l'API et met à jour la liste.
     */
    private void loadPublicationsFromApi() {
        PublicationApi api = RetrofitClientInstance.getRetrofitInstance().create(PublicationApi.class);
        api.getAllPublications().enqueue(new Callback<List<Publication>>() {
            @Override
            public void onResponse(Call<List<Publication>> call, Response<List<Publication>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    publications.clear();
                    publications.addAll(response.body());

                    // Formatte la date pour l'affichage
                    for (Publication p : publications) {
                        p.setDatePublication(formatDateTime(p.getDatePublication()));
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(DashboardActivity.this, "Erreur chargement publications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Publication>> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Crée une publication texte + optionnellement image et/ou vidéo.
     */
    private void createPublication() {
        String contenu = editTextPublication.getText().toString().trim();

        // Vérification contenu / média obligatoire
        if (contenu.isEmpty() && selectedImageUri == null && selectedVideoUri == null) {
            textMessage.setText("Veuillez saisir un contenu texte, ou sélectionner une image, ou une vidéo.");
            textMessage.setVisibility(View.VISIBLE);
            return;
        }
        textMessage.setVisibility(View.GONE);

        // Prépare les parties du formulaire
        RequestBody contenuPart = RequestBody.create(MediaType.parse("text/plain"), contenu.isEmpty() ? "" : contenu);
        RequestBody auteurIdPart = RequestBody.create(MediaType.parse("text/plain"), "1"); // à adapter selon ton contexte

        MultipartBody.Part imagePart = null;
        MultipartBody.Part videoPart = null;

        // Prépare les fichiers média si sélectionnés
        if (selectedImageUri != null) {
            imagePart = prepareFilePart("image", selectedImageUri);
        }
        if (selectedVideoUri != null) {
            videoPart = prepareFilePart("video", selectedVideoUri);
        }

        // Appel API Retrofit pour création de publication
        PublicationApi api = RetrofitClientInstance.getRetrofitInstance().create(PublicationApi.class);
        api.createPublicationWithMedia(contenuPart, auteurIdPart, imagePart, videoPart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DashboardActivity.this, "Publication créée", Toast.LENGTH_SHORT).show();

                    // Reset UI
                    editTextPublication.setText("");
                    selectedImageUri = null;
                    selectedVideoUri = null;
                    loadPublicationsFromApi();
                } else {
                    Toast.makeText(DashboardActivity.this, "Erreur création publication, code: " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e("API_ERROR", "Code: " + response.code() + " Msg: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_FAILURE", t.toString());
            }
        });
    }

    /**
     * Prépare un MultipartBody.Part à partir d’un Uri.
     * Lit le fichier en bytes, récupère le mime type et le vrai nom de fichier.
     */
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        try {
            // Ouvre InputStream pour lire le contenu du fichier
            InputStream inputStream = getContentResolver().openInputStream(fileUri);

            // Lit tout le contenu dans un tableau de bytes
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            // Récupère le type MIME (ex: image/png ou video/mp4)
            String mimeType = getContentResolver().getType(fileUri);
            if (mimeType == null) mimeType = "application/octet-stream";

            // Récupère le nom de fichier réel pour éviter erreur 400
            String fileName = getFileName(fileUri);
            if (fileName == null) fileName = "uploadfile";

            // Crée RequestBody à partir du contenu binaire
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), bytes);

            // Crée MultipartBody.Part avec nom correct
            return MultipartBody.Part.createFormData(partName, fileName, requestFile);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Récupère le vrai nom de fichier depuis un Uri content:// ou file://
     */
    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            // Pour un scheme "file", récupère le dernier segment du chemin
            String path = uri.getPath();
            if (path != null) {
                int cut = path.lastIndexOf('/');
                if (cut != -1) {
                    result = path.substring(cut + 1);
                } else {
                    result = path;
                }
            }
        }
        return result;
    }

    /**
     * Like une publication et met à jour le compteur localement dans la liste.
     */
    private void likePublication(Long publicationId, int position) {
        Log.d("LIKE", "Like publication " + publicationId + " par user 1");

        PublicationApi api = RetrofitClientInstance.getRetrofitInstance().create(PublicationApi.class);

        api.likePublication(publicationId, 1L).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Publication pub = publications.get(position);
                    pub.setNbLikes(pub.getNbLikes() + 1);
                    adapter.notifyItemChanged(position);
                    Toast.makeText(DashboardActivity.this, "Vous avez aimé la publication", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DashboardActivity.this, "Erreur lors du like : " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Supprime une publication via API et recharge la liste.
     */
    private void deletePublication(Long publicationId) {
        PublicationApi api = RetrofitClientInstance.getRetrofitInstance().create(PublicationApi.class);
        api.deletePublication(publicationId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loadPublicationsFromApi();
                Toast.makeText(DashboardActivity.this, "Publication supprimée", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Démarre la modification d'une publication avec boîte de dialogue.
     */
    private void startEditingPublication(Publication publication) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier publication");

        // Champ texte pré-rempli avec le contenu actuel
        final EditText input = new EditText(this);
        input.setText(publication.getContenu());
        builder.setView(input);

        // Bouton "Enregistrer" avec confirmation
        builder.setPositiveButton("Enregistrer", (dialog, which) -> {
            new AlertDialog.Builder(DashboardActivity.this)
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous vraiment modifier cette publication ?")
                    .setPositiveButton("Oui", (confirmDialog, confirmWhich) -> {
                        String nouveauContenu = input.getText().toString();
                        PublicationApi api = RetrofitClientInstance.getRetrofitInstance().create(PublicationApi.class);
                        RequestBody contenuPart = RequestBody.create(MediaType.parse("text/plain"), nouveauContenu);

                        // Ici on ne modifie pas l'image/vidéo (null)
                        api.updatePublication(publication.getId(), contenuPart, null, null)
                                .enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        loadPublicationsFromApi();
                                        Toast.makeText(DashboardActivity.this, "Publication modifiée", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(DashboardActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });

        // Bouton annuler
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Formatte une date ISO en une chaîne plus lisible.
     * Ex: "2025-07-08T19:30:00" => "08 Jul 2025, 19:30"
     */
    private String formatDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";

        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = isoFormat.parse(dateStr);

            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");
            return displayFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr;
        }
    }


    private void chargerCommentairesDePublication(int publicationId) {
        CommentaireApi commentaireApi = RetrofitClientCommentaire.getClient().create(CommentaireApi.class);

        commentaireApi.getCommentairesParPublication(publicationId).enqueue(new Callback<List<Commentaire>>() {
            @Override
            public void onResponse(Call<List<Commentaire>> call, Response<List<Commentaire>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Commentaire> commentaires = response.body();
                    Toast.makeText(DashboardActivity.this, "Commentaires chargés: " + commentaires.size(), Toast.LENGTH_SHORT).show();

                    // Affiche les commentaires dans le Logcat (exemple)
                    for (Commentaire c : commentaires) {
                        Log.d("COMMENTAIRE", "Contenu: " + c.getContenu());
                    }

                    // Ici tu peux ajouter du code pour afficher les commentaires dans un dialog ou une nouvelle activité
                } else {
                    Toast.makeText(DashboardActivity.this, "Erreur lors du chargement des commentaires", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Commentaire>> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
