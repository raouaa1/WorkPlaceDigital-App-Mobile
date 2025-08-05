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
import pi.tn.workplacedigital_mobile.adapters.ActiviteAdapter;
import pi.tn.workplacedigital_mobile.api.ActiviteApi;
import pi.tn.workplacedigital_mobile.api.RetrofitActiviteClientInstance;
import pi.tn.workplacedigital_mobile.model.Activite;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiviteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ActiviteAdapter adapter;
    private List<Activite> activiteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activite);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Les activités de Ooredoo 🗓️");  // Titre avec emoji
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);          // Activer flèche retour
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp); // flèche personnalisée
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        recyclerView = findViewById(R.id.recyclerViewActivites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ActiviteAdapter(activiteList);
        recyclerView.setAdapter(adapter);

        fetchActivites();
    }

    private void fetchActivites() {
        ActiviteApi api = RetrofitActiviteClientInstance.getRetrofitInstance().create(ActiviteApi.class);
        api.getAllActivites().enqueue(new Callback<List<Activite>>() {
            @Override
            public void onResponse(Call<List<Activite>> call, Response<List<Activite>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activiteList.clear();
                    activiteList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ActiviteActivity.this, "Erreur chargement activités", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Activite>> call, Throwable t) {
                Toast.makeText(ActiviteActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
