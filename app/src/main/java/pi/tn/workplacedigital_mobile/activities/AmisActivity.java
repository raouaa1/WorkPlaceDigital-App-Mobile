package pi.tn.workplacedigital_mobile.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.adapters.AmisAdapter;
import pi.tn.workplacedigital_mobile.api.ApiClient;
import pi.tn.workplacedigital_mobile.api.ApiService;
import pi.tn.workplacedigital_mobile.model.ListItem;
import pi.tn.workplacedigital_mobile.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activité affichant la liste des utilisateurs en 2 sections :
 * - Amis actuels (avec bouton Supprimer)
 * - Non-amis (avec bouton Ajouter)
 */
public class AmisActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAmis;
    private AmisAdapter amisAdapter;
    private List<ListItem> itemList = new ArrayList<>();

    // TODO: Récupérer dynamiquement l'ID de l'utilisateur connecté (par ex. via SharedPreferences)
    private long currentUserId = 1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amis);

        // Setup Toolbar avec flèche retour personnalisée
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerViewAmis = findViewById(R.id.recyclerViewAmis);
        recyclerViewAmis.setLayoutManager(new LinearLayoutManager(this));

        // Init API Retrofit
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Créer adapter avec listener pour gérer Ajouter/Supprimer ami
        amisAdapter = new AmisAdapter(this, itemList, currentUserId, new AmisAdapter.OnAmiActionListener() {
            @Override
            public void onAddAmi(User user) {
                apiService.addAmi(currentUserId, user.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AmisActivity.this, "Ami ajouté avec succès", Toast.LENGTH_SHORT).show();
                            chargerAmis(apiService);
                        } else {
                            Toast.makeText(AmisActivity.this, "Erreur lors de l'ajout de l'ami", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(AmisActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onRemoveAmi(User user) {
                apiService.removeAmi(currentUserId, user.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AmisActivity.this, "Ami supprimé avec succès", Toast.LENGTH_SHORT).show();
                            chargerAmis(apiService);
                        } else {
                            Toast.makeText(AmisActivity.this, "Erreur lors de la suppression de l'ami", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(AmisActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        recyclerViewAmis.setAdapter(amisAdapter);

        // Charger la liste au lancement
        chargerAmis(apiService);
    }

    /**
     * Charge les utilisateurs depuis l'API, les classe en amis et non-amis,
     * puis met à jour la liste affichée dans le RecyclerView.
     */
    private void chargerAmis(ApiService apiService) {
        Call<List<User>> call = apiService.getAllUsersWithAmiStatus(currentUserId);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    itemList.clear();

                    List<User> amis = new ArrayList<>();
                    List<User> nonAmis = new ArrayList<>();

                    for (User u : response.body()) {
                        if (u.getId() == currentUserId) continue; // Ignore current user
                        if (u.isEstAmi()) {
                            amis.add(u);
                        } else {
                            nonAmis.add(u);
                        }
                    }

                    // Section Amis (avec bouton Supprimer)
                    if (!amis.isEmpty()) {
                        itemList.add(new ListItem(ListItem.TYPE_TITLE, "Liste des amis 👥", null));
                        for (User ami : amis) {
                            itemList.add(new ListItem(ListItem.TYPE_USER, null, ami, true));
                        }
                        Log.d("AmisActivity", "Amis chargés: " + amis.size());
                    }

                    // Section Non-amis (avec bouton Ajouter)
                    if (!nonAmis.isEmpty()) {
                        itemList.add(new ListItem(ListItem.TYPE_TITLE, "Souhaitez-vous les ajouter comme amis ?", null));
                        for (User nonAmi : nonAmis) {
                            itemList.add(new ListItem(ListItem.TYPE_USER, null, nonAmi, false));
                        }
                    }

                    amisAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AmisActivity.this, "Erreur récupération utilisateurs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(AmisActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
