package pi.tn.workplacedigital_mobile.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.adapters.CommentairesAdapter;
import pi.tn.workplacedigital_mobile.api.CommentaireApi;
import pi.tn.workplacedigital_mobile.api.RetrofitClientCommentaire;
import pi.tn.workplacedigital_mobile.model.Commentaire;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentairesActivity extends AppCompatActivity implements CommentairesAdapter.OnCommentaireInteractionListener {

    private RecyclerView recyclerCommentaires;
    private EditText editNouveauCommentaire;
    private Button btnPublier;

    private CommentairesAdapter adapter;
    private List<Commentaire> commentaires = new ArrayList<>();

    private int publicationId = 78; // Exemple ID publication
    private int userId = 1; // Simulé utilisateur connecté

    private CommentaireApi commentaireApi; // Retrofit API interface

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentaires);

        recyclerCommentaires = findViewById(R.id.recyclerCommentaires);
        editNouveauCommentaire = findViewById(R.id.editNouveauCommentaire);
        btnPublier = findViewById(R.id.btnPublier);

        commentaires = new ArrayList<>();
        adapter = new CommentairesAdapter(commentaires, this);
        recyclerCommentaires.setLayoutManager(new LinearLayoutManager(this));
        recyclerCommentaires.setAdapter(adapter);

        // Récupérer d'abord l'ID
        long publicationId = getIntent().getLongExtra("publicationId", -1);

        if (publicationId == -1) {
            Toast.makeText(this, "Publication ID invalide", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Maintenant qu'on a l'ID, on peut appeler Retrofit
        commentaireApi = RetrofitClientCommentaire.getClient().create(CommentaireApi.class);

        // ✅ Charger les commentaires après avoir l’ID
        chargerCommentaires();

        btnPublier.setOnClickListener(v -> ajouterCommentaire());
    }


    private void chargerCommentaires() {
        commentaireApi.getCommentairesParPublication(publicationId).enqueue(new Callback<List<Commentaire>>() {
            @Override
            public void onResponse(Call<List<Commentaire>> call, Response<List<Commentaire>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    commentaires.clear();
                    commentaires.addAll(response.body());
                    adapter.updateCommentaires(commentaires);
                }
            }

            @Override
            public void onFailure(Call<List<Commentaire>> call, Throwable t) {
                Toast.makeText(CommentairesActivity.this, "Erreur chargement commentaires", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ajouterCommentaire() {
        String contenu = editNouveauCommentaire.getText().toString().trim();
        if (contenu.isEmpty()) {
            Toast.makeText(this, "⚠️ Le commentaire ne peut pas être vide.", Toast.LENGTH_SHORT).show();
            return;
        }

        Commentaire nouveauComm = new Commentaire();
        nouveauComm.setId(0);
        nouveauComm.setUserId(userId);
        nouveauComm.setPublicationId(publicationId);
        nouveauComm.setContenu(contenu);
        nouveauComm.setDateCreation(new Date().toString()); // ou format ISO

        commentaireApi.ajouterCommentaire(nouveauComm).enqueue(new Callback<Commentaire>() {
            @Override
            public void onResponse(Call<Commentaire> call, Response<Commentaire> response) {
                if (response.isSuccessful() && response.body() != null) {
                    commentaires.add(response.body());
                    adapter.updateCommentaires(commentaires);
                    editNouveauCommentaire.setText("");
                }
            }

            @Override
            public void onFailure(Call<Commentaire> call, Throwable t) {
                Toast.makeText(CommentairesActivity.this, "Erreur ajout commentaire", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onModifier(Commentaire commentaire) {
        // Pas besoin ici car gestion dans adapter (affiche les champs édition)
    }

    @Override
    public void onSupprimer(Commentaire commentaire) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Êtes-vous sûr de vouloir supprimer ce commentaire ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    commentaireApi.supprimerCommentaire(commentaire.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                commentaires.remove(commentaire);
                                adapter.updateCommentaires(commentaires);
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(CommentairesActivity.this, "Erreur suppression commentaire", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Non", null)
                .show();
    }

    @Override
    public void onEnregistrer(Commentaire commentaire, String nouveauContenu) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment enregistrer cette modification ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    commentaire.setContenu(nouveauContenu);
                    commentaire.setDateCreation(new Date().toString());

                    commentaireApi.modifierCommentaire(commentaire.getId(), commentaire).enqueue(new Callback<Commentaire>() {
                        @Override
                        public void onResponse(Call<Commentaire> call, Response<Commentaire> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                int idx = commentaires.indexOf(commentaire);
                                commentaires.set(idx, response.body());
                                adapter.updateCommentaires(commentaires);
                            }
                        }

                        @Override
                        public void onFailure(Call<Commentaire> call, Throwable t) {
                            Toast.makeText(CommentairesActivity.this, "Erreur modification commentaire", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Non", null)
                .show();
    }

    @Override
    public void onAnnuler() {
        // Optionnel, ici rien de spécial à faire
    }



}
