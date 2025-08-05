package pi.tn.workplacedigital_mobile.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pi.tn.workplacedigital_mobile.R;
import pi.tn.workplacedigital_mobile.adapters.CollaborateurAdapter;
import pi.tn.workplacedigital_mobile.api.CollaborateurApi;
import pi.tn.workplacedigital_mobile.api.RetrofitCollaborateurClientInstance;
import pi.tn.workplacedigital_mobile.model.Collaborateur;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activité affichant la liste des collaborateurs récupérés via Retrofit.
 */
public class CollaborateurActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CollaborateurAdapter adapter;
    private List<Collaborateur> collaborateurList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collaborateur);

        // Initialisation Toolbar avec titre et flèche retour
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // Active la flèche retour dans la Toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // Gestion du clic sur la flèche retour pour fermer l’activité
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewCollaborateurs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CollaborateurAdapter(collaborateurList);
        recyclerView.setAdapter(adapter);

        // Chargement des collaborateurs via API
        fetchCollaborateurs();
    }

    /**
     * Récupère la liste des collaborateurs via Retrofit et met à jour l'affichage.
     */
    private void fetchCollaborateurs() {
        CollaborateurApi api = RetrofitCollaborateurClientInstance
                .getRetrofitInstance()
                .create(CollaborateurApi.class);

        api.getAllCollaborateurs().enqueue(new Callback<List<Collaborateur>>() {
            @Override
            public void onResponse(Call<List<Collaborateur>> call, Response<List<Collaborateur>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    collaborateurList.clear();
                    collaborateurList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CollaborateurActivity.this,
                            "Erreur chargement collaborateurs",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Collaborateur>> call, Throwable t) {
                Toast.makeText(CollaborateurActivity.this,
                        "Erreur réseau : " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
